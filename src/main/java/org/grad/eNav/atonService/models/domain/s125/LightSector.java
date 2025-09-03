/*
 * Copyright (c) 2025 GLA Research and Development Directorate
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

package org.grad.eNav.atonService.models.domain.s125;

import _int.iho.s_125.gml.cs0._1.CategoryOfLightType;
import _int.iho.s_125.gml.cs0._1.ColourType;
import _int.iho.s_125.gml.cs0._1.LightVisibilityType;
import _int.iho.s_125.gml.cs0._1.impl.DirectionalCharacterTypeImpl;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * The S-125 Light Sector Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Light Sector
 * type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.LightSectorType
 */
@Embeddable
public class LightSector implements Serializable {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfLightType.class)
    private Set<ColourType> colours;

    private DirectionalCharacter directionalCharacter;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfLightType.class)
    private Set<LightVisibilityType> lightVisibilities;

    private SectorLimit sectorLimit;

    private BigDecimal valueOfNominalRange;

    @ElementCollection
    private Set<SectorInformation> sectorInformations;

    private Boolean sectorExtension;

    /**
     * Gets colours.
     *
     * @return the colours
     */
    public Set<ColourType> getColours() {
        return colours;
    }

    /**
     * Sets colours.
     *
     * @param colours the colours
     */
    public void setColours(Set<ColourType> colours) {
        this.colours = colours;
    }

    /**
     * Gets directional character.
     *
     * @return the directional character
     */
    public DirectionalCharacter getDirectionalCharacter() {
        return directionalCharacter;
    }

    /**
     * Sets directional character.
     *
     * @param directionalCharacter the directional character
     */
    public void setDirectionalCharacter(DirectionalCharacter directionalCharacter) {
        this.directionalCharacter = directionalCharacter;
    }

    /**
     * Gets light visibilities.
     *
     * @return the light visibilities
     */
    public Set<LightVisibilityType> getLightVisibilities() {
        return lightVisibilities;
    }

    /**
     * Sets light visibilities.
     *
     * @param lightVisibilities the light visibilities
     */
    public void setLightVisibilities(Set<LightVisibilityType> lightVisibilities) {
        this.lightVisibilities = lightVisibilities;
    }

    /**
     * Gets sector limit.
     *
     * @return the sector limit
     */
    public SectorLimit getSectorLimit() {
        return sectorLimit;
    }

    /**
     * Sets sector limit.
     *
     * @param sectorLimit the sector limit
     */
    public void setSectorLimit(SectorLimit sectorLimit) {
        this.sectorLimit = sectorLimit;
    }

    /**
     * Gets value of nominal range.
     *
     * @return the value of nominal range
     */
    public BigDecimal getValueOfNominalRange() {
        return valueOfNominalRange;
    }

    /**
     * Sets value of nominal range.
     *
     * @param valueOfNominalRange the value of nominal range
     */
    public void setValueOfNominalRange(BigDecimal valueOfNominalRange) {
        this.valueOfNominalRange = valueOfNominalRange;
    }

    /**
     * Gets sector informations.
     *
     * @return the sector informations
     */
    public Set<SectorInformation> getSectorInformations() {
        return sectorInformations;
    }

    /**
     * Sets sector informations.
     *
     * @param sectorInformations the sector informations
     */
    public void setSectorInformations(Set<SectorInformation> sectorInformations) {
        this.sectorInformations = sectorInformations;
    }

    /**
     * Gets sector extension.
     *
     * @return the sector extension
     */
    public Boolean getSectorExtension() {
        return sectorExtension;
    }

    /**
     * Sets sector extension.
     *
     * @param sectorExtension the sector extension
     */
    public void setSectorExtension(Boolean sectorExtension) {
        this.sectorExtension = sectorExtension;
    }
}
