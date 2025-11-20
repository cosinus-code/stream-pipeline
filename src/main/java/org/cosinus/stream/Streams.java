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

import org.cosinus.stream.page.PageSupplier;
import org.cosinus.stream.page.PagedSpliterator;
import org.cosinus.stream.swing.FlatSwingComponentsSpliterator;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;
import static org.cosinus.stream.StreamingStrategy.NO_STRATEGY;

/**
 * Streams utils
 */
public final class Streams {

    /**
     * Create a stream from an iterator.
     *
     * @param <T>            the type of elements in the stream
     * @param sourceIterator the source iterator
     * @return the stream
     */
    public static <T> Stream<T> stream(Iterator<T> sourceIterator) {
        return stream(sourceIterator, false);
    }

    /**
     * Create a stream from an iterator.
     *
     * @param <T>            the type of elements in the stream
     * @param sourceIterator the source iterator
     * @param parallel       true if the stream is parallel
     * @return the stream
     */
    public static <T> Stream<T> stream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    /**
     * Get the pages stream using a given page supplier.
     *
     * @param <T>          the type of the streamed items
     * @param pageSupplier the page supplier
     * @return the paged stream
     */
    public static <T> Stream<T> pagedStream(PageSupplier<T> pageSupplier) {
        return StreamSupport.stream(new PagedSpliterator<>(pageSupplier), false);
    }

    /**
     * Reverse a stream.
     *
     * @param <T>    the type parameter
     * @param stream the stream to reverse
     * @return the stream the reversed stream
     */
    public static <T> Stream<T> reverseStream(Stream<T> stream) {
        List<T> list = stream.collect(toList());
        reverse(list);
        return list.stream();
    }

    public static <T extends StreamSupplier<?>> Stream<T> flatStream(final FlatStreamingStrategy flatStreamingStrategy,
                                                                     final T... streamSuppliers) {
        return flatStream(flatStreamingStrategy, Arrays.stream(streamSuppliers));
    }

    public static <T extends StreamSupplier<?>> Stream<T> flatStream(final FlatStreamingStrategy flatStreamingStrategy,
                                                                     final Stream<T> streams) {
        return flatStream(flatStreamingStrategy, NO_STRATEGY, streams);
    }

    public static <T extends StreamSupplier<?>> Stream<T> flatStream(final FlatStreamingStrategy flatStreamingStrategy,
                                                                     final StreamingStrategy streamingStrategy,
                                                                     final Stream<T> streams) {
        return StreamSupport.stream(
            new FlatStreamingSpliterator<>(flatStreamingStrategy, streamingStrategy, streams), false);
    }

    public static Stream<Component> flatComponentsStream(Container container) {
        return StreamSupport.stream(new FlatSwingComponentsSpliterator(container), false);
    }

    /**
     * Private constructor
     */
    private Streams() {
    }
}
