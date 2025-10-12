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

import java.util.function.Predicate;

import static java.util.Optional.ofNullable;

/**
 * The {@link Predicate} to check if a {@link ClassDescriptor} is a parametrized class of a given class.
 *
 * @param parametrizedClass the parametrized class
 */
public record ParametrizedClassPredicate(Class<?> parametrizedClass) implements Predicate<ClassDescriptor<?>> {

    @Override
    public boolean test(ClassDescriptor<?> classToCheck) {
        return ofNullable(classToCheck)
            .filter(ClassDescriptor::isParametrized)
            .filter(classDescriptor ->
                parametrizedClass.equals(classDescriptor.getMainClass()))
            .isPresent();
    }

    /**
     * Get a predicate to check if a {@link ClassDescriptor} is a parametrized class
     * of a given class with given generics classes.
     *
     * @param genericClasses the classes of generics
     * @return the predicate
     */
    public ParametrizedTypeWithGenericsPredicate withGenerics(final Class<?>... genericClasses) {
        return new ParametrizedTypeWithGenericsPredicate(parametrizedClass, false, genericClasses);
    }

    /**
     * Get a predicate to check if a {@link ClassDescriptor} is a parametrized class
     * of a given class with the generics extending the given classes.
     *
     * @param genericClasses the classes of generics
     * @return the predicate
     */
    public ParametrizedTypeWithGenericsPredicate withGenericsExtending(final Class<?>... genericClasses) {
        return new ParametrizedTypeWithGenericsPredicate(parametrizedClass, true, genericClasses);
    }

    /**
     * Get the predicate to check if a {@link ClassDescriptor} is a parametrized class
     *
     * @param parametrizedClass the parametrized class
     * @return the predicate
     */
    public static ParametrizedClassPredicate isParametrizedClass(Class<?> parametrizedClass) {
        return new ParametrizedClassPredicate(parametrizedClass);
    }
}
