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

import java.util.*;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Long.MAX_VALUE;
import static java.util.stream.Collectors.toList;
import static org.cosinus.stream.reflection.ParametrizedClassPredicate.isParametrizedClass;
import static org.cosinus.stream.reflection.ReflectionStream.ancestorStream;

/**
 * Spliterator for flattening a tree of streams
 */
public class FlatStreamingSpliterator<S extends StreamSupplier<?>> extends AbstractSpliterator<S> {

    private final FlatStreamingStrategy strategy;

    private final Queue<S> streamersQueue;

    private final Function<StreamSupplier<S>, Stream<S>> streamSupplierHandler;

    private final Set<S> streamedAlready;

    public FlatStreamingSpliterator(
        final FlatStreamingStrategy strategy,
        final Stream<S> streamers) {
        this(strategy, streamers, StreamSupplier::stream);
    }

    public FlatStreamingSpliterator(
        final FlatStreamingStrategy strategy,
        final Stream<S> streamers,
        final Function<StreamSupplier<S>, Stream<S>> streamSupplierHandler) {

        super(MAX_VALUE, ORDERED | NONNULL);
        this.strategy = strategy;
        this.streamersQueue = strategy.isDepthFirst() ? new ConcurrentLinkedDeque<>() : new ConcurrentLinkedQueue<>();
        this.streamSupplierHandler = streamSupplierHandler;
        this.streamedAlready = new HashSet<>();
        streamers.forEach(this.streamersQueue::add);
    }

    @Override
    public boolean tryAdvance(Consumer<? super S> action) {
        S streamer = streamersQueue.peek();
        if (streamer == null) {
            return false;
        }

        boolean isMetaStreamSupplier = ancestorStream(streamer)
            .anyMatch(isParametrizedClass(StreamSupplier.class)
                .withGenericsExtending(StreamSupplier.class));

        boolean isMetaStreamSupplierButNotYetStreamed = isMetaStreamSupplier && !isStreamed(streamer);
        if (strategy.isParentFirst() || !isMetaStreamSupplierButNotYetStreamed) {
            action.accept(streamersQueue.poll());
        }

        if (isMetaStreamSupplierButNotYetStreamed) {
            try (Stream<? extends S> stream = streamSupplierHandler.apply((StreamSupplier<S>) streamer)) {
                pushInQueue(stream);
            }
            setStreamed(streamer);
        }

        return true;
    }

    protected void setStreamed(final S streamer) {
        streamedAlready.add(streamer);
    }

    protected boolean isStreamed(final S streamer) {
        return streamedAlready.contains(streamer);
    }

    protected void pushInQueue(Stream<? extends S> stream) {
        if (streamersQueue instanceof Deque<S> deque) {
            reverse(stream).forEach(deque::push);
        } else {
            stream.forEach(streamersQueue::add);
        }
    }

    protected Stream<? extends S> reverse(Stream<? extends S> stream) {
        List<? extends S> streamersList = stream.collect(toList());
        Collections.reverse(streamersList);
        return streamersList.stream();
    }
}
