/*
 * Copyright (c) 2022 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonService.repos;

import org.grad.eNav.atonService.models.domain.secom.SubscriptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the SECOM Subscription entities.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public interface SecomSubscriptionRepo extends JpaRepository<SubscriptionRequest, UUID> {

    /**
     * Retrieves a database entry based on the subscription request MRN, if that
     * exists.
     *
     * @param clientMrn the subscription request client MRN
     * @return The subscription request if that exists
     */
    Optional<SubscriptionRequest> findByClientMrn(String clientMrn);

}
