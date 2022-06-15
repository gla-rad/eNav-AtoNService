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

package org.grad.eNav.atonService.components;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.grad.secom.models.*;
import org.grad.secom.models.enums.ContainerTypeEnum;
import org.grad.secom.models.enums.SECOM_DataProductType;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import javax.validation.constraints.Min;
import javax.ws.rs.QueryParam;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.grad.secom.interfaces.jaxrs.AccessNotificationSecomInterface.ACCESS_NOTIFICATION_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.AccessSecomInterface.ACCESS_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.AcknowledgementSecomInterface.ACKNOWLEDGMENT_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.CapabilitySecomInterface.CAPABILITY_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.DiscoveryServiceSecomInterface.DISCOVERY_SERVICE_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.EncryptionKeyNotifySecomInterface.ENCRYPTION_KEY_NOTIFY_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.EncryptionKeySecomInterface.ENCRYPTION_KEY_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.GetByLinkSecomInterface.GET_BY_LINK_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.GetSecomInterface.GET_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.GetSummarySecomInterface.GET_SUMMARY_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.PingSecomInterface.PING_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.RemoveSubscriptionSecomInterface.REMOVE_SUBSCRIPTION_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.SubscriptionNotificationSecomInterface.SUBSCRIPTION_NOTIFICATION_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.SubscriptionSecomInterface.SUBSCRIPTION_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.UploadLinkSecomInterface.UPLOAD_LINK_INTERFACE_PATH;
import static org.grad.secom.interfaces.jaxrs.UploadSecomInterface.UPLOAD_INTERFACE_PATH;

