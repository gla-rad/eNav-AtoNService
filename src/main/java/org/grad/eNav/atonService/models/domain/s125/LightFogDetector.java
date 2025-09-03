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

import _int.iho.s_125.gml.cs0._1.SignalGenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * The S-125 Light Fog Detector Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Light Fog
 * Detector type. It is modelled as an entity that extends the
 * {@link GenericLight} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.LightFogDetector
 */
@Entity
public class LightFogDetector extends GenericLight {

    // Class Variables
    @Enumerated(EnumType.STRING)
    private SignalGenerationType signalGeneration;

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
}
