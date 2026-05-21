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

/**
 * Pageable parameters.
 */
@Getter
@Builder
@AllArgsConstructor
public class Pageable {

    /**
     * The page token indicating that there are no more pages to fetch.
     */
    public static final String LAST_PAGE_TOKEN = "lastPageToken";

    private int pageSize;

    private int pageNumber;

    private String pageToken;

    /**
     * Checks if this is the last page.
     *
     * @return true if this is the last page, false otherwise
     */
    public boolean isLastPage() {
        return LAST_PAGE_TOKEN.equals(pageToken);
    }
}
