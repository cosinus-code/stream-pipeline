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

import java.io.File;

/**
 * The interface for strategies of creating a temporary file for a given file.
 */
@FunctionalInterface
public interface TemporaryFileStrategy {

    /**
     * Gets the temporary file for a given file.
     *
     * @param file the initial file
     * @return the temporary file
     */
    File getFile(File file);
}
