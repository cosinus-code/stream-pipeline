/*
 * Copyright 2025 Cosinus Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cosinus.stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cosinus.stream.error.SkipPipelineConsumeException;

import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Long.MAX_VALUE;
import static org.cosinus.stream.Streams.reverseStream;
import static org.cosinus.stream.reflection.ParametrizedClassPredicate.isParametrizedClass;
import static org.cosinus.stream.reflection.ReflectionStream.ancestorStream;

/**
 * Spliterator for flattening a tree of streams
 *
 * @param <S> the type parameter
 */
public class FlatStreamingSpliterator<S extends StreamSupplier<?>> extends AbstractSpliterator<S> {

    private static final Logger LOG = LogManager.getLogger(FlatStreamingSpliterator.class);

    private final FlatStreamingStrategy flatStreamingStrategy;

    final StreamingStrategy streamingStrategy;

    private final Queue<S> streamersQueue;

    private final Function<StreamSupplier<S>, Stream<S>> streamSupplierHandler;

    private final Set<S> streamedAlready;

    /**
     * Constructor.
     *
     * @param flatStreamingStrategy flat streaming strategy
     * @param streamingStrategy     the streaming strategy
     * @param streams               the streams
     */
    public FlatStreamingSpliterator(
        final FlatStreamingStrategy flatStreamingStrategy,
        final StreamingStrategy streamingStrategy,
        final Stream<S> streams) {
        this(flatStreamingStrategy, streamingStrategy, streams, StreamSupplier::stream);
    }

    /**
     * Instantiates a new Flat streaming spliterator.
     *
     * @param flatStreamingStrategy the strategy
     * @param streamingStrategy     the streaming strategy
     * @param streamers             the streamers
     * @param streamSupplierHandler the stream supplier handler
     */
    public FlatStreamingSpliterator(
        final FlatStreamingStrategy flatStreamingStrategy,
        final StreamingStrategy streamingStrategy,
        final Stream<S> streamers,
        final Function<StreamSupplier<S>, Stream<S>> streamSupplierHandler) {

        super(MAX_VALUE, ORDERED | NONNULL);
        this.flatStreamingStrategy = flatStreamingStrategy;
        this.streamingStrategy = streamingStrategy;
        this.streamersQueue = flatStreamingStrategy.isDepthFirst() ?
            new ConcurrentLinkedDeque<>() :
            new ConcurrentLinkedQueue<>();
        this.streamSupplierHandler = streamSupplierHandler;
        this.streamedAlready = new HashSet<>();
        streamers.forEach(this.streamersQueue::add);
    }

    @Override
    public boolean tryAdvance(Consumer<? super S> action) {
        S streamSupplier = streamersQueue.peek();
        if (streamSupplier == null) {
            return false;
        }

        boolean isMetaStreamSupplier = ancestorStream(streamSupplier)
            .anyMatch(isParametrizedClass(StreamSupplier.class)
                .withGenericsExtending(StreamSupplier.class));

        boolean isMetaStreamSupplierButNotYetStreamed = isMetaStreamSupplier && !isStreamed(streamSupplier);
        if (flatStreamingStrategy.isParentFirst() || !isMetaStreamSupplierButNotYetStreamed) {
            action.accept(streamersQueue.poll());
        }

        if (isMetaStreamSupplierButNotYetStreamed) {
            try (Stream<? extends S> stream = getStream((StreamSupplier<S>) streamSupplier)) {
                pushInQueue(stream);
            } catch (SkipPipelineConsumeException skipPipelineConsumeException) {
                LOG.info("Stream flatting step skipped while streaming from: {}", streamSupplier);
            }
            setStreamed(streamSupplier);
        }

        return true;
    }

    /**
     * Get stream from supplier.
     *
     * @param streamSupplier the stream supplier
     * @return the supplied stream
     */
    protected Stream<? extends S> getStream(StreamSupplier<S> streamSupplier) {
        return getStream(streamSupplier, 0);
    }

    /**
     * Get stream from supplier.
     *
     * @param streamSupplier the stream supplier
     * @param retryCount     the current retry count
     * @return the supplied stream
     */
    protected Stream<? extends S> getStream(StreamSupplier<S> streamSupplier, int retryCount) {
        try {
            return streamSupplierHandler.apply(streamSupplier);
        } catch (SkipPipelineConsumeException skipPipelineConsumeException) {
            throw skipPipelineConsumeException;
        } catch (Exception ex) {
            if (streamingStrategy != null &&
                retryCount < streamingStrategy.getRetryMaxAttempts() &&
                streamingStrategy.shouldRetryOnFail(ex)) {

                return getStream(streamSupplier, ++retryCount);
            } else {
                throw ex;
            }
        }
    }

    /**
     * Sets a stream supplier as streamed.
     *
     * @param streamer the stream supplier to set as streamed
     */
    protected void setStreamed(final S streamer) {
        streamedAlready.add(streamer);
    }

    /**
     * Check if a stream supplier is streamed.
     *
     * @param streamer the stream supplier to check
     * @return true if the given stream supplier is already streamed, false otherwise
     */
    protected boolean isStreamed(final S streamer) {
        return streamedAlready.contains(streamer);
    }

    /**
     * Push a stream of stream suppliers to the queue.
     *
     * @param stream the stream of stream suppliers
     */
    protected void pushInQueue(Stream<? extends S> stream) {
        if (streamersQueue instanceof Deque<S> deque) {
            reverseStream(stream).forEach(deque::push);
        } else {
            stream.forEach(streamersQueue::add);
        }
    }
}
