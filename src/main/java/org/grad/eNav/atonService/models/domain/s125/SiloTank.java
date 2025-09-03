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
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.util.Set;

/**
 * The S-125 Silo Tank Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Silo Tank type.
 * It is modelled as an entity that extends the {@link StructureObject} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.SiloTank
 */
@Entity
public class SiloTank extends StructureObject {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private BuildingShapeType buildingShape;

    @Enumerated(EnumType.STRING)
    private CategoryOfSiloTankType categoryOfSiloTank;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourType.class)
    private Set<ColourType> colours;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ColourPatternType.class)
    private Set<ColourPatternType> colourPatterns;

    private Boolean radarConspicuous;

    @Enumerated(EnumType.STRING)
    private VisualProminenceType visualProminence;

    private BigDecimal height;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = NatureOfConstructionType.class)
    private Set<NatureOfConstructionType> natureOfConstructions;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    /**
     * Gets building shape.
     *
     * @return the building shape
     */
    public BuildingShapeType getBuildingShape() {
        return buildingShape;
    }

    /**
     * Sets building shape.
     *
     * @param buildingShape the building shape
     */
    public void setBuildingShape(BuildingShapeType buildingShape) {
        this.buildingShape = buildingShape;
    }

    /**
     * Gets category of silo tank.
     *
     * @return the category of silo tank
     */
    public CategoryOfSiloTankType getCategoryOfSiloTank() {
        return categoryOfSiloTank;
    }

    /**
     * Sets category of silo tank.
     *
     * @param categoryOfSiloTank the category of silo tank
     */
    public void setCategoryOfSiloTank(CategoryOfSiloTankType categoryOfSiloTank) {
        this.categoryOfSiloTank = categoryOfSiloTank;
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
     * Gets visual prominence.
     *
     * @return the visual prominence
     */
    public VisualProminenceType getVisualProminence() {
        return visualProminence;
    }

    /**
     * Sets visual prominence.
     *
     * @param visualProminence the visual prominence
     */
    public void setVisualProminence(VisualProminenceType visualProminence) {
        this.visualProminence = visualProminence;
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
     * Gets nature of constructions.
     *
     * @return the nature of constructions
     */
    public Set<NatureOfConstructionType> getNatureOfConstructions() {
        return natureOfConstructions;
    }

    /**
     * Sets nature of constructions.
     *
     * @param natureOfConstructions the nature of constructions
     */
    public void setNatureOfConstructions(Set<NatureOfConstructionType> natureOfConstructions) {
        this.natureOfConstructions = natureOfConstructions;
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
}
