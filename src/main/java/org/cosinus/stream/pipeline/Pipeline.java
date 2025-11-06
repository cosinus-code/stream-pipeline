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

import org.cosinus.stream.consumer.StreamConsumer;
import org.cosinus.stream.error.SkipPipelineConsumeException;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Pipeline interface.
 *
 * @param <D> the type of data consumed through the pipeline
 * @param <I> the type of input stream to the pipeline
 * @param <O> the type of output stream consumer from the pipeline
 * @param <S> the type of stream pipeline strategy
 */
public interface Pipeline<D, I extends Stream<D>, O extends StreamConsumer<D>, S extends PipelineStrategy> {

    /**
     * Open pipeline input.
     *
     * @param pipelineStrategy the pipeline strategy
     * @return the input stream
     */
    I openPipelineInputStream(S pipelineStrategy);

    /**
     * Open the pipeline output.
     *
     * @param pipelineStrategy the pipeline strategy
     * @return the stream consumer
     */
    O openPipelineOutputStream(S pipelineStrategy);

    /**
     * Gets pipeline strategy.
     *
     * @return the pipeline strategy
     */
    S getPipelineStrategy();

    /**
     * Gets pipeline listener.
     *
     * @return the pipeline listener
     */
    PipelineListener<D> getPipelineListener();

    /**
     * Open the pipeline.
     *
     * @throws IOException the io exception
     */
    default void openPipeline() throws IOException {
        S pipelineStrategy = getPipelineStrategy();
        PipelineListener<D> pipelineListener = ofNullable(getPipelineListener())
            .orElseGet(() -> new PipelineListener<>() {
            });

        try {
            preparePipelineOpen(pipelineStrategy, pipelineListener);
        } catch (SkipPipelineConsumeException ex) {
            pipelineListener.afterPipelineDataSkip(ex.getSkippedSize());
            return;
        }

        boolean pipelineFailed = false;
        pipelineListener.beforePipelineOpen();
        try (I pipelineInputStream = openPipelineInputStream(pipelineStrategy);
             O pipelineOutputStream = openPipelineOutputStream(pipelineStrategy)) {

            pipelineListener.afterPipelineOpen();
            preparePipelineConsume(pipelineInputStream, pipelineOutputStream, pipelineStrategy, pipelineListener);
            pipelineOutputStream.consume(
                pipelineInputStream,
                ofNullable(pipelineStrategy)
                    .map(strategy -> (Function<Exception, Boolean>) strategy::shouldRetryOnFail)
                    .orElse(null),
                pipelineListener::beforePipelineDataConsume,
                pipelineListener::afterPipelineDataConsume);
            checkPipelineConsume(pipelineInputStream, pipelineOutputStream, pipelineStrategy, pipelineListener);
            pipelineListener.beforePipelineClose();
        } catch (SkipPipelineConsumeException ex) {
            pipelineListener.afterPipelineDataSkip(ex.getSkippedSize());
        } catch (Exception ex) {
            pipelineFailed = true;
            pipelineListener.onPipelineFail();
            throw ex;
        } finally {
            pipelineListener.afterPipelineClose(pipelineFailed);
        }
    }

    /**
     * Prepare the pipeline for opening.
     *
     * @param pipelineStrategy the pipeline strategy
     * @param pipelineListener the pipeline listener
     */
    default void preparePipelineOpen(S pipelineStrategy, PipelineListener<D> pipelineListener) {
    }

    /**
     * Prepare the pipeline for consumption.
     *
     * @param pipelineInputStream  the pipeline input stream
     * @param pipelineOutputStream the pipeline output stream
     * @param pipelineStrategy     the pipeline strategy
     * @param pipelineListener     the pipeline listener
     * @throws IOException the io exception
     */
    default void preparePipelineConsume(I pipelineInputStream,
                                        O pipelineOutputStream,
                                        S pipelineStrategy,
                                        PipelineListener<D> pipelineListener) throws IOException {
    }

    /**
     * Check the pipeline for consumption.
     *
     * @param pipelineInputStream  the pipeline input stream
     * @param pipelineOutputStream the pipeline output stream
     * @param pipelineStrategy     the pipeline strategy
     * @param pipelineListener     the pipeline listener
     */
    default void checkPipelineConsume(I pipelineInputStream,
                                      O pipelineOutputStream,
                                      S pipelineStrategy,
                                      PipelineListener<D> pipelineListener) {
    }
}
