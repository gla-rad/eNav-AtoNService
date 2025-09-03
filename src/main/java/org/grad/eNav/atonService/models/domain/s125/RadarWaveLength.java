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

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The S-125 Radar Wave Length Embeddable Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Radar
 * WaveL ength type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.RadarWaveLengthType
 */
@Embeddable
public class RadarWaveLength implements Serializable {

    // Class Variables
    private String radarBand;

    private BigDecimal waveLengthValue;

    /**
     * Gets radar band.
     *
     * @return the radar band
     */
    public String getRadarBand() {
        return radarBand;
    }

    /**
     * Sets radar band.
     *
     * @param radarBand the radar band
     */
    public void setRadarBand(String radarBand) {
        this.radarBand = radarBand;
    }

    /**
     * Gets wave length value.
     *
     * @return the wave length value
     */
    public BigDecimal getWaveLengthValue() {
        return waveLengthValue;
    }

    /**
     * Sets wave length value.
     *
     * @param waveLengthValue the wave length value
     */
    public void setWaveLengthValue(BigDecimal waveLengthValue) {
        this.waveLengthValue = waveLengthValue;
    }
}
