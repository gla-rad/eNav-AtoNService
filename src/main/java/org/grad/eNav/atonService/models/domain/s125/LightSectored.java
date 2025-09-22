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

import _int.iho.s_125.gml.cs0._1.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-125 Light Sectored Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Light Sectored
 * type. It is modelled as an entity that extends the {@link GenericLight}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.LightSectored
 */
@Entity
public class LightSectored extends GenericLight {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = CategoryOfLightType.class)
    private Set<CategoryOfLightType> categoryOfLights;

    @Enumerated(EnumType.STRING)
    private ExhibitionConditionOfLightType exhibitionConditionOfLight;

    @Enumerated(EnumType.STRING)
    private MarksNavigationalSystemOfType marksNavigationalSystemOf;

    @Enumerated(EnumType.STRING)
    private SignalGenerationType signalGeneration;

    @ElementCollection
    private Set<ObscuredSector> obscuredSectors;

    @JsonManagedReference
    @OneToMany(mappedBy = "sectorOf", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<SectorCharacteristics> sectorCharacteristics = new HashSet<>();

    /**
     * Gets category of lights.
     *
     * @return the category of lights
     */
    public Set<CategoryOfLightType> getCategoryOfLights() {
        return categoryOfLights;
    }

    /**
     * Sets category of lights.
     *
     * @param categoryOfLights the category of lights
     */
    public void setCategoryOfLights(Set<CategoryOfLightType> categoryOfLights) {
        this.categoryOfLights = categoryOfLights;
    }

    /**
     * Gets exhibition condition of light.
     *
     * @return the exhibition condition of light
     */
    public ExhibitionConditionOfLightType getExhibitionConditionOfLight() {
        return exhibitionConditionOfLight;
    }

    /**
     * Sets exhibition condition of light.
     *
     * @param exhibitionConditionOfLight the exhibition condition of light
     */
    public void setExhibitionConditionOfLight(ExhibitionConditionOfLightType exhibitionConditionOfLight) {
        this.exhibitionConditionOfLight = exhibitionConditionOfLight;
    }

    /**
     * Gets marks navigational system of.
     *
     * @return the marks navigational system of
     */
    public MarksNavigationalSystemOfType getMarksNavigationalSystemOf() {
        return marksNavigationalSystemOf;
    }

    /**
     * Sets marks navigational system of.
     *
     * @param marksNavigationalSystemOf the marks navigational system of
     */
    public void setMarksNavigationalSystemOf(MarksNavigationalSystemOfType marksNavigationalSystemOf) {
        this.marksNavigationalSystemOf = marksNavigationalSystemOf;
    }

    /**
     * Gets signal generation.
     *
     * @return the signal generation
     */
    public SignalGenerationType getSignalGeneration() {
        return signalGeneration;
    }

    /**
     * Sets signal generation.
     *
     * @param signalGeneration the signal generation
     */
    public void setSignalGeneration(SignalGenerationType signalGeneration) {
        this.signalGeneration = signalGeneration;
    }

    /**
     * Gets obscured sectors.
     *
     * @return the obscured sectors
     */
    public Set<ObscuredSector> getObscuredSectors() {
        return obscuredSectors;
    }

    /**
     * Sets obscured sectors.
     *
     * @param obscuredSectors the obscured sectors
     */
    public void setObscuredSectors(Set<ObscuredSector> obscuredSectors) {
        this.obscuredSectors = obscuredSectors;
    }

    /**
     * Gets sector characteristics.
     *
     * @return the sector characteristics
     */
    public Set<SectorCharacteristics> getSectorCharacteristics() {
        return sectorCharacteristics;
    }

    /**
     * Sets sector characteristics.
     *
     * @param sectorCharacteristics the sector characteristics
     */
    public void setSectorCharacteristics(Set<SectorCharacteristics> sectorCharacteristics) {
        this.sectorCharacteristics.clear();
        if(sectorCharacteristics != null) {
            sectorCharacteristics.forEach(sectorCharacteristic -> sectorCharacteristic.setSectorOf(this));
            this.sectorCharacteristics.addAll(sectorCharacteristics);
        }
    }
}
