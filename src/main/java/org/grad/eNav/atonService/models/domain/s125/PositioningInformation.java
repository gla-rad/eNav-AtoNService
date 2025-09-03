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

import _int.iho.s_125.gml.cs0._1.ChangeTypesType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * The S-125 Positioning Information Entity Class.
 * <p/>
 * This class implements the AtoN Fixing Method type of the S-125
 * Positioning Information objects. It is modelled as an entity
 * that extends the {@link Information} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.PositioningInformation
 */
@Entity
public class PositioningInformation extends Information {

    // Class Variables
    private String positioningDevice;

    private PositioningMethod positioningMethod;

    /**
     * Gets positioning device.
     *
     * @return the positioning device
     */
    public String getPositioningDevice() {
        return positioningDevice;
    }

    /**
     * Sets positioning device.
     *
     * @param positioningDevice the positioning device
     */
    public void setPositioningDevice(String positioningDevice) {
        this.positioningDevice = positioningDevice;
    }

    /**
     * Gets positioning method.
     *
     * @return the positioning method
     */
    public PositioningMethod getPositioningMethod() {
        return positioningMethod;
    }

    /**
     * Sets positioning method.
     *
     * @param positioningMethod the positioning method
     */
    public void setPositioningMethod(PositioningMethod positioningMethod) {
        this.positioningMethod = positioningMethod;
    }
}
