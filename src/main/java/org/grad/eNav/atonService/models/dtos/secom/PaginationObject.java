/*
 * Copyright (c) 2022 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonService.models.dtos.secom;

/**
 * The SECOM Pagination Object
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class PaginationObject {

    // Class Variables
    private int totalItems;
    private int maxItemsPerPage;

    /**
     * Instantiates a new Pagination object.
     */
    public PaginationObject() {

    }

    /**
     * Instantiates a new Pagination object.
     *
     * @param totalItems      the total items
     * @param maxItemsPerPage the max items per page
     */
    public PaginationObject(int totalItems, int maxItemsPerPage) {
        this.totalItems = totalItems;
        this.maxItemsPerPage = maxItemsPerPage;
    }

    /**
     * Gets total items.
     *
     * @return the total items
     */
    public int getTotalItems() {
        return totalItems;
    }

    /**
     * Sets total items.
     *
     * @param totalItems the total items
     */
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    /**
     * Gets max items per page.
     *
     * @return the max items per page
     */
    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    /**
     * Sets max items per page.
     *
     * @param maxItemsPerPage the max items per page
     */
    public void setMaxItemsPerPage(int maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
    }
}
