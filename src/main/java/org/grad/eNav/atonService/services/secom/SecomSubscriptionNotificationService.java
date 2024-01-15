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

package org.grad.eNav.atonService.services.secom;

import lombok.extern.slf4j.Slf4j;
import org.grad.secom.core.models.SubscriptionNotificationObject;
import org.grad.secom.core.models.SubscriptionNotificationResponseObject;
import org.grad.secom.core.models.enums.SubscriptionEventEnum;
import org.grad.secom.springboot3.components.SecomClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The SECOM Subscription Notification Service Class.
 * <p/>
 * A service handles the subscription notifications in an asynchronous way so
 * that the responses can go back to the clients regardless of the respective
 * subscription requests.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Service
@Slf4j
public class SecomSubscriptionNotificationService {

    /**
     * The SECOM Service.
     */
    @Autowired
    SecomService secomService;

    /**
     * Sends a SECOM subscription notification response object to the SECOM
     * client specified by the provided MRN. The response is generted for the
     * UUID and subscription event type provided in the input parameterr.
     *
     * @param mrn                       the client MRN to be informed
     * @param subscriptionIdentifier    the subscription identifier UUID
     * @param subscriptionEventEnum     the subscription event that took place
     * @return the received subscription notification response object
     */
    @Async("taskExecutor")
    public CompletableFuture<SubscriptionNotificationResponseObject> sendNotification(String mrn, UUID subscriptionIdentifier, SubscriptionEventEnum subscriptionEventEnum) {
        // Get the SECOM client matching the provided MRN
        final SecomClient secomClient = this.secomService.getClient(mrn);

        // Create the subscription notification response object
        SubscriptionNotificationObject subscriptionNotificationObject = new SubscriptionNotificationObject();
        subscriptionNotificationObject.setSubscriptionIdentifier(subscriptionIdentifier);
        subscriptionNotificationObject.setEventEnum(subscriptionEventEnum);

        // Send the object the return the response
        return CompletableFuture.completedFuture(secomClient.subscriptionNotification(subscriptionNotificationObject)
                .orElse(null));
    }

}
