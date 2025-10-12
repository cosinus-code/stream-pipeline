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

import org.cosinus.stream.error.AbortPipelineConsumeException;
import org.cosinus.stream.binary.BinaryStream;
import org.cosinus.stream.pipeline.Pipeline;
import org.cosinus.stream.pipeline.PipelineListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Interface for a pipeline which processes binary data.
 */
public interface BinaryPipeline extends Pipeline<byte[], BinaryStream, BinaryStreamConsumer, BinaryPipelineStrategy> {

    @Override
    default BinaryStream openPipelineInputStream(final BinaryPipelineStrategy pipelineStrategy) {
        return BinaryStream.of(inputStream(), pipelineStrategy.getPipelineRate());
    }

    @Override
    default BinaryStreamConsumer openPipelineOutputStream(final BinaryPipelineStrategy pipelineStrategy) {
        boolean append = pipelineStrategy.shouldAppend() || pipelineStrategy.shouldResume();
        return new BinaryStreamConsumer(outputStream(append));
    }

    @Override
    default void preparePipelineConsume(final BinaryStream pipelineInputStream,
                                        final BinaryStreamConsumer pipelineOutputStream,
                                        final BinaryPipelineStrategy pipelineStrategy,
                                        PipelineListener<byte[]> pipelineListener) throws IOException {
        if (pipelineStrategy.shouldResume()) {
            long bytesToSkip = outputSize();
            if (bytesToSkip > 0) {
                long skippedBytes = pipelineInputStream.skipBytes(bytesToSkip);
                if (skippedBytes != bytesToSkip &&
                    !pipelineStrategy.shouldContinueWhenCannotResume(skippedBytes, bytesToSkip)) {
                    throw new AbortPipelineConsumeException(
                        format("Pipeline aborted by user after resume not match: expected to skip %d but was %d",
                            bytesToSkip, skippedBytes));
                }
                pipelineListener.afterPipelineDataSkip(skippedBytes);
            }
        }
    }

    @Override
    default void checkPipelineConsume(BinaryStream pipelineInputStream,
                                      BinaryStreamConsumer pipelineOutputStream,
                                      BinaryPipelineStrategy pipelineStrategy,
                                      PipelineListener<byte[]> listener) {
        if (pipelineStrategy.shouldCheck()) {
            Optional<String> inputChecksum = pipelineInputStream.checksum();
            Optional<String> outputChecksum = pipelineOutputStream.checksum();
            if (inputChecksum.equals(outputChecksum) &&
                !pipelineStrategy.shouldContinueWhenCheckFailed()) {
                throw new AbortPipelineConsumeException(
                    format("Pipeline aborted by user after consumed stream verification failed: " +
                            "expected %s checksum but was %s",
                        inputChecksum, outputChecksum));
            }
        }
    }

    /**
     * Get the input stream.
     *
     * @return the input stream
     */
    InputStream inputStream();

    /**
     * Get the output stream.
     *
     * @param append true if streamed data should be appended to existing data
     * @return the output stream
     */
    OutputStream outputStream(boolean append);

    /**
     * Get the pipeline output size (which would the pipeline debit measurement).
     *
     * @return the output size
     */
    long outputSize();
}
