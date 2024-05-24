/*
 * Copyright (c) 2024 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonService.services;

import _int.iho.s125.gml.cs0._1.CategoryOfAssociationType;
import org.grad.eNav.atonService.exceptions.DataNotFoundException;
import org.grad.eNav.atonService.models.domain.s125.*;
import org.grad.eNav.atonService.repos.AidsToNavigationRepo;
import org.grad.eNav.atonService.repos.AssociationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociationServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    AssociationService associationService;

    /**
     * The Association Repo mock.
     */
    @Mock
    AssociationRepo associationRepo;

    /**
     * The Aids to Navigation Repo mock.
     */
    @Mock
    AidsToNavigationRepo aidsToNavigationRepo;

    // Test Variables
    private Association association;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        this.association = new Association();
        this.association.setId(BigInteger.ONE);
        this.association.setAssociationType(CategoryOfAssociationType.DANGER_MARKINGS);

        // Initialise the AtoN messages list
        for(long i=0; i<5; i++) {
            AidsToNavigation aidsToNavigation = new BeaconCardinal();
            aidsToNavigation.setId(BigInteger.valueOf(i));
            aidsToNavigation.setIdCode("ID"+i);
            aidsToNavigation.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            // Add the feature name entries
            FeatureName featureName = new FeatureName();
            featureName.setName("Aton No" + i);
            aidsToNavigation.setFeatureNames(Collections.singleton(featureName));
            // Add the information entries
            Information information = new Information();
            information.setText("Description of AtoN No" + i);
            aidsToNavigation.setInformations(Collections.singleton(information));
            this.association.getPeers().add(aidsToNavigation);
        }
    }

    /**
     * Test that we can save correctly a new or existing association entry if
     * all the validation checks are successful.
     */
    @Test
    void testSave() {
        doReturn(this.association).when(this.associationRepo).save(any());

        // Perform the service call
        Association result = this.associationService.save(this.association);

        // Test the result
        assertNotNull(result);
        assertEquals(this.association.getId(), result.getId());
        assertEquals(this.association.getAssociationType(), result.getAssociationType());
        assertNotNull(result.getPeers());
        assertEquals(this.association.getPeers().size(), result.getPeers().size());
        assertTrue(result.getPeers().containsAll(this.association.getPeers()));

        // Also, that a saving call took place in the repository
        verify(this.associationRepo, times(1)).save(this.association);
    }

    /**
     * Test that we can successfully delete an existing association entry.
     */
    @Test
    void testDelete() throws DataNotFoundException {
        doReturn(Optional.of(this.association)).when(this.associationRepo).findById(this.association.getId());
        doNothing().when(this.associationRepo).delete(this.association);

        // Perform the service call
        this.associationService.delete(this.association.getId());

        // Verify that a deletion call took place in the repository
        verify(this.associationRepo, times(1)).delete(this.association);
    }

    /**
     * Test that we can update all the relevant associations of an AtoN based
     * on it's number. Because associations have an issue with the IDs coming
     * from the S-125 datasets, i.e. we cannot deterministically identify
     * which is which, we will keep the unchanged ones, but changes will already
     * create a new association and delete any old versions.
     */
    @Test
    void testUpdateAidsToNavigationAssociations() {
        doReturn(Collections.emptySet()).when(this.associationRepo).findByIncludedIdCode(any());
        doAnswer((inv) ->
                this.association.getPeers()
                        .stream()
                        .filter(aton -> Objects.equals(aton.getIdCode(), inv.getArgument(0)))
                        .findFirst()
        ).when(this.aidsToNavigationRepo).findByIdCode(any());
        doAnswer((inv) -> inv.getArgument(0)).when(this.associationService).save(any());

        // Perform the service  call
        final Set<Association> result = this.associationService.updateAidsToNavigationAssociations("aton-number", Collections.singleton(this.association));

        // Now make sure the response is as expected
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        // Inspect the included aggregation
        final Association resultAssociation = result.stream().findFirst().orElse(null);
        assertNotNull(resultAssociation);
        assertEquals(this.association.getId(), resultAssociation.getId());
        assertEquals(this.association.getAssociationType(), resultAssociation.getAssociationType());
        assertNotNull(resultAssociation.getPeers());
        assertFalse(resultAssociation.getPeers().isEmpty());
        assertEquals(this.association.getPeers().size(), resultAssociation.getPeers().size());
        assertTrue(this.association.getPeers().containsAll(resultAssociation.getPeers()));
    }

}