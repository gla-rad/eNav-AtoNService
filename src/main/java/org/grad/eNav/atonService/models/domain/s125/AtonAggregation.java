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

import _int.iho.s_125.gml.cs0._1.CategoryOfAggregationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The S-125 Aggregation Entity Class
 * <p/>
 * This class implements the {@link _int.iho.s_125.gml.cs0._1.AtonAggregation}
 * objects of the S-125 data product. These can be used to group multiple
 * Aids to Navigation into a single aggregation with a give type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see  _int.iho.s_125.gml.cs0._1.AtonAggregation
 */
@Entity
public class AtonAggregation implements Serializable {

    // Class Variables
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggregation_generator")
    @SequenceGenerator(name="aggregation_generator", sequenceName = "aggregation_seq", allocationSize=1)
    private BigInteger id;

    @Enumerated(EnumType.STRING)
    private CategoryOfAggregationType categoryOfAggregation;

    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "aggregation_join_table",
            joinColumns = { @JoinColumn(name = "aggregation_id") },
            inverseJoinColumns = { @JoinColumn(name = "aton_id") }
    )
    final private Set<AidsToNavigation> peers = new HashSet<>();

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
     * Gets category of aggregation.
     *
     * @return the category of aggregation
     */
    public CategoryOfAggregationType getCategoryOfAggregation() {
        return categoryOfAggregation;
    }

    /**
     * Sets category of aggregation.
     *
     * @param categoryOfAggregationType the category of aggregation
     */
    public void setCategoryOfAggregation(CategoryOfAggregationType categoryOfAggregationType) {
        this.categoryOfAggregation = categoryOfAggregationType;
    }

    /**
     * Gets peers.
     *
     * @return the peers
     */
    public Set<AidsToNavigation> getPeers() {
        return peers;
    }

    /**
     * Sets peers.
     *
     * @param peers the peers
     */
    public void setPeers(Set<AidsToNavigation> peers) {
        this.peers.clear();
        if (peers!= null) {
            this.getPeers().addAll(peers);
        }
    }

    /**
     * Overrides the equality operator of the class.
     *
     * @param o the object to check the equality
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtonAggregation that)) return false;
        return categoryOfAggregation == that.categoryOfAggregation
                && Objects.equals(this.getPeers().size(), that.getPeers().size())
                && new HashSet<>(this.getPeerIDCodes()).containsAll(that.getPeerIDCodes());
    }

    /**
     * Overrides the hashcode generation of the object.
     *
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                categoryOfAggregation,
                Arrays.hashCode(this.getPeerIDCodes().toArray())
        );
    }

    /**
     * Returns a list of all the peer AtoN ID Codes included in the aggregation.
     *
     * @return a list of all the peer AtoN ID Codes included in the aggregation
     */
    @JsonIgnore
    public Set<String> getPeerIDCodes() {
        return this.getPeers()
                .stream()
                .map(AidsToNavigation::getIdCode)
                .collect(Collectors.toSet());
    }
}
