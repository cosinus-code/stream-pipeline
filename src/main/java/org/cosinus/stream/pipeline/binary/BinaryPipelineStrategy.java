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

package org.cosinus.stream.pipeline.binary;

import org.cosinus.stream.pipeline.PipelineStrategy;

/**
 * The pipeline strategy for binary pipeline.
 */
public interface BinaryPipelineStrategy extends PipelineStrategy {

    /**
     * The default pipeline rate.
     */
    int DEFAULT_PIPELINE_RATE = 8192;

    /**
     * Gets pipeline rate.
     *
     * @return the pipeline rate
     */
    default int getPipelineRate() {
        return DEFAULT_PIPELINE_RATE;
    }

    /**
     * Ask if the pipeline consumption should be checked.
     *
     * @return true if the pipeline consumption should be checked, false otherwise
     */
    default boolean shouldCheck() {
        return false;
    }

    /**
     * Ask if the pipeline output should be appended to the existing data.
     *
     * @return true if the pipeline output should be appended, false otherwise
     */
    default boolean shouldAppend() {
        return false;
    }

    /**
     * Ask if the pipeline output should be resumed if the exising data matches the beginning of the input data.
     *
     * @return tree if the pipeline output should be resumed, false otherwise
     */
    default boolean shouldResume() {
        return false;
    }

    /**
     * Ask if the pipeline output should continue when cannot resume.
     *
     * @param skippedBytes the skipped bytes
     * @param bytesToSkip  the bytes to skip
     * @return true if the pipeline should continue when cannot resume, false otherwise
     */
    default boolean shouldContinueWhenCannotResume(long skippedBytes, long bytesToSkip) {
        return false;
    }

    /**
     * Ask if the pipeline output should continue when the consumption check failed.
     *
     * @return true if the pipeline should continue when the consumption check failed, false otherwise
     */
    default boolean shouldContinueWhenCheckFailed() {
        return false;
    }

    /**
     * Ask if the pipeline output should be skipped if the target already exists.
     *
     * @return true if the pipeline should skip the consumption when the target already exists, false otherwise
     */
    default boolean shouldSkipExistingTarget() {
        return false;
    }
}
