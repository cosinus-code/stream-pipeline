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

package org.cosinus.stream.swing;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Long.MAX_VALUE;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

public class FlatSwingComponentsSpliterator extends Spliterators.AbstractSpliterator<Component> {

    private final Queue<Component> componentsQueue;

    private final Set<Component> expandedComponents;

    public FlatSwingComponentsSpliterator(final Container container) {
        super(MAX_VALUE, ORDERED | NONNULL);
        this.componentsQueue = new ArrayDeque<>();
        this.expandedComponents = new HashSet<>();
        if (container instanceof Component component) {
            componentsQueue.add(component);
            expandedComponents.add(component);
        }
        expandContainerIntoQueue(container);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Component> action) {
        Component component = componentsQueue.poll();
        if (component == null) {
            return false;
        }

        if (component instanceof Container container) {
            expandContainerIntoQueue(container);
        }

        action.accept(component);
        return true;
    }

    protected void expandContainerIntoQueue(final Container container) {
        List<Component> components = concat(
            stream(container.getComponents()),
            container instanceof ExtendedContainer extendedContainer ?
                extendedContainer.streamAdditionalContainers() :
                Stream.empty())
            .filter(Objects::nonNull)
            .filter(component -> !expandedComponents.contains(component))
            .toList();

        componentsQueue.addAll(components);
        expandedComponents.addAll(components);
    }
}