/**
 * The SECOM Client Class.
 * <p/>
 * This class can be used to register Springboot beans that connect to SECOM
 * compliant services and access/push information in the standardised
 * interfaces.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
public class SecomClient {

    // Class Variables
    WebClient secomClient;

    /**
     * The SECOM Client Constructor.
     *
     * @param url       the URL of the SECOM service
     * @param useSSL    whether to use SSL to connect to the service
     * @throws SSLException for any exceptions while configuring the SSL connection
     */
    public SecomClient(URL url, boolean useSSL) throws SSLException {
        // Initialise the HTTP connection configuration
        final HttpClient httpConnector = HttpClient
                .create()
                .followRedirect(true);

        // Add the SSL configuration if required
        if(useSSL) {
            final SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            httpConnector.secure(t -> t.sslContext(sslContext) );
        }

        // And create the SECOM web client
        this.secomClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpConnector))
                .baseUrl(url.toString())
                //.filter(setJWT())
                .build();
    }

    /**
     * POST /v1/access/notification : Result from Access Request performed on a
     * service instance shall be sent asynchronous through this client
     * interface.
     *
     * @param accessNotificationObject  the access notification object
     * @return the access notification response object
     */
    public Optional<AccessNotificationResponseObject> accessNotification(AccessNotificationObject accessNotificationObject) {
        return this.secomClient
                .post()
                .uri(ACCESS_NOTIFICATION_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accessNotificationObject))
                .retrieve()
                .bodyToMono(AccessNotificationResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/access : Access to the service instance information can be
     * requested through the Request Access interface.
     *
     * @param accessRequestObject the request access object
     * @return the request access response object
     */
    public Optional<AccessResponseObject> requestAccess(AccessRequestObject accessRequestObject) {
        return this.secomClient
                .post()
                .uri(ACCESS_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(accessRequestObject))
                .retrieve()
                .bodyToMono(AccessResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/acknowledgement : During upload of information, an
     * acknowledgement can be requested which is expected to be received when
     * the uploaded message has been delivered to the end system (technical
     * acknowledgement), and an acknowledgement when the message has been opened
     * (read) by the end user (operational acknowledgement). The acknowledgement
     * contains a reference to object delivered.
     *
     * @param acknowledgementObject  the acknowledgement object
     * @return the acknowledgement response object
     */
    public Optional<AcknowledgementResponseObject> acknowledgment(AcknowledgementObject acknowledgementObject) {
        return this.secomClient
                .post()
                .uri(ACKNOWLEDGMENT_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(acknowledgementObject))
                .retrieve()
                .bodyToMono(AcknowledgementResponseObject.class)
                .blockOptional();
    }

    /**
     * GET /v1/capability : The purpose of the interface is to provide a dynamic
     * method to ask a service instance at runtime what interfaces are
     * accessible, and what payload formats and version are valid.
     *
     * @return the capability response object
     */
    public Optional<CapabilityResponseObject> capability() {
        return this.secomClient
                .get()
                .uri(CAPABILITY_INTERFACE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CapabilityResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/searchService : The purpose of this interface is to search for
     * service instances to consume.
     *
     * @param searchFilterObject    The search filter object
     * @param page the page number to be retrieved
     * @param pageSize the maximum page size
     * @return the result list of the search
     */
    public Optional<SearchObjectResult[]> search(SearchFilterObject searchFilterObject,
                                                 Integer page,
                                                 Integer pageSize) {
        return this.secomClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(DISCOVERY_SERVICE_INTERFACE_PATH)
                        .queryParam("page", page)
                        .queryParam("pageSize", pageSize)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(searchFilterObject))
                .retrieve()
                .bodyToMono(SearchObjectResult[].class)
                .blockOptional();
    }

    /**
     * POST /v1/encryptionkey/notify : The purpose of the interface is to
     * exchange a temporary secret key. This operation enables a consumer to
     * request an encrypted secret key from a producer by providing a reference
     * to the encrypted data and a public certificate for symmetric key
     * derivation used to protect the temporary encryption key during transfer.
     *
     * @return the encryption key response object
     */
    public Optional<EncryptionKeyResponseObject> encryptionKeyNotify(EncryptionKeyNotificationObject encryptionKeyNotificationObject) {
        return this.secomClient
                .post()
                .uri(ENCRYPTION_KEY_NOTIFY_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(encryptionKeyNotificationObject))
                .retrieve()
                .bodyToMono(EncryptionKeyResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/encryptionkey : The purpose of the interface is to exchange a
     * temporary secret key. This operation is used to upload (push) an
     * encrypted secret key to a consumer.
     *
     * @return the encryption key response object
     */
    public Optional<EncryptionKeyResponseObject> encryptionKey(EncryptionKeyObject encryptionKeyObject) {
        return this.secomClient
                .post()
                .uri(ENCRYPTION_KEY_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(encryptionKeyObject))
                .retrieve()
                .bodyToMono(EncryptionKeyResponseObject.class)
                .blockOptional();
    }

    /**
     * GET /v1/object/link : The Get By Link interface is used for pulling
     * information from a data storage handled by the information owner. The
     * link to the data storage can be exchanged with Upload Link interface.
     * The owner of the information (provider) is responsible for relevant
     * authentication and authorization procedure before returning information.
     *
     * @param transactionIdentifier the transaction identifier
     * @return the object in an "application/octet-stream" encoding
     */
    public Optional<Object> getByLink(UUID transactionIdentifier) {
        return this.secomClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_BY_LINK_INTERFACE_PATH)
                        .queryParam("transactionIdentifier", transactionIdentifier)
                        .build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(Object.class)
                .blockOptional();
    }

    /**
     * GET /v1/object : The Get interface is used for pulling information from a
     * service provider. The owner of the information (provider) is responsible
     * for the authorization procedure before returning information.
     *
     * @param dataReference the object data reference
     * @param containerType the object data container type
     * @param dataProductType the object data product type
     * @param productVersion the object data product version
     * @param geometry the object geometry
     * @param unlocode the object UNLOCODE
     * @param validFrom the object valid from time
     * @param validTo the object valid to time
     * @param page the page number to be retrieved
     * @param pageSize the maximum page size
     * @return the object information
     */
    public Optional<GetResponseObject> get(@QueryParam("dataReference") UUID dataReference,
                                           @QueryParam("containerType") ContainerTypeEnum containerType,
                                           @QueryParam("dataProductType") SECOM_DataProductType dataProductType,
                                           @QueryParam("productVersion") String productVersion,
                                           @QueryParam("geometry") String geometry,
                                           @QueryParam("unlocode") String unlocode,
                                           @QueryParam("validFrom") LocalDateTime validFrom,
                                           @QueryParam("validTo") LocalDateTime validTo,
                                           @QueryParam("page") @Min(0) Integer page,
                                           @QueryParam("pageSize") @Min(0) Integer pageSize) {
        return this.secomClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_INTERFACE_PATH)
                        .queryParam("dataReference", dataReference)
                        .queryParam("containerType", containerType.getValue())
                        .queryParam("dataProductType", dataProductType.name())
                        .queryParam("productVersion", productVersion)
                        .queryParam("geometry", geometry)
                        .queryParam("unlocode", unlocode)
                        .queryParam("validFrom", validFrom)
                        .queryParam("validTo", validTo)
                        .queryParam("page", page)
                        .queryParam("pageSize", pageSize)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GetResponseObject.class)
                .blockOptional();
    }

    /**
     * GET /v1/object/summary :  A list of information shall be returned from
     * this interface. The summary contains identity, status and short
     * description of each information object. The actual information object
     * shall be retrieved using the Get interface.
     *
     * @param containerType the object data container type
     * @param dataProductType the object data product type
     * @param productVersion the object data product version
     * @param geometry the object geometry
     * @param unlocode the object UNLOCODE
     * @param validFrom the object valid from time
     * @param validTo the object valid to time
     * @param page the page number to be retrieved
     * @param pageSize the maximum page size
     * @return the summary response object
     */
    public Optional<GetSummaryResponseObject> getSummary(@QueryParam("containerType") ContainerTypeEnum containerType,
                                                         @QueryParam("dataProductType") SECOM_DataProductType dataProductType,
                                                         @QueryParam("productVersion") String productVersion,
                                                         @QueryParam("geometry") String geometry,
                                                         @QueryParam("unlocode") String unlocode,
                                                         @QueryParam("validFrom") LocalDateTime validFrom,
                                                         @QueryParam("validTo") LocalDateTime validTo,
                                                         @QueryParam("page") @Min(0) Integer page,
                                                         @QueryParam("pageSize") @Min(0) Integer pageSize) {
        return this.secomClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", containerType.getValue())
                        .queryParam("dataProductType", dataProductType.name())
                        .queryParam("productVersion", productVersion)
                        .queryParam("geometry", geometry)
                        .queryParam("unlocode", unlocode)
                        .queryParam("validFrom", validFrom)
                        .queryParam("validTo", validTo)
                        .queryParam("page", page)
                        .queryParam("pageSize", pageSize)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GetSummaryResponseObject.class)
                .blockOptional();
    }

    /**
     * GET /v1/ping : The purpose of the interface is to provide a dynamic
     * method to ask for the technical status of the specific service instance.
     *
     * @return the status response object
     */
    public Optional<GetSummaryResponseObject> ping() {
        return this.secomClient
                .get()
                .uri(PING_INTERFACE_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GetSummaryResponseObject.class)
                .blockOptional();
    }

    /**
     * DELETE /v1/subscription : Subscription(s) can be removed either
     * internally by information owner, or externally by the consumer. This
     * interface shall be used by the consumer to request removal of
     * subscription.
     *
     * @param removeSubscriptionObject the remove subscription object
     * @return the remove subscription response object
     */
    public Optional<RemoveSubscriptionResponseObject> removeSubscription(RemoveSubscriptionObject removeSubscriptionObject) {
        return this.secomClient
                .method(HttpMethod.DELETE)
                .uri(REMOVE_SUBSCRIPTION_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(removeSubscriptionObject))
                .retrieve()
                .bodyToMono(RemoveSubscriptionResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/subscription/notification : The interface receives notifications
     * when a subscription is created or removed by the information provider.
     *
     * @param subscriptionNotificationObject the subscription notification request object
     * @return the subscription notification response object
     */
    public Optional<SubscriptionNotificationResponseObject> subscriptionNotification(SubscriptionNotificationObject subscriptionNotificationObject) {
        return this.secomClient
                .post()
                .uri(SUBSCRIPTION_NOTIFICATION_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(subscriptionNotificationObject))
                .retrieve()
                .bodyToMono(SubscriptionNotificationResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/subscription : Request subscription on information, either
     * specific information according to parameters, or the information
     * accessible upon decision by the information provider.
     *
     * @param subscriptionRequestObject the subscription object
     * @return the subscription response object
     */
    public Optional<SubscriptionResponseObject> subscription(SubscriptionRequestObject subscriptionRequestObject) {
        return this.secomClient
                .post()
                .uri(SUBSCRIPTION_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(subscriptionRequestObject))
                .retrieve()
                .bodyToMono(SubscriptionResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/object/link : The REST operation POST /object/link. The
     * interface shall be used for uploading (pushing) a link to data to a
     * consumer.
     *
     * @param uploadLinkObject  the upload link object
     * @return the upload link response object
     */
    public Optional<UploadLinkResponseObject> uploadLink(UploadLinkObject uploadLinkObject) {
        return this.secomClient
                .post()
                .uri(UPLOAD_LINK_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(uploadLinkObject))
                .retrieve()
                .bodyToMono(UploadLinkResponseObject.class)
                .blockOptional();
    }

    /**
     * POST /v1/object : The interface shall be used for uploading (pushing)
     * data to a consumer. The operation expects one single data object and
     * its metadata.
     *
     * @param uploadObject  the upload object
     * @return the upload response object
     */
    public Optional<UploadResponseObject> upload(UploadObject uploadObject) {
        return this.secomClient
                .post()
                .uri(UPLOAD_INTERFACE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(uploadObject))
                .retrieve()
                .bodyToMono(UploadResponseObject.class)
                .blockOptional();
    }

}
