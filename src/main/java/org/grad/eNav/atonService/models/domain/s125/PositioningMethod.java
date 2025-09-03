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

import _int.iho.s_125.gml.cs0._1.PositioningEquipmentType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;

/**
 * The S-125 Positioning Method Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Positioning
 * Method type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.PositioningMethodType
 */
@Embeddable
public class PositioningMethod implements Serializable {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private PositioningEquipmentType positioningEquipment;

    private String nmeaString;

    /**
     * Gets positioning equipment.
     *
     * @return the positioning equipment
     */
    public PositioningEquipmentType getPositioningEquipment() {
        return positioningEquipment;
    }

    /**
     * Sets positioning equipment.
     *
     * @param positioningEquipment the positioning equipment
     */
    public void setPositioningEquipment(PositioningEquipmentType positioningEquipment) {
        this.positioningEquipment = positioningEquipment;
    }

    /**
     * Gets nmea string.
     *
     * @return the nmea string
     */
    public String getNmeaString() {
        return nmeaString;
    }

    /**
     * Sets nmea string.
     *
     * @param nmeaString the nmea string
     */
    public void setNmeaString(String nmeaString) {
        this.nmeaString = nmeaString;
    }
}
