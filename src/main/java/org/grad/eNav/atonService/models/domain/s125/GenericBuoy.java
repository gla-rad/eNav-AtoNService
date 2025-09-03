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

package org.grad.eNav.atonService.models.domain.s125;


import _int.iho.s_125.gml.cs0._1.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-125 Generic Buoy Entity Class.
 *
 * This is the basic class for implementing the S-125-compatible Generic Buoy
 * type. It is modelled as an entity class on hibernate, but it is abstract so
 * that we can extend this for each Buoy type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.GenericBuoyType
 */
@Entity
public abstract class GenericBuoy extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private BuoyShapeType buoyShape;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private Set<ColourPatternType> colourPatterns;

    @Enumerated(EnumType.STRING)
    private MarksNavigationalSystemOfType marksNavigationalSystemOf;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private Set<NatureOfConstructionType> natureOfconstuctions;

    private Boolean radarConspicuous;

    private String typeOfBuoy;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    @JsonManagedReference
    @OneToMany(mappedBy = "structure", cascade = CascadeType.REMOVE, orphanRemoval = true)
    final private Set<Topmark> equipments = new HashSet<>();

    /**
     * Gets buoy shape.
     *
     * @return the buoy shape
     */
    public BuoyShapeType getBuoyShape() {
        return buoyShape;
    }

    /**
     * Sets buoy shape.
     *
     * @param buoyShape the buoy shape
     */
    public void setBuoyShape(BuoyShapeType buoyShape) {
        this.buoyShape = buoyShape;
    }

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
     * Gets colour patterns.
     *
     * @return the colour patterns
     */
    public Set<ColourPatternType> getColourPatterns() {
        return colourPatterns;
    }

    /**
     * Sets colour patterns.
     *
     * @param colourPatterns the colour patterns
     */
    public void setColourPatterns(Set<ColourPatternType> colourPatterns) {
        this.colourPatterns = colourPatterns;
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
     * Gets nature ofconstuctions.
     *
     * @return the nature ofconstuctions
     */
    public Set<NatureOfConstructionType> getNatureOfconstuctions() {
        return natureOfconstuctions;
    }

    /**
     * Sets nature ofconstuctions.
     *
     * @param natureOfconstuctions the nature ofconstuctions
     */
    public void setNatureOfconstuctions(Set<NatureOfConstructionType> natureOfconstuctions) {
        this.natureOfconstuctions = natureOfconstuctions;
    }

    /**
     * Gets radar conspicuous.
     *
     * @return the radar conspicuous
     */
    public Boolean getRadarConspicuous() {
        return radarConspicuous;
    }

    /**
     * Sets radar conspicuous.
     *
     * @param radarConspicuous the radar conspicuous
     */
    public void setRadarConspicuous(Boolean radarConspicuous) {
        this.radarConspicuous = radarConspicuous;
    }

    /**
     * Gets type of buoy.
     *
     * @return the type of buoy
     */
    public String getTypeOfBuoy() {
        return typeOfBuoy;
    }

    /**
     * Sets type of buoy.
     *
     * @param typeOfBuoy the type of buoy
     */
    public void setTypeOfBuoy(String typeOfBuoy) {
        this.typeOfBuoy = typeOfBuoy;
    }

    /**
     * Gets statuses.
     *
     * @return the statuses
     */
    public Set<StatusType> getStatuses() {
        return statuses;
    }

    /**
     * Sets statuses.
     *
     * @param statuses the statuses
     */
    public void setStatuses(Set<StatusType> statuses) {
        this.statuses = statuses;
    }

    /**
     * Gets equipments.
     *
     * @return the equipments
     */
    public Set<Topmark> getEquipments() {
        return equipments;
    }

    /**
     * Sets equipment.
     *
     * @param equipments the equipments
     */
    public void setEquipment(Set<Topmark> equipments) {
        this.equipments.clear();
        if (equipments != null) {
            // Set the structure correctly
            equipments.forEach(fn -> fn.setStructure(this));
            // And update the equipments
            this.equipments.addAll(equipments);
        }
    }
}
