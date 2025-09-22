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

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.grad.eNav.atonService.utils.GeometryBinder;
import org.grad.eNav.atonService.utils.GeometryJSONDeserializer;
import org.grad.eNav.atonService.utils.GeometryJSONSerializer;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The S-125 Aids to Navigation Entity Class.
 * <p>
 * This is the basic class for implementing the S-125-compatible Aids to
 * Navigation type. It is modelled as an entity class on hibernate, but it is
 * abstract so that we can extend this for each Aids to Navigation type.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.AidsToNavigationType
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Cacheable
@Indexed
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class AidsToNavigation implements Serializable {

    // Class Variables
    @Id
    @ScaledNumberField(name = "id_sort", decimalScale=0, sortable = Sortable.YES)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aids_to_navigation_generator")
    @SequenceGenerator(name="aids_to_navigation_generator", sequenceName = "aids_to_navigation_seq", allocationSize=1)
    private BigInteger id;

    @NotNull
    @KeywordField(name="id_code", sortable = Sortable.YES)
    @Column(unique=true)
    private String idCode;

    @KeywordField(sortable = Sortable.YES)
    @Column(unique=true)
    private String interoperabilityIdentifier;

    @ElementCollection
    private Set<FeatureName> featureNames;

    @KeywordField(sortable = Sortable.YES)
    private String source;

    @GenericField(indexNullAs = "1970-01-01")
    private LocalDate sourceDate;

    @GenericField(indexNullAs = "1970-01-01")
    private LocalDate installationDate;

    @GenericField(indexNullAs = "1970-01-01")
    private LocalDate dateStart;

    @GenericField(indexNullAs = "9999-01-01")
    private LocalDate dateEnd;

    @GenericField(indexNullAs = "1970-01-01")
    private LocalDate periodStart;

    @GenericField(indexNullAs = "9999-01-01")
    private LocalDate periodEnd;

    private BigInteger scaleMinimum;

    private String pictorialRepresentation;

    @JsonSerialize(using = GeometryJSONSerializer.class)
    @JsonDeserialize(using = GeometryJSONDeserializer.class)
    @NonStandardField(name="geometry", valueBinder = @ValueBinderRef(type = GeometryBinder.class))
    private Geometry geometry;

    @JsonManagedReference
    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    final private Set<Information> informations = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "peers")
    final private Set<AtonAggregation> peerAtonAggregations = new HashSet<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "peers")
    final private Set<AtonAssociation> peerAtonAssociations = new HashSet<>();

    @ElementCollection
    private Set<String> seasonalActionRequireds;

    @GenericField()
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

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
     * Gets id code.
     *
     * @return the id code
     */
    public String getIdCode() {
        return idCode;
    }

    /**
     * Sets id code.
     *
     * @param idCode the id code
     */
    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    /**
     * Gets interoperability identifier.
     *
     * @return the interoperability identifier
     */
    public String getInteroperabilityIdentifier() {
        return interoperabilityIdentifier;
    }

    /**
     * Sets interoperability identifier.
     *
     * @param interoperabilityIdentifier the interoperability identifier
     */
    public void setInteroperabilityIdentifier(String interoperabilityIdentifier) {
        this.interoperabilityIdentifier = interoperabilityIdentifier;
    }

    /**
     * Gets feature names.
     *
     * @return the feature names
     */
    public Set<FeatureName> getFeatureNames() {
        return featureNames;
    }

    /**
     * Sets feature names.
     *
     * @param featureNames the feature names
     */
    public void setFeatureNames(Set<FeatureName> featureNames) {
        this.featureNames = featureNames;
    }

    /**
     * Gets source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets source.
     *
     * @param source the source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets source date.
     *
     * @return the source date
     */
    public LocalDate getSourceDate() {
        return sourceDate;
    }

    /**
     * Sets source date.
     *
     * @param sourceDate the source date
     */
    public void setSourceDate(LocalDate sourceDate) {
        this.sourceDate = sourceDate;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    /**
     * Gets date end.
     *
     * @return the date end
     */
    public LocalDate getDateEnd() {
        return dateEnd;
    }

    /**
     * Sets date end.
     *
     * @param dateEnd the date end
     */
    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    /**
     * Gets date start.
     *
     * @return the date start
     */
    public LocalDate getDateStart() {
        return dateStart;
    }

    /**
     * Sets date start.
     *
     * @param dateStart the date start
     */
    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    /**
     * Gets period end.
     *
     * @return the period end
     */
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    /**
     * Sets period end.
     *
     * @param periodEnd the period end
     */
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    /**
     * Gets period start.
     *
     * @return the period start
     */
    public LocalDate getPeriodStart() {
        return periodStart;
    }

    /**
     * Sets period start.
     *
     * @param periodStart the period start
     */
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    /**
     * Gets scale minimum.
     *
     * @return the scale minimum
     */
    public BigInteger getScaleMinimum() {
        return scaleMinimum;
    }

    /**
     * Sets scale minimum.
     *
     * @param scaleMinimum the scale minimum
     */
    public void setScaleMinimum(BigInteger scaleMinimum) {
        this.scaleMinimum = scaleMinimum;
    }

    /**
     * Gets pictorial representation.
     *
     * @return the pictorial representation
     */
    public String getPictorialRepresentation() {
        return pictorialRepresentation;
    }

    /**
     * Sets pictorial representation.
     *
     * @param pictorialRepresentation the pictorial representation
     */
    public void setPictorialRepresentation(String pictorialRepresentation) {
        this.pictorialRepresentation = pictorialRepresentation;
    }

    /**
     * Gets geometry.
     *
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Sets geometry.
     *
     * @param geometry the geometry
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Gets informations.
     *
     * @return the informations
     */
    public Set<Information> getInformations() {
        return informations;
    }

    /**
     * Sets informations.
     *
     * @param informations the informations
     */
    public void setInformations(Set<Information> informations) {
        this.informations.clear();
        if(informations != null) {
            informations.forEach(information -> information.setFeature(this));
            this.informations.addAll(informations);
        }
    }

    /**
     * Gets peer aton aggregations.
     *
     * @return the peer aton aggregations
     */
    public Set<AtonAggregation> getPeerAtonAggregations() {
        return peerAtonAggregations;
    }

    /**
     * Sets peer aton aggregations.
     *
     * @param peerAtonAggregations the peer aton aggregations
     */
    public void setPeerAtonAggregations(Set<AtonAggregation> peerAtonAggregations) {
        this.peerAtonAggregations.clear();
        if(peerAtonAggregations != null) {
            peerAtonAggregations.forEach(aggregation -> aggregation.getPeers().add(this));
            this.peerAtonAggregations.addAll(peerAtonAggregations);
        }
    }

    /**
     * Gets peer aton associations.
     *
     * @return the peer aton associations
     */
    public Set<AtonAssociation> getPeerAtonAssociations() {
        return peerAtonAssociations;
    }

    /**
     * Sets peer aton associations.
     *
     * @param peerAtonAssociations the peer aton associations
     */
    public void setPeerAtonAssociations(Set<AtonAssociation> peerAtonAssociations) {
        this.peerAtonAssociations.clear();
        if(peerAtonAssociations != null) {
            peerAtonAssociations.forEach(association -> association.getPeers().add(this));
            this.peerAtonAssociations.addAll(peerAtonAssociations);
        }
    }

    /**
     * Gets seasonal action requireds.
     *
     * @return the seasonal action requireds
     */
    public Set<String> getSeasonalActionRequireds() {
        return seasonalActionRequireds;
    }

    /**
     * Sets seasonal action requireds.
     *
     * @param seasonalActionRequireds the seasonal action requireds
     */
    public void setSeasonalActionRequireds(Set<String> seasonalActionRequireds) {
        this.seasonalActionRequireds = seasonalActionRequireds;
    }

    /**
     * Gets last modified at.
     *
     * @return the last modified at
     */
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    /**
     * Sets last modified at.
     *
     * @param lastModifiedAt the last modified at
     */
    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}
