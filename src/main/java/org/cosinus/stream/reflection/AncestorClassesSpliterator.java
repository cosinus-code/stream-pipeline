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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Queue;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Long.MAX_VALUE;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.ofNullable;

/**
 * A spliterator for streaming all ancestor classes and interfaces of a given object.
 */
public class AncestorClassesSpliterator extends AbstractSpliterator<ClassDescriptor<?>> {

    private final Queue<ClassDescriptor<?>> typesQueue;

    /**
     * Instantiates a new AncestorClassesSpliterator.
     *
     * @param object the object to analyze
     */
    public AncestorClassesSpliterator(final Object object) {
        super(MAX_VALUE, ORDERED | NONNULL);

        this.typesQueue = new ConcurrentLinkedQueue<>();
        this.typesQueue.add(new ClassDescriptor<>(object.getClass()));
    }

    @Override
    public boolean tryAdvance(Consumer<? super ClassDescriptor<?>> action) {
        ClassDescriptor<?> nextClassDescriptor = typesQueue.poll();
        if (nextClassDescriptor == null) {
            return false;
        }

        Class<?> nextClass = nextClassDescriptor.getMainClass();
        Class<?>[] genericClasses = nextClassDescriptor.getGenericClasses();

        concat(stream(nextClass.getGenericInterfaces()), Stream.of(nextClass.getGenericSuperclass()))
            .filter(Objects::nonNull)
            .map(type -> createClassDescriptor(type, genericClasses))
            .forEach(typesQueue::add);

        action.accept(nextClassDescriptor);
        return true;
    }

    private ClassDescriptor<?> createClassDescriptor(Type type, Class<?>[] genericClasses) {
        Class<?> mainClass = (Class<?>) (type instanceof ParameterizedType parameterizedType ?
            parameterizedType.getRawType() :
            type);
        ClassDescriptor<?> classDescriptor = new ClassDescriptor<>(mainClass, type);
        if (classDescriptor.getType() instanceof ParameterizedType parameterizedType) {
            AtomicInteger genericIndex = new AtomicInteger();
            classDescriptor.setGenericClasses(stream(parameterizedType.getActualTypeArguments())
                .map(genericType ->
                    genericType instanceof Class<?> genericClass ? genericClass :
                        genericType instanceof ParameterizedType genericParameterizedType ?
                            (Class<?>) genericParameterizedType.getRawType() :
                            //TODO: to find a better solution to find the right generic class
                            //finding by index is not correct in all cases
                            genericClasses != null && genericClasses.length > genericIndex.get() ?
                                genericClasses[genericIndex.getAndIncrement()] :
                                null)
                    .filter(Objects::nonNull)
                .toArray(Class<?>[]::new));
        }

        return classDescriptor;
    }
}
