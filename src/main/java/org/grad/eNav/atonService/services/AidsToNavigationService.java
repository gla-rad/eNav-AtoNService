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

package org.grad.eNav.atonService.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.grad.eNav.atonService.exceptions.DataNotFoundException;
import org.grad.eNav.atonService.models.domain.s125.AidsToNavigation;
import org.grad.eNav.atonService.models.dtos.datatables.DtPagingRequest;
import org.grad.eNav.atonService.repos.AidsToNavigationRepo;
import org.hibernate.search.backend.lucene.LuceneExtension;
import org.hibernate.search.backend.lucene.search.sort.dsl.LuceneSearchSortFactory;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.shape.jts.JtsGeometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * The Aids to Navigation Service.
 *
 * Service Implementation for managing the S-125 Aids to Navigation objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class AidsToNavigationService {

    /**
     * The Entity Manager.
     */
    @Autowired
    EntityManager entityManager;

    /**
     * The Generic Aids to Navigation Repo.
     */
    @Autowired
    AidsToNavigationRepo aidsToNavigationRepo;

    // Service Variables
    private final String[] searchFields = new String[] {
            "aton_number"
    };
    private final String[] searchFieldsWithSort = new String[] {
            "id"
    };

    /**
     * Get all the Aids to Navigation in a pageable search.
     *
     * @param pageable the pagination information
     * @return the list of Aids to Navigation
     */
    @Transactional(readOnly = true)
    public Page<AidsToNavigation> findAll(Optional<String> atonNumber,
                                     Optional<Geometry> geometry,
                                     Pageable pageable) {
        log.debug("Request to get Aids to Navigation in a pageable search");
        // Create the search query - always sort by name
        SearchQuery searchQuery = this.getAidsToNavigationSearchQuery(atonNumber, geometry, new Sort(new SortedNumericSortField("id_sort", SortField.Type.LONG, true)));

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> query.fetch(pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize()))
                .map(searchResult -> new PageImpl<AidsToNavigation>(searchResult.hits(), pageable, searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), pageable, 0));
    }

    /**
     * Handles a datatables pagination request and returns the results list in
     * an appropriate format to be viewed by a datatables jQuery table.
     *
     * @param dtPagingRequest the Datatables pagination request
     * @return the Datatables paged response
     */
    @Transactional(readOnly = true)
    public Page<AidsToNavigation> handleDatatablesPagingRequest(DtPagingRequest dtPagingRequest) {
        log.debug("Request to get Aids to Navigation in a Datatables pageable search");
        // Create the search query
        SearchQuery searchQuery = this.getSearchAidsToNavigationQueryByText(
                dtPagingRequest.getSearch().getValue(),
                dtPagingRequest.getLucenceSort(Arrays.asList(searchFieldsWithSort))
        );

        // Map the results to a paged response
        return Optional.of(searchQuery)
                .map(query -> query.fetch(dtPagingRequest.getStart(), dtPagingRequest.getLength()))
                .map(searchResult -> new PageImpl<AidsToNavigation>(searchResult.hits(), dtPagingRequest.toPageRequest(), searchResult.total().hitCount()))
                .orElseGet(() -> new PageImpl<>(Collections.emptyList(), dtPagingRequest.toPageRequest(), 0));
    }

    /**
     * A simple saving operation that persists the models in the database using
     * the correct repository based on the instance type.
     *
     * @param aidsToNavigation the Aids to Navigation entity to be saved
     * @return the saved Aids to Navigation entity
     */
    @Transactional
    public AidsToNavigation save(AidsToNavigation aidsToNavigation) {
        log.debug("Request to save Aids to Navigation : {}", aidsToNavigation);

        // Update the entity ID if the Code ID was found
        this.aidsToNavigationRepo.findByAtonNumber(aidsToNavigation.getAtonNumber())
                .ifPresent(aton -> aidsToNavigation.setId(aton.getId()));

        // Now save for each type
        return this.aidsToNavigationRepo.save(aidsToNavigation);
    }

    /**
     * Delete the Aids to Navigation by ID.
     *
     * @param id the ID of the Aids to Navigation
     */
    @Transactional
    public void delete(BigInteger id) {
        log.debug("Request to delete Aids to Navigation with ID : {}", id);

        // Make sure the station node exists
        final AidsToNavigation aidsToNavigation = this.aidsToNavigationRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("No Aids to Navigation found for the provided ID: %d", id)));

        // Now delete the station node
        this.aidsToNavigationRepo.delete(aidsToNavigation);
    }

    /**
     * Delete the Aids to Navigation by its AtoN number.
     *
     * @param atonNumber the AtoN number of the Aids to Navigation
     */
    @Transactional
    public void deleteByAtonNumber(String atonNumber) throws DataNotFoundException {
        log.debug("Request to delete ids to Navigation with AtoN number : {}", atonNumber);
        BigInteger id = this.aidsToNavigationRepo.findByAtonNumber(atonNumber)
                .map(AidsToNavigation::getId)
                .orElseThrow(() ->
                        new DataNotFoundException(String.format("No Aids to Navigation found for the provided AtoN number: %s", atonNumber))
                );
        this.delete(id);
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * search test. This query will be based solely on the station nodes table
     * and will include the following fields:
     * - UID
     * - Type
     * - Message
     *
     * @param searchText the text to be searched
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<AidsToNavigation> getSearchAidsToNavigationQueryByText(String searchText, Sort sort) {
        SearchSession searchSession = Search.session( entityManager );
        SearchScope<AidsToNavigation> scope = searchSession.scope( AidsToNavigation.class );
        return searchSession.search( scope )
                .extension(LuceneExtension.get())
                .where(f -> f.wildcard()
                        .fields( this.searchFields )
                        .matching( Optional.ofNullable(searchText).map(st -> "*"+st).orElse("") + "*" ))
                .sort(f -> f.fromLuceneSort(sort))
                .toQuery();
    }

    /**
     * Constructs a hibernate search query using Lucene based on the provided
     * AtoN UID and geometry. This query will be based solely on the aton
     * messages table and will include the following fields:
     * - UID
     * - Geometry
     * For any more elaborate search, the getSearchMessageQueryByText funtion
     * can be used.
     *
     * @param atonNumber the AtoN UID to be searched
     * @param geometry the geometry that the results should intersect with
     * @param sort the sorting selection for the search query
     * @return the full text query
     */
    protected SearchQuery<AidsToNavigation> getAidsToNavigationSearchQuery(Optional<String> atonNumber, Optional<Geometry> geometry, Sort sort) {
        // Then build and return the hibernate-search query
        SearchSession searchSession = Search.session( entityManager );
        SearchScope<AidsToNavigation> scope = searchSession.scope( AidsToNavigation.class );
        return searchSession.search( scope )
                .where( f -> f.bool(b -> {
                            b.must(f.matchAll());
                            atonNumber.ifPresent(v -> b.must(f.match()
                                    .field("aton_number")
                                    .matching(v)));
                            geometry.ifPresent(g-> b.must(f.extension(LuceneExtension.get())
                                    .fromLuceneQuery(createGeoSpatialQuery(g))));
                        })
                )
                .sort(f -> ((LuceneSearchSortFactory)f).fromLuceneSort(sort))
                .toQuery();
    }


    /**
     * Creates a Lucene geo-spatial query based on the provided geometry. The
     * query isa recursive one based on the maxLevels defined (in this case 12,
     * which result in a sub-meter precision).
     *
     * @param geometry      The geometry to generate the spatial query for
     * @return The Lucene geo-spatial query constructed
     */
    protected Query createGeoSpatialQuery(Geometry geometry) {
        // Initialise the spatial strategy
        JtsSpatialContext ctx = JtsSpatialContext.GEO;
        int maxLevels = 12; //results in sub-meter precision for geo-hash
        SpatialPrefixTree grid = new GeohashPrefixTree(ctx, maxLevels);
        RecursivePrefixTreeStrategy strategy = new RecursivePrefixTreeStrategy(grid,"geometry");

        // Create the Lucene GeoSpatial Query
        return Optional.ofNullable(geometry)
                .map(g -> new SpatialArgs(SpatialOperation.Intersects, new JtsGeometry(g, ctx, false , true)))
                .map(strategy::makeQuery)
                .orElse(null);
    }


}