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

package org.cosinus.stream.page;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.cosinus.stream.page.Pageable.LAST_PAGE_TOKEN;

@Getter
@Builder
public class Page<T> {

    private List<T> content;

    private String nextPageToken;

    public static <T> Page<T> of(List<T> content) {
        return Page.<T>builder()
            .content(content)
            .build();
    }

    public static <T> Page<T> of(List<T> content, String nextPageToken) {
        return Page.<T>builder()
            .content(content)
            .nextPageToken(ofNullable(nextPageToken)
                .orElse(LAST_PAGE_TOKEN))
            .build();
    }

    public static <T> Page<T> empty() {
        return Page.<T>builder()
            .content(emptyList())
            .build();
    }
}
