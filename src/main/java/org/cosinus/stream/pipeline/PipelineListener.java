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

package org.cosinus.stream.pipeline;

/**
 * The pipeline listener.
 *
 * @param <D> the type of streamd data
 */
public interface PipelineListener<D> {

    /**
     * Before pipeline open.
     */
    default void beforePipelineOpen() {
    }

    /**
     * After pipeline open.
     */
    default void afterPipelineOpen() {
    }

    /**
     * Before pipeline data consume.
     *
     * @param data the data
     */
    default void beforePipelineDataConsume(D data) {
    }

    /**
     * After pipeline data consume.
     *
     * @param data the data
     */
    default void afterPipelineDataConsume(D data) {
    }

    /**
     * After pipeline data skip.
     *
     * @param skippedDataSize the skipped data size
     */
    default void afterPipelineDataSkip(long skippedDataSize) {
    }

    /**
     * Before pipeline close.
     */
    default void beforePipelineClose() {
    }

    /**
     * After pipeline close.
     *
     * @param pipelineFailed the pipeline failed
     */
    default void afterPipelineClose(boolean pipelineFailed) {
    }

    /**
     * On pipeline fail.
     */
    default void onPipelineFail() {
    }
}
