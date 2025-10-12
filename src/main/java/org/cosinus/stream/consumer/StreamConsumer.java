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

package org.cosinus.stream.consumer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Stream consumer.
 *
 * @param <T> the type of streamed items
 */
public interface StreamConsumer<T> extends Consumer<T>, AutoCloseable {

    /**
     * The default maximum number of retry attempts when a retry is requested.
     */
    int RETRY_MAX_ATTEMPTS = 1;

    /**
     * Consume the stream items.
     *
     * @param stream the stream to consume
     */
    default void consume(Stream<T> stream) {
        consume(stream, null, null, null);
    }

    /**
     * Consume the stream items.
     *
     * @param stream the stream to consume
     * @param retry  the retry true is a retry should be attempted on failure
     * @param before the before an action to perform before consuming an item
     * @param after  the after an action to perform after consuming an item
     */
    default void consume(Stream<T> stream, Supplier<Boolean> retry, Consumer<T> before, Consumer<T> after) {
        stream.forEach(data -> {
            apply(data, before);
            acceptWithRetry(data, retry, 0, getRetryMaxAttempts());
            apply(data, after);
        });

    }

    private void acceptWithRetry(T data, Supplier<Boolean> retry, int retryCount, int retryMaxAttempts) {
        try {
            accept(data);
        } catch (UncheckedIOException ex) {
            if (retryCount < retryMaxAttempts && retry != null && retry.get()) {
                acceptWithRetry(data, retry, ++retryCount, retryMaxAttempts);
            } else {
                throw ex;
            }
        }
    }

    private void apply(T data, Consumer<T> consumer) {
        ofNullable(consumer).ifPresent(c -> c.accept(data));
    }

    /**
     * Gets retry max attempts.
     *
     * @return the retry max attempts
     */
    default int getRetryMaxAttempts() {
        return RETRY_MAX_ATTEMPTS;
    }

    @Override
    default void close() throws IOException {
    }

    /**
     * After close action.
     *
     * @param failed true if the consumption failed
     */
    default void afterClose(boolean failed) {
    }
}
