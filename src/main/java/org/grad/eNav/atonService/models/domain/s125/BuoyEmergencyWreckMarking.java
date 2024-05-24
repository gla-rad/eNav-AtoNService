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

import jakarta.persistence.Entity;

/**
 * The S-124 Buoy Emergency Wreck Marking Entity Class.
 * <p>
 * This is the basic class for implementing the S-201-compatible Buoy
 * Emergency Wreck Marking type. It is modelled as an entity that extends the
 * {@link GenericBuoy} super class.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 * @see _int.iho.s125.gml.cs0._1.BuoyEmergencyWreckMarking
 */
@Entity
public class BuoyEmergencyWreckMarking extends GenericBuoy {

}
