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

import _int.iho.s_125.gml.cs0._1.LightCharacteristicType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The S-125 Rhythm Of Light Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Rhythm Of Light type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.RhythmOfLightType
 */
@Entity
public class RhythmOfLight implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rhythm_of_light_generator")
    @SequenceGenerator(name="rhythm_of_light_generator", sequenceName = "rhythm_of_light_seq", allocationSize=1)
    private BigInteger id;

    @Enumerated(EnumType.STRING)
    private LightCharacteristicType lightCharacteristic;

    private String signalGroup;

    private BigDecimal signalPeriod;

    private SignalSequence signalSequence;

    /**
     * Gets id.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(BigInteger id) {
        this.id = id;
    }

    /**
     * Gets light characteristic.
     *
     * @return the light characteristic
     */
    public LightCharacteristicType getLightCharacteristic() {
        return lightCharacteristic;
    }

    /**
     * Sets light characteristic.
     *
     * @param lightCharacteristic the light characteristic
     */
    public void setLightCharacteristic(LightCharacteristicType lightCharacteristic) {
        this.lightCharacteristic = lightCharacteristic;
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
    public BigDecimal getSignalPeriod() {
        return signalPeriod;
    }

    /**
     * Sets signal period.
     *
     * @param signalPeriod the signal period
     */
    public void setSignalPeriod(BigDecimal signalPeriod) {
        this.signalPeriod = signalPeriod;
    }

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
}
