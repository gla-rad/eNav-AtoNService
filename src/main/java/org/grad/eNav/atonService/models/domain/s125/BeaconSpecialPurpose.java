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

package org.grad.eNav.atonService.models.domain.s125;

import _int.iala_aism.s125.gml._0_0.CategoryOfSpecialPurposeMarkType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;

/**
 * The S-125 Beacon Special Purpose Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Beacon Special
 * Purpose type. It is modelled as an entity that extends the
 * {@link GenericBeacon} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iala_aism.s125.gml._0_0.BeaconSpecialPurposeGeneralType
 */
@Entity
public class BeaconSpecialPurpose extends GenericBeacon {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfSpecialPurposeMarkType.class)
    private List<CategoryOfSpecialPurposeMarkType> categoryOfSpecialPurposeMarks;

    /**
     * Gets category of special purpose marks.
     *
     * @return the category of special purpose marks
     */
    public List<CategoryOfSpecialPurposeMarkType> getCategoryOfSpecialPurposeMarks() {
        return categoryOfSpecialPurposeMarks;
    }

    /**
     * Sets category of special purpose marks.
     *
     * @param categoryOfSpecialPurposeMarks the category of special purpose marks
     */
    public void setCategoryOfSpecialPurposeMarks(List<CategoryOfSpecialPurposeMarkType> categoryOfSpecialPurposeMarks) {
        this.categoryOfSpecialPurposeMarks = categoryOfSpecialPurposeMarks;
    }
}
