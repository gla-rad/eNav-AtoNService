/*
 * Copyright (c) 2023 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonService.services;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.grad.eNav.atonService.exceptions.DataNotFoundException;
import org.grad.eNav.atonService.models.domain.s125.AidsToNavigation;
import org.grad.eNav.atonService.models.domain.s125.Association;
import org.grad.eNav.atonService.repos.AidsToNavigationRepo;
import org.grad.eNav.atonService.repos.AssociationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Aggregation Service.
 *
 * Service Implementation for managing the S-125 Association objects.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class AssociationService {

    /**
     * The Association Repo.
     */
    @Autowired
    AssociationRepo associationRepo;

    /**
     * The Aids to Navigation Repo.
     */
    @Autowired
    AidsToNavigationRepo aidsToNavigationRepo;

    /**
     * A simple saving operation that persists the models in the database using
     * the correct repository based on the instance type.
     *
     * @param association   The association to be persisted
     * @return the persisted association entity
     */
    @Transactional
    public Association save(Association association) {
        log.debug("Request to save Aggregation : {}", association);

        // Now save for each type
        return this.associationRepo.save(association);
    }

    /**
     * Delete the Association by ID.
     *
     * @param id the ID of the Association
     */
    @Transactional
    public Association delete(BigInteger id) {
        log.debug("Request to delete association with ID : {}", id);

        // Make sure the station node exists
        final Association association = this.associationRepo.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("No association found for the provided ID: %d", id)));

        // Now delete the association
        this.associationRepo.delete(association);

        // And return the object for AOP
        return association;
    }

    /**
     * Updates the association list of a given Aid to Navigation by retaining
     * the unchanged entries, deleting the obsolete entries and creating all
     * new ones that should be created.
     * <p/>
     * Note that this function is heavily dependent on set operations. The
     * reason is that we receive the association information mainly from
     * S-125 datasets and these do not contain clear identifiers for the
     * aggregations and associations. So we have to rely on the type and peers
     * of the object. Therefore, that's how it is mapped and checked against
     * the existing entries.
     *
     * @param atonNumber        The number of the Aid to Navigation included in the associations
     * @param newAssociations   The new associations to update the Aid to Navigation with
     * @return the update Aids to Navigation
     */
    @Transactional
    public Set<Association> updateAidsToNavigationAssociations(@NotNull String atonNumber, @NotNull Set<Association> newAssociations) {
        // Find the matching new aggregations
        final Set<Association> oldAssociations = this.associationRepo.findByIncludedAtonNumber(atonNumber);

        // Perform the set operations - find the existing ones to be retained
        final Set<Association> existingAssociations = Sets.intersection(oldAssociations, newAssociations);

        // Delete the obsolete aggregations
        final Set<Association> deletedAssociations = Sets.difference(oldAssociations, existingAssociations)
                .stream()
                .map(Association::getId)
                .map(this::delete)
                .collect(Collectors.toSet());

        // Create the new aggregations
        final Set<Association> createdAssociations = Sets.difference(newAssociations, existingAssociations)
                .stream()
                // We need to make sure that we have the correct objects to persist
                .peek(association -> association.setPeers(association.getPeers()
                        .stream()
                        .map(AidsToNavigation::getAtonNumber)
                        .map(this.aidsToNavigationRepo::findByAtonNumber)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet())))
                .map(this::save)
                .collect(Collectors.toSet());

        // Combine the existing and new aggregations for the AtoN
        return Sets.union(existingAssociations, createdAssociations);
    }
}
