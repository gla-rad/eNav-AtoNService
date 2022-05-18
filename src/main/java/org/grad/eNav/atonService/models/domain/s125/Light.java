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

import _int.iala_aism.s125.gml._0_0.*;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * The S-125 Light Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Light type.
 * It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iala_aism.s125.gml._0_0.S125LightType
 */
@Entity
public class Light extends Equipment {

    // Class Variables
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = S125CategoryOfLight.class)
    private List<S125CategoryOfLight> categoryOfLights;

    @Enumerated(EnumType.STRING)
    private S125Colour colour;

    @Enumerated(EnumType.STRING)
    private S125ExhibitionConditionOfLight exhibitionConditionOfLight;

    private BigDecimal height;

    @Enumerated(EnumType.STRING)
    private S125LightCharacteristic lightCharacteristic;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = S125LightVisibility.class)
    private List<S125LightVisibility> lightVisibilities;

    @Enumerated(EnumType.STRING)
    private S125MarksNavigationalSystemOf marksNavigationalSystemOf;

    private BigInteger multiplicityOfLights;

    private String objectNameInNationalLanguage;

    private String objectName;

    private BigDecimal orientation;

    private BigDecimal sectorLimitOne;

    private BigDecimal sectorLimitTwo;

    private String signalGroup;

    private String signalPeriod;

    private String signalSequence;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = S125Status.class)
    private List<S125Status> statuses;

    private BigDecimal valueOfNominalRange;

    /**
     * Gets category of lights.
     *
     * @return the category of lights
     */
    public List<S125CategoryOfLight> getCategoryOfLights() {
        return categoryOfLights;
    }

    /**
     * Sets category of lights.
     *
     * @param categoryOfLights the category of lights
     */
    public void setCategoryOfLights(List<S125CategoryOfLight> categoryOfLights) {
        this.categoryOfLights = categoryOfLights;
    }

    /**
     * Gets colour.
     *
     * @return the colour
     */
    public S125Colour getColour() {
        return colour;
    }

    /**
     * Sets colour.
     *
     * @param colour the colour
     */
    public void setColour(S125Colour colour) {
        this.colour = colour;
    }

    /**
     * Gets exhibition condition of light.
     *
     * @return the exhibition condition of light
     */
    public S125ExhibitionConditionOfLight getExhibitionConditionOfLight() {
        return exhibitionConditionOfLight;
    }

    /**
     * Sets exhibition condition of light.
     *
     * @param exhibitionConditionOfLight the exhibition condition of light
     */
    public void setExhibitionConditionOfLight(S125ExhibitionConditionOfLight exhibitionConditionOfLight) {
        this.exhibitionConditionOfLight = exhibitionConditionOfLight;
    }

    /**
     * Gets height.
     *
     * @return the height
     */
    public BigDecimal getHeight() {
        return height;
    }

    /**
     * Sets height.
     *
     * @param height the height
     */
    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    /**
     * Gets light characteristic.
     *
     * @return the light characteristic
     */
    public S125LightCharacteristic getLightCharacteristic() {
        return lightCharacteristic;
    }

    /**
     * Sets light characteristic.
     *
     * @param lightCharacteristic the light characteristic
     */
    public void setLightCharacteristic(S125LightCharacteristic lightCharacteristic) {
        this.lightCharacteristic = lightCharacteristic;
    }

    /**
     * Gets light visibilities.
     *
     * @return the light visibilities
     */
    public List<S125LightVisibility> getLightVisibilities() {
        return lightVisibilities;
    }

    /**
     * Sets light visibilities.
     *
     * @param lightVisibilities the light visibilities
     */
    public void setLightVisibilities(List<S125LightVisibility> lightVisibilities) {
        this.lightVisibilities = lightVisibilities;
    }

    /**
     * Gets marks navigational system of.
     *
     * @return the marks navigational system of
     */
    public S125MarksNavigationalSystemOf getMarksNavigationalSystemOf() {
        return marksNavigationalSystemOf;
    }

    /**
     * Sets marks navigational system of.
     *
     * @param marksNavigationalSystemOf the marks navigational system of
     */
    public void setMarksNavigationalSystemOf(S125MarksNavigationalSystemOf marksNavigationalSystemOf) {
        this.marksNavigationalSystemOf = marksNavigationalSystemOf;
    }

    /**
     * Gets multiplicity of lights.
     *
     * @return the multiplicity of lights
     */
    public BigInteger getMultiplicityOfLights() {
        return multiplicityOfLights;
    }

    /**
     * Sets multiplicity of lights.
     *
     * @param multiplicityOfLights the multiplicity of lights
     */
    public void setMultiplicityOfLights(BigInteger multiplicityOfLights) {
        this.multiplicityOfLights = multiplicityOfLights;
    }

    /**
     * Gets object name in national language.
     *
     * @return the object name in national language
     */
    public String getObjectNameInNationalLanguage() {
        return objectNameInNationalLanguage;
    }

    /**
     * Sets object name in national language.
     *
     * @param objectNameInNationalLanguage the object name in national language
     */
    public void setObjectNameInNationalLanguage(String objectNameInNationalLanguage) {
        this.objectNameInNationalLanguage = objectNameInNationalLanguage;
    }

    /**
     * Gets object name.
     *
     * @return the object name
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets object name.
     *
     * @param objectName the object name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Gets orientation.
     *
     * @return the orientation
     */
    public BigDecimal getOrientation() {
        return orientation;
    }

    /**
     * Sets orientation.
     *
     * @param orientation the orientation
     */
    public void setOrientation(BigDecimal orientation) {
        this.orientation = orientation;
    }

    /**
     * Gets sector limit one.
     *
     * @return the sector limit one
     */
    public BigDecimal getSectorLimitOne() {
        return sectorLimitOne;
    }

    /**
     * Sets sector limit one.
     *
     * @param sectorLimitOne the sector limit one
     */
    public void setSectorLimitOne(BigDecimal sectorLimitOne) {
        this.sectorLimitOne = sectorLimitOne;
    }

    /**
     * Gets sector limit two.
     *
     * @return the sector limit two
     */
    public BigDecimal getSectorLimitTwo() {
        return sectorLimitTwo;
    }

    /**
     * Sets sector limit two.
     *
     * @param sectorLimitTwo the sector limit two
     */
    public void setSectorLimitTwo(BigDecimal sectorLimitTwo) {
        this.sectorLimitTwo = sectorLimitTwo;
    }

    /**
     * Gets signal group.
     *
     * @return the signal group
     */
    public String getSignalGroup() {
        return signalGroup;
    }

    /**
     * Sets signal group.
     *
     * @param signalGroup the signal group
     */
    public void setSignalGroup(String signalGroup) {
        this.signalGroup = signalGroup;
    }

    /**
     * Gets signal period.
     *
     * @return the signal period
     */
    public String getSignalPeriod() {
        return signalPeriod;
    }

    /**
     * Sets signal period.
     *
     * @param signalPeriod the signal period
     */
    public void setSignalPeriod(String signalPeriod) {
        this.signalPeriod = signalPeriod;
    }

    /**
     * Gets signal sequence.
     *
     * @return the signal sequence
     */
    public String getSignalSequence() {
        return signalSequence;
    }

    /**
     * Sets signal sequence.
     *
     * @param signalSequence the signal sequence
     */
    public void setSignalSequence(String signalSequence) {
        this.signalSequence = signalSequence;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public List<S125Status> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(List<S125Status> statuses) {
        this.statuses = statuses;
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
}