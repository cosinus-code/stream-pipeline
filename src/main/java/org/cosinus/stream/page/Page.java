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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.cosinus.stream.page.Pageable.LAST_PAGE_TOKEN;

/**
 * Page of items.
 *
 * @param <T> the type of items
 */
@Getter
@Builder
@AllArgsConstructor
public class Page<T> {

    private List<T> content;

    private String nextPageToken;

    /**
     * Creates a page with the given content and no next page token.
     *
     * @param content the content of the page
     * @param <T>     the type of items
     * @return the page
     */
    public static <T> Page<T> of(List<T> content) {
        return Page.<T>builder()
            .content(content)
            .build();
    }

    /**
     * Creates a page with the given content and next page token.
     *
     * @param content       the content of the page
     * @param nextPageToken the next page token, or null if there is no next page
     * @param <T>           the type of items
     * @return the page
     */
    public static <T> Page<T> of(List<T> content, String nextPageToken) {
        return Page.<T>builder()
            .content(content)
            .nextPageToken(ofNullable(nextPageToken)
                .orElse(LAST_PAGE_TOKEN))
            .build();
    }

    /**
     * Creates an empty page with no content and no next page token.
     *
     * @param <T> the type of items
     * @return the empty page
     */
    public static <T> Page<T> empty() {
        return Page.<T>builder()
            .content(emptyList())
            .build();
    }
}
