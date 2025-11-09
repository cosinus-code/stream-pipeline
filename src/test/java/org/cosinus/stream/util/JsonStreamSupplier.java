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
package org.cosinus.stream.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.cosinus.stream.StreamSupplier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Json based implementation of {@link StreamSupplier} used for tests
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonStreamSupplier implements StreamSupplier<JsonStreamSupplier> {

    @Setter
    @Getter
    private String name;

    @Getter
    @Setter
    private JsonStreamSupplier parent;

    private List<JsonStreamSupplier> children;

    public void setChildren(List<JsonStreamSupplier> children) {
        this.children = children;
        children.forEach(child -> child.setParent(this));
    }

    @Override
    public Stream<JsonStreamSupplier> stream() {
        return ofNullable(children)
            .stream()
            .flatMap(Collection::stream);
    }
}
