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
import static java.util.stream.IntStream.range;

/**
 * The {@link Predicate} to check if a {@link ClassDescriptor} is a parametrized class of a given class
 * with given generics classes.
 *
 * @param parametrizedClass the parametrized class to check
 * @param extending         if true, the generic classes can be subclasses of the given generic classes
 * @param genericClasses    the generic classes to check
 */
public record ParametrizedTypeWithGenericsPredicate(Class<?> parametrizedClass,
                                                    boolean extending,
                                                    Class<?>... genericClasses)
    implements Predicate<ClassDescriptor<?>> {

    @Override
    public boolean test(ClassDescriptor<?> classToCheck) {
        return ofNullable(classToCheck)
            .filter(ClassDescriptor::isParametrized)
            .filter(classDescriptor ->
                parametrizedClass.equals(classDescriptor.getMainClass()))
            .map(ClassDescriptor::getGenericClasses)
            .filter(types -> types.length == genericClasses.length)
            .filter(types -> range(0, genericClasses.length)
                .allMatch(index -> isGenericClass(types[index], genericClasses[index])))
            .isPresent();
    }

    private boolean isGenericClass(final Class<?> classToCheck, final Class<?> genericClass) {
        return extending ?
            genericClass.isAssignableFrom(classToCheck) :
            genericClass.equals(classToCheck);
    }
}

