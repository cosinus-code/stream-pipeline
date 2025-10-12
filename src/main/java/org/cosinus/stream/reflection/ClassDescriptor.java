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

package org.cosinus.stream.reflection;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The class descriptor.
 *
 * @param <T> the type od class
 */
@Getter
@Setter
public class ClassDescriptor<T> {

    private final Class<T> mainClass;

    private final Type type;

    private Class<?>[] genericClasses;

    /**
     * Instantiates a new ClassDescriptor.
     *
     * @param mainClass the main class
     */
    public ClassDescriptor(final Class<T> mainClass) {
        this.mainClass = mainClass;
        this.type = mainClass;
    }

    /**
     * Instantiates a new ClassDescriptor.
     *
     * @param mainClass the main class
     * @param type      the type
     */
    public ClassDescriptor(final Class<T> mainClass, final Type type) {
        this.mainClass = mainClass;
        this.type = type;
    }

    /**
     * Ask if class is parametrized.
     *
     * @return true if class is parametrized
     */
    public boolean isParametrized() {
        return type instanceof ParameterizedType;
    }
}
