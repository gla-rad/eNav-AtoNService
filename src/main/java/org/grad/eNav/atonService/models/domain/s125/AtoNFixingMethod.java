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

import _int.iho.s_125.gml.cs0._1.HorizontalDatumType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

/**
 * The S-125 AtoN Fixing Method Entity Class.
 * <p/>
 * This class implements the AtoN Fixing Method type of the S-125 Aids to
 * Navigation objects. It is modelled as an entity that extends the
 * {@link Information} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s_125.gml.cs0._1.AtoNFixingMethod
 */
@Entity
public class AtoNFixingMethod extends Information {

    // Class Variable
    private String referencePoint;

    @Enumerated(EnumType.STRING)
    private HorizontalDatumType horizontalDatum;

    private LocalDate sourceDate;

    private String positioningProcedure;

    /**
     * Gets reference point.
     *
     * @return the reference point
     */
    public String getReferencePoint() {
        return referencePoint;
    }

    /**
     * Sets reference point.
     *
     * @param referencePoint the reference point
     */
    public void setReferencePoint(String referencePoint) {
        this.referencePoint = referencePoint;
    }

    /**
     * Gets horizontal datum.
     *
     * @return the horizontal datum
     */
    public HorizontalDatumType getHorizontalDatum() {
        return horizontalDatum;
    }

    /**
     * Sets horizontal datum.
     *
     * @param horizontalDatum the horizontal datum
     */
    public void setHorizontalDatum(HorizontalDatumType horizontalDatum) {
        this.horizontalDatum = horizontalDatum;
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

    /**
     * Gets positioning procedure.
     *
     * @return the positioning procedure
     */
    public String getPositioningProcedure() {
        return positioningProcedure;
    }

    /**
     * Sets positioning procedure.
     *
     * @param positioningProcedure the positioning procedure
     */
    public void setPositioningProcedure(String positioningProcedure) {
        this.positioningProcedure = positioningProcedure;
    }
}
