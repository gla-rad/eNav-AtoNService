/*
 * Copyright (c) 2021 GLA Research and Development Directorate
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

package org.grad.eNav.atonService.services;

import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonService.models.domain.s125.AidsToNavigation;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * The S125 Web-Socket Service Class
 *
 * This class implements a handler for the AtoN messages coming into a Spring
 * Integration channel. It basically just publishes them to another channel,
 * which happens to be a web-socket implementation.
 *
 * @author Nikolaos Vastardis
 */
@Service
@Slf4j
public class S125WebSocketService implements MessageHandler {

    /**
     * The General Destination Prefix
     */
    @Value("${gla.rad.vdes-ctrl.web-socket.prefix:topic}")
    String prefix;

    /**
     * The S-125 Publish Channel to listen for the publications to.
     */
    @Autowired
    @Qualifier("s125PublicationChannel")
    PublishSubscribeChannel s125PublicationChannel;

    /**
     * Attach the web-socket as a simple messaging template
     */
    @Autowired
    SimpMessagingTemplate webSocket;

    /**
     * The service post-construct operations where the handler auto-registers
     * it-self to the aton publication channel. Once successful, it will then
     * monitor the channel for all inputs coming through the REST API.
     */
    @PostConstruct
    public void init() {
        log.info("S-125 Web Socket Service is booting up...");
        this.s125PublicationChannel.subscribe(this);
    }

    /**
     * When shutting down the application we need to make sure that all
     * threads have been gracefully shutdown as well.
     */
    @PreDestroy
    public void destroy() {
        log.info("S-125 Web Socket Service is shutting down...");
        if (this.s125PublicationChannel != null) {
            this.s125PublicationChannel.destroy();
        }
    }

    /**
     * This is a simple handler for the incoming messages. This is a generic
     * handler for any type of Spring Integration messages but it should really
     * only be used for the ones containing S-125 message payloads.
     *
     * @param message               The message to be handled
     * @throws MessagingException   The Messaging exceptions that might occur
     */
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        // Get the headers of the incoming message
        SECOM_DataProductType contentType = message.getHeaders().get(MessageHeaders.CONTENT_TYPE, SECOM_DataProductType.class);
        Boolean deletion = message.getHeaders().get("deletion", Boolean.class);

        // Only listen to S-125 data product messages
        if(contentType != SECOM_DataProductType.S125) {
            return;
        }

        // Handle only messages that seem valid
        if(message.getPayload() instanceof AidsToNavigation) {
            // Get the payload of the incoming message
            AidsToNavigation aidsToNavigation = (AidsToNavigation) message.getPayload();

            // A simple debug message
            log.debug(String.format("S-125 Web Socket Service received AtoN %s with AtoN number: %s.",
                    deletion ? "deletion" : "publication",
                    aidsToNavigation.getAtonNumber()));

            // Handle based on whether this is a deletion or not
            if(deletion) {
                this.publishMessage(this.webSocket, String.format("/%s/%s", prefix + "/deletions", contentType), aidsToNavigation);
            } else {
                this.publishMessage(this.webSocket, String.format("/%s/%s", prefix, contentType), aidsToNavigation);
            }
        }
        else {
            log.warn("Radar message handler received a message with erroneous format.");
        }
    }

    /**
     * Pushes a new/updated message into the web-socket messaging template.
     *
     * @param messagingTemplate     The web-socket messaging template
     * @param topic                 The topic of the web-socket
     * @param payload               The payload to be pushed
     */
    private void publishMessage(SimpMessagingTemplate messagingTemplate, String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }

}
