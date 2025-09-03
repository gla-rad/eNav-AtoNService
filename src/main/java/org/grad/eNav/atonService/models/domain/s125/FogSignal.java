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

import _int.iho.s_125.gml.cs0._1.CategoryOfFogSignalType;
import _int.iho.s_125.gml.cs0._1.StatusType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Set;

/**
 * The S-125 Fog Signal Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Fog Signal
 * type. It is modelled as an entity that extends the {@link Equipment} super
 * class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.FogSignal
 */
@Entity
public class FogSignal extends Equipment {

    // Class Variables
    private SignalSequence signalSequence;

    @Enumerated(EnumType.STRING)
    private CategoryOfFogSignalType categoryOfFogSignal;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    /**
     * Gets signal sequence.
     *
     * @return the signal sequence
     */
    public SignalSequence getSignalSequence() {
        return signalSequence;
    }

    /**
     * Sets signal sequence.
     *
     * @param signalSequence the signal sequence
     */
    public void setSignalSequence(SignalSequence signalSequence) {
        this.signalSequence = signalSequence;
    }

    /**
     * Gets category of fog signal.
     *
     * @return the category of fog signal
     */
    public CategoryOfFogSignalType getCategoryOfFogSignal() {
        return categoryOfFogSignal;
    }

    /**
     * Sets category of fog signal.
     *
     * @param categoryOfFogSignal the category of fog signal
     */
    public void setCategoryOfFogSignal(CategoryOfFogSignalType categoryOfFogSignal) {
        this.categoryOfFogSignal = categoryOfFogSignal;
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
