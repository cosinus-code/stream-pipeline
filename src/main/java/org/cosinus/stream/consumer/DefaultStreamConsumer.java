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
import java.io.OutputStream;
import java.io.UncheckedIOException;

/**
 * The default implementation of a stream consumer by writing to an output stream.
 *
 * @param <O> the type of the output stream to write to
 */
public class DefaultStreamConsumer<O extends OutputStream> implements StreamConsumer<OutputWriter<O>> {

    /**
     * The output stream to write to.
     */
    protected final O outputStream;

    /**
     * Instantiates a new DefaultStreamConsumer.
     *
     * @param outputStream the output stream
     */
    public DefaultStreamConsumer(O outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void accept(OutputWriter<O> writer) {
        try {
            writer.write(outputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }
}
