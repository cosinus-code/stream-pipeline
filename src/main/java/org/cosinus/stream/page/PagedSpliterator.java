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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;

import static java.lang.Long.MAX_VALUE;

/**
 * The spliterator implementation for streaming items in pages.
 * Useful when the source of items is a paged API.
 *
 * @param <T> the type streamed items
 */
public class PagedSpliterator<T> extends AbstractSpliterator<T> {

    private static final int DEFAULT_PAGE_SIZE = 200;

    private final PageSupplier<T> pageSupplier;

    private final Queue<T> items;

    private final int pageSize;

    private int pageNumber = 1;

    private String pageToken;

    /**
     * Instantiates a new PagedSpliterator.
     *
     * @param pageSupplier the items page supplier
     */
    public PagedSpliterator(final PageSupplier<T> pageSupplier) {
        this(pageSupplier, DEFAULT_PAGE_SIZE);
    }

    /**
     * Instantiates a new PagedSpliterator.
     *
     * @param pageSupplier the items page supplier
     * @param pageSize     the page size
     */
    public PagedSpliterator(final PageSupplier<T> pageSupplier, final int pageSize) {
        super(MAX_VALUE, ORDERED | NONNULL);

        this.pageSupplier = pageSupplier;
        this.pageSize = pageSize;
        this.items = new LinkedList<>();
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        if (items.isEmpty()) {
            items.addAll(getNextPage());
        }

        if (items.isEmpty()) {
            return false;
        }

        action.accept(items.poll());
        return true;
    }

    /**
     * Gets the next page of items.
     *
     * @return the next page of items
     */
    protected List<T> getNextPage() {
        Page<T> page = pageSupplier.getPage(Pageable.builder()
            .pageSize(getPageSize())
            .pageNumber(pageNumber++)
            .pageToken(pageToken)
            .build());
        pageToken = page.getNextPageToken();
        return page.getContent();
    }

    /**
     * Gets the page size.
     *
     * @return the page size
     */
    protected int getPageSize() {
        return pageSize;
    }
}
