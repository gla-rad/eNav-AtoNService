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

import _int.iho.s_125.gml.cs0._1.StatusType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.Set;

/**
 * The S-125 SElectronic AtoN Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Electronic
 * AtoN type. It is modelled as an entity that extends the
 * {@link AidsToNavigation} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.ElectronicAtonType
 * @see _int.iho.s_125.gml.cs0._1.PhysicalAISAidToNavigation
 * @see _int.iho.s_125.gml.cs0._1.VirtualAISAidToNavigation
 * @see _int.iho.s_125.gml.cs0._1.SyntheticAISAidToNavigation
 */
@Entity
public abstract class ElectronicAton extends AidsToNavigation {

    @KeywordField(sortable = Sortable.YES)
    private String atoNNumber;

    @KeywordField(sortable = Sortable.YES)
    private String mmsiCode;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = StatusType.class)
    private Set<StatusType> statuses;

    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "broadcast_by_join_table",
            joinColumns = { @JoinColumn(name = "ais_aton_id") },
            inverseJoinColumns = { @JoinColumn(name = "radio_station_id") }
    )
    private Set<RadioStation> broadcastBy;

    /**
     * Gets ato n number.
     *
     * @return the ato n number
     */
    public String getAtoNNumber() {
        return atoNNumber;
    }

    /**
     * Sets ato n number.
     *
     * @param atoNNumber the ato n number
     */
    public void setAtoNNumber(String atoNNumber) {
        this.atoNNumber = atoNNumber;
    }

    /**
     * Gets mmsi code.
     *
     * @return the mmsi code
     */
    public String getMmsiCode() {
        return mmsiCode;
    }

    /**
     * Sets mmsi code.
     *
     * @param mmsiCode the mmsi code
     */
    public void setMmsiCode(String mmsiCode) {
        this.mmsiCode = mmsiCode;
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
     * Gets broadcast by.
     *
     * @return the broadcast by
     */
    public Set<RadioStation> getBroadcastBy() {
        return broadcastBy;
    }

    /**
     * Sets broadcast by.
     *
     * @param broadcastBy the broadcast by
     */
    public void setBroadcastBy(Set<RadioStation> broadcastBy) {
        this.broadcastBy = broadcastBy;
    }
}
