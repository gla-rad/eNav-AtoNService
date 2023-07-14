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

package org.grad.eNav.atonService.utils;

import _net.opengis.gml.profiles.ReferenceType;
import org.grad.eNav.atonService.models.domain.s125.AidsToNavigation;
import org.grad.eNav.atonService.models.domain.s125.BeaconCardinal;
import org.grad.eNav.atonService.models.domain.s125.VirtualAISAidToNavigation;
import org.grad.eNav.atonService.models.enums.ReferenceTypeRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceTypeS125ConverterTest {

    // Test Variables
    private AidsToNavigation aidToNavigation;
    private List<AidsToNavigation> aidsToNavigationList;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setup() {
        // Create a temp geometry factory to get a test geometries
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Initialise a single AtoN object
        this.aidToNavigation = new VirtualAISAidToNavigation();
        aidToNavigation.setId(BigInteger.ZERO);
        aidToNavigation.setAtonNumber("Virtual AtoN Number");
        aidToNavigation.setIdCode("Virtual AtoN ID");
        aidToNavigation.setTextualDescription("Description of Virtual AtoN");
        aidToNavigation.setTextualDescriptionInNationalLanguage("National Language Description of Virtual AtoN");
        aidToNavigation.setGeometry(factory.createPoint(new Coordinate(0, 0)));

        // Initialise the AtoN object list
        this.aidsToNavigationList = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            AidsToNavigation tempAidToNavigation = new BeaconCardinal();
            tempAidToNavigation.setId(BigInteger.valueOf(i));
            tempAidToNavigation.setAtonNumber("AtonNumber" + i);
            tempAidToNavigation.setIdCode("ID" + i);
            tempAidToNavigation.setTextualDescription("Description of AtoN No" + i);
            tempAidToNavigation.setTextualDescriptionInNationalLanguage("National Language Description of AtoN No" + i);
            tempAidToNavigation.setGeometry(factory.createPoint(new Coordinate(i % 180, i % 90)));
            this.aidsToNavigationList.add(tempAidToNavigation);
        }
    }

    /**
     * Test that we can correctly convert an Aid to Navigation object to an.
     * S-125 reference type. This should include the ID of the AtoN, its AtoN
     * number as the title, and the roles of the reference.
     */
    @Test
    void testConvertToReferenceType() {
       final ReferenceType referenceType =  new ReferenceTypeS125Converter().convertToReferenceType(this.aidToNavigation, ReferenceTypeRole.PARENT);

       // Make sure it looks OK
       assertNotNull(referenceType);
       assertTrue(referenceType.getHref().startsWith("#"));
        assertTrue(referenceType.getHref().endsWith(String.valueOf(this.aidToNavigation.getId())));
       assertEquals(ReferenceTypeRole.PARENT.getRole(), referenceType.getRole());
       assertEquals(ReferenceTypeRole.PARENT.getArchRole(), referenceType.getArcrole());
    }

    /**
     * Test that we can correctly convert an Aid to Navigation object list to a
     * list of S-125 reference types. Each entry should include the ID of the
     * AtoN, its AtoN number as the title, and the roles of the reference.
     */
    @Test
    void testConvertToReferenceTypeList() {
        final List<ReferenceType> referenceTypes =  new ReferenceTypeS125Converter().convertToReferenceTypes(this.aidsToNavigationList, ReferenceTypeRole.PARENT);

        // Make sure it looks OK
        assertNotNull(referenceTypes);
        assertEquals(this.aidsToNavigationList.size(), referenceTypes.size());
        int index = 0;
        for(ReferenceType referenceType: referenceTypes) {
            assertNotNull(referenceType);
            assertTrue(referenceType.getHref().startsWith("#"));
            assertEquals(ReferenceTypeRole.PARENT.getRole(), referenceType.getRole());
            assertEquals(ReferenceTypeRole.PARENT.getArchRole(), referenceType.getArcrole());

            // Increase the index to get the next test item
            index++;
        }
    }

    /**
     * Test that we can correctly convert an Aid to Navigation object set to a
     * list of S-125 reference types. Each entry should include the ID of the
     * AtoN, its AtoN number as the title, and the roles of the reference.
     */
    @Test
    void testConvertToReferenceTypeSet() {
        final List<ReferenceType> referenceTypes =  new ReferenceTypeS125Converter().convertToReferenceTypes(new HashSet<>(this.aidsToNavigationList), ReferenceTypeRole.PARENT);

        // Make sure it looks OK
        assertNotNull(referenceTypes);
        assertEquals(this.aidsToNavigationList.size(), referenceTypes.size());
        int index = 0;
        for(ReferenceType referenceType: referenceTypes) {
            assertNotNull(referenceType);
            assertTrue(referenceType.getHref().startsWith("#"));
            assertEquals(ReferenceTypeRole.PARENT.getRole(), referenceType.getRole());
            assertEquals(ReferenceTypeRole.PARENT.getArchRole(), referenceType.getArcrole());

            // Increase the index to get the next test item
            index++;
        }
    }

}