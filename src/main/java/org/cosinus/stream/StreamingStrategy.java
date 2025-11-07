/*
 * Copyright 2025 Cosinus Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.cosinus.stream;

/**
 * Streaming strategy
 */
public interface StreamingStrategy {

    /**
     * No strategy constant
     */
    StreamingStrategy NO_STRATEGY = new StreamingStrategy() {};

    /**
     * The default maximum number of retry attempts when a retry is requested.
     */
    int RETRY_MAX_ATTEMPTS = 1;

    /**
     * Should retry on pipeline failure.
     *
     * @param exception the exception
     * @return true if the pipeline should be retried on failure, false otherwise
     */
    default boolean shouldRetryOnFail(final Exception exception) {
        return false;
    }

    /**
     * Get the maximum number of retry attempts when a retry is requested.
     *
     * @return the maximum number of retry attempts when a retry is requested
     */
    default int getRetryMaxAttempts() {
        return RETRY_MAX_ATTEMPTS;
    }
}
