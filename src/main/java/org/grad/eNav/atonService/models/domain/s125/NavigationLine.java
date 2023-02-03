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

import _int.iala_aism.s125.gml._0_0.S125CategoryOfNavigationLine;
import _int.iala_aism.s125.gml._0_0.S125Status;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * The S-125 Navigation Line Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Navigation Line
 * type. It is modelled as an entity that extends the {@link AidsToNavigation}
 * super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iala_aism.s125.gml._0_0.S125NavigationLineType
 */
@Entity
public class NavigationLine extends AidsToNavigation {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private S125CategoryOfNavigationLine categoryOfNavigationLine;

    private BigDecimal orientation;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = S125Status.class)
    private List<S125Status> statuses;

    @ManyToMany(mappedBy = "navigationLines")
    private List<RecommendedTrack> navigableTracks;

    /**
     * Gets category of navigation line.
     *
     * @return the category of navigation line
     */
    public S125CategoryOfNavigationLine getCategoryOfNavigationLine() {
        return categoryOfNavigationLine;
    }

    /**
     * Sets category of navigation line.
     *
     * @param categoryOfNavigationLine the category of navigation line
     */
    public void setCategoryOfNavigationLine(S125CategoryOfNavigationLine categoryOfNavigationLine) {
        this.categoryOfNavigationLine = categoryOfNavigationLine;
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
     * Gets navigable tracks.
     *
     * @return the navigable tracks
     */
    public List<RecommendedTrack> getNavigableTracks() {
        return navigableTracks;
    }

    /**
     * Sets navigable tracks.
     *
     * @param navigableTracks the navigable tracks
     */
    public void setNavigableTracks(List<RecommendedTrack> navigableTracks) {
        this.navigableTracks = navigableTracks;
    }
}
