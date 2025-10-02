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

package org.grad.eNav.atonService.controllers.secom.v2;

import _int.iho.s_125.gml.cs0._1.Dataset;
import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.bind.JAXBException;
import org.grad.eNav.atonService.TestFeignSecurityConfig;
import org.grad.eNav.atonService.TestingConfiguration;
import org.grad.eNav.atonService.components.SecomV2CertificateProviderImpl;
import org.grad.eNav.atonService.components.SecomV2SignatureProviderImpl;
import org.grad.eNav.atonService.controllers.secom.SecomRequestHeaders;
import org.grad.eNav.atonService.models.domain.DatasetContent;
import org.grad.eNav.atonService.models.domain.s125.S125Dataset;
import org.grad.eNav.atonService.models.domain.secom.SubscriptionRequest;
import org.grad.eNav.atonService.services.DatasetService;
import org.grad.eNav.atonService.services.S100ExchangeSetService;
import org.grad.eNav.atonService.services.UnLoCodeService;
import org.grad.eNav.atonService.services.secom.v2.SecomV2SubscriptionService;
import org.grad.eNav.atonService.utils.S125DatasetBuilder;
import org.grad.eNav.s125.utils.S125Utils;
import org.grad.secomv2.core.base.DigitalSignatureCertificate;
import org.grad.secomv2.core.base.SecomConstants;
import org.grad.secomv2.core.components.SecomSignatureFilter;
import org.grad.secomv2.core.exceptions.SecomValidationException;
import org.grad.secomv2.core.models.*;
import org.grad.secomv2.core.models.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.geomesa.utils.interop.WKTUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import static org.grad.secomv2.core.base.SecomConstants.SECOM_DATE_TIME_FORMAT;
import static org.grad.secomv2.core.base.SecomConstants.SECOM_DATE_TIME_FORMATTER;
import static org.grad.secomv2.core.interfaces.AcknowledgementServiceInterface.ACKNOWLEDGMENT_INTERFACE_PATH;
import static org.grad.secomv2.core.interfaces.CapabilityServiceInterface.CAPABILITY_INTERFACE_PATH;
import static org.grad.secomv2.core.interfaces.GetServiceInterface.GET_INTERFACE_PATH;
import static org.grad.secomv2.core.interfaces.GetSummaryServiceInterface.GET_SUMMARY_INTERFACE_PATH;
import static org.grad.secomv2.core.interfaces.RemoveSubscriptionServiceInterface.REMOVE_SUBSCRIPTION_INTERFACE_PATH;
import static org.grad.secomv2.core.interfaces.SubscriptionServiceInterface.SUBSCRIPTION_INTERFACE_PATH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@Import({TestingConfiguration.class, TestFeignSecurityConfig.class})
class SecomV2ControllerTest {

    /**
     * The Reactive Web Test Client.
     */
    @Autowired
    WebTestClient webTestClient;

    /**
     * The Model Mapper.
     */
    @Autowired
    ModelMapper modelMapper;

    /**
     * The Dataset Service mock.
     */
    @MockitoBean
    DatasetService datasetService;

    /**
     * The SECOM Exchange Set Service.
     */
    @MockitoBean
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The UN/LoCode Service mock.
     */
    @MockitoBean
    UnLoCodeService unLoCodeService;

    /**
     * The SECOM v2 Subscription Service mock.
     */
    @MockitoBean
    SecomV2SubscriptionService secomV2SubscriptionService;

    /**
     * The SECOM v2 Certificate Provider mock.
     */
    @MockitoBean
    SecomV2CertificateProviderImpl secomCertificateProvider;

    /**
     * The SECOM v2 Signature Provider mock.
     */
    @MockitoBean
    SecomV2SignatureProviderImpl secomSignatureProvider;

    /**
     * The SECOM v2 Signature Filter mock.
     *
     * This will block the actual SECOM signature verification process and will
     * allow out test messages to go through the signature filter without valid
     * signatures.
     */
    @MockitoBean
    SecomSignatureFilter secomSignatureFilter;

    // Test Variables
    private UUID queryDataReference;
    private ContainerTypeEnum queryContainerType;
    private SECOM_DataProductType queryDataProductType;
    private String queryProductVersion;
    private String queryGeometry;
    private String queryUnlocode;
    private LocalDateTime queryValidFrom;
    private LocalDateTime queryValidTo;
    private Integer queryPage;
    private Integer queryPageSize;
    private S125Dataset s125DataSet;
    private String s125DataSetAsXml;
    private DatasetContent datasetContent;
    private SubscriptionRequestObject subscriptionRequestObject;
    private SubscriptionRequest savedSubscriptionRequest;
    private RemoveSubscriptionObject removeSubscriptionObject;
    private AcknowledgementObject acknowledgementObject;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() throws JAXBException {
        // Setup the query arguments
        // Test Variables
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        this.queryDataReference = UUID.randomUUID();
        this.queryContainerType = ContainerTypeEnum.S100_DataSet;
        this.queryDataProductType = SECOM_DataProductType.S125;
        this.queryProductVersion = "0.0.1";
        this.queryGeometry = WKTUtils.write(geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-180, -90),
                new Coordinate(-180, 90),
                new Coordinate(180, 90),
                new Coordinate(180, -90),
                new Coordinate(-180, -90),
        }));
        this.queryUnlocode = "ADALV";
        this.queryValidFrom = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        this.queryValidTo = LocalDateTime.of(3000, 1, 1, 0, 0, 0);
        this.queryPage = 0;
        this.queryPageSize = Integer.MAX_VALUE;

        // Construct a test S-125 Dataset
        this.s125DataSet = new S125Dataset("125Dataset");
        this.s125DataSet.setUuid(this.queryDataReference);
        this.s125DataSet.setGeometry(geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(-180, -90),
                new Coordinate(-180, 90),
                new Coordinate(180, 90),
                new Coordinate(180, -90),
                new Coordinate(-180, -90),
        }));

        // Marshal the dataset content
        final S125DatasetBuilder s125DatasetBuilder = new S125DatasetBuilder(this.modelMapper);
        final Dataset dataset = s125DatasetBuilder.packageToDataset(this.s125DataSet, Collections.emptyList());
        this.s125DataSetAsXml = S125Utils.marshalS125(dataset, Boolean.FALSE);
        this.datasetContent = new DatasetContent();
        this.datasetContent.setId(BigInteger.ONE);
        this.datasetContent.setGeneratedAt(LocalDateTime.now());
        this.datasetContent.setContent(this.s125DataSetAsXml);
        this.datasetContent.setContentLength(BigInteger.valueOf(this.s125DataSetAsXml.length()));
        this.s125DataSet.setDatasetContent(this.datasetContent);

        // Setup the subscription requests and responses
        this.subscriptionRequestObject = new SubscriptionRequestObject();
        final EnvelopeSubscriptionObject envelopeSubscriptionObject = new EnvelopeSubscriptionObject();
        envelopeSubscriptionObject.setContainerType(ContainerTypeEnum.S100_DataSet);
        envelopeSubscriptionObject.setDataProductType(SECOM_DataProductType.S125);
        this.subscriptionRequestObject.setEnvelope(envelopeSubscriptionObject);
        this.subscriptionRequestObject.setEnvelopeSignature("Envelope Signature");
        this.savedSubscriptionRequest = new SubscriptionRequest();
        this.savedSubscriptionRequest.setUuid(UUID.randomUUID());
        this.savedSubscriptionRequest.setContainerType(ContainerTypeEnum.S100_DataSet);
        this.savedSubscriptionRequest.setDataProductType(SECOM_DataProductType.S125);
        this.removeSubscriptionObject = new RemoveSubscriptionObject();
        this.removeSubscriptionObject.setSubscriptionIdentifier(UUID.randomUUID());

        // Setup an acknowledgement
        this.acknowledgementObject = new AcknowledgementObject();
        EnvelopeAckObject envelopeAckObject = new EnvelopeAckObject();
        envelopeAckObject.setCreatedAt(Instant.now());
        envelopeAckObject.setTransactionIdentifier(UUID.randomUUID());
        envelopeAckObject.setAckType(AckTypeEnum.DELIVERED_ACK);
        envelopeAckObject.setEnvelopeSignatureCertificate(new String[]{"Signature Certificate"});
        envelopeAckObject.setEnvelopeRootCertificateThumbprint("Root Certificate Thumbprint");
        envelopeAckObject.setEnvelopeSignatureTime(Instant.now());
        this.acknowledgementObject.setEnvelope(envelopeAckObject);
        this.acknowledgementObject.setEnvelopeSignature("Envelope Signature");
    }

    /**
     * Test that the SECOM Capability interface is configured properly and
     * returns the expected Capability Response Object output.
     */
    @Test
    void testCapability() {
        webTestClient.get()
                .uri("/api/secom" + CAPABILITY_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CapabilityResponseObject.class)
                .consumeWith(response -> {
                    CapabilityResponseObject capabilityResponseObject = response.getResponseBody();
                    assertNotNull(capabilityResponseObject);
                    assertNotNull(capabilityResponseObject.getCapability());
                    assertFalse(capabilityResponseObject.getCapability().isEmpty());
                    assertEquals(1, capabilityResponseObject.getCapability().size());
                    assertEquals(ContainerTypeEnum.S100_DataSet, capabilityResponseObject.getCapability().getFirst().getContainerType());
                    assertEquals(SECOM_DataProductType.S125, capabilityResponseObject.getCapability().getFirst().getDataProductType());
                    assertEquals("/xsd/S125.xsd", capabilityResponseObject.getCapability().getFirst().getProductSchemaUrl().getPath());
                    assertEquals("0.0.0", capabilityResponseObject.getCapability().getFirst().getServiceVersion());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getAccess());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getEncryptionKey());
                    assertTrue(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getGet());
                    assertTrue(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getSubscription());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getGetByLink());
                    assertTrue(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getGetSummary());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getUpload());
                    assertFalse(capabilityResponseObject.getCapability().getFirst().getImplementedInterfaces().getUploadLink());
                });
    }

    /**
     * Test that the SECOM Capability interface will respond with an HTTP
     * Status METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testCapabilityMethodNotAllowed() {
        webTestClient.post()
                .uri("/api/secom" + CAPABILITY_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Get Summary interface is configured properly
     * and returns the expected Get Summary Response Object output.
     */
    @Test
    void testGetSummary() {
        doReturn(new PageImpl<>(Collections.singletonList(this.s125DataSet), Pageable.ofSize(this.queryPageSize), 1))
                .when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());

         webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetSummaryResponseObject.class)
                .consumeWith(response -> {
                    GetSummaryResponseObject getSummaryResponseObject = response.getResponseBody();
                    assertNotNull(getSummaryResponseObject);
                    assertNotNull(getSummaryResponseObject.getInformationSummaryObject());
                    assertEquals(1, getSummaryResponseObject.getInformationSummaryObject().size());
                    assertEquals(ContainerTypeEnum.S100_DataSet, getSummaryResponseObject.getInformationSummaryObject().getFirst().getContainerType());
                    assertEquals(SECOM_DataProductType.S125, getSummaryResponseObject.getInformationSummaryObject().getFirst().getDataProductType());
                    assertEquals(Boolean.FALSE, getSummaryResponseObject.getInformationSummaryObject().getFirst().getDataCompression());
                    assertEquals(Boolean.FALSE, getSummaryResponseObject.getInformationSummaryObject().getFirst().getDataProtection());
                    assertEquals(this.s125DataSet.getUuid(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getDataReference());
                    assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getProductEdition(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getInfo_productVersion());
                    assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getDatasetFileIdentifier(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getInfo_identifier());
                    assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getDatasetTitle(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getInfo_name());
                    assertEquals(InfoStatusEnum.PRESENT.getValue(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getInfo_status());
                    assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getDatasetAbstract(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getInfo_description());
                    assertEquals(this.s125DataSet.getLastUpdatedAt(), getSummaryResponseObject.getInformationSummaryObject().getFirst().getInfo_lastModifiedDate());
                    assertNotNull(getSummaryResponseObject.getPagination());
                    assertEquals(Integer.MAX_VALUE, getSummaryResponseObject.getPagination().getMaxItemsPerPage());
                    assertEquals(1, getSummaryResponseObject.getPagination().getTotalItems());
                });
    }

    /**
     * Test that the SECOM Get Summary interface will return an HTTP Status
     * BAD_REQUEST if one of the provided query parameters is not formatted
     * properly
     */
    @Test
    void testGetSummaryBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", "Badly Formatted Date")
                        .queryParam("validTo", "Another Badly Formatted Date")
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Get Summary interface will return an HTTP Status
     * METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testGetSummaryMethodNotAllowed() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_SUMMARY_INTERFACE_PATH)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Get interface is configured properly and returns the
     * expected Get Response Object output.
     */
    @Test
    void testGet() throws CertificateEncodingException, IOException {
        // Mock the SECOM library certificate and signature providers
        X509Certificate mockCertificate = mock(X509Certificate.class);
        doReturn("certificate".getBytes()).when(mockCertificate).getEncoded();
        PublicKey mockPublicKey = mock(PublicKey.class);
        doReturn(mockPublicKey).when(mockCertificate).getPublicKey();
        X509Certificate mockRootCertificate = mock(X509Certificate.class);
        doReturn("rootCertificate".getBytes()).when(mockRootCertificate).getEncoded();
        DigitalSignatureCertificate digitalSignatureCertificate = new DigitalSignatureCertificate();
        digitalSignatureCertificate.setCertificateAlias(new String[]{"secom"});
        digitalSignatureCertificate.setCertificate(new X509Certificate[]{mockCertificate});
        digitalSignatureCertificate.setPublicKey(mockPublicKey);
        digitalSignatureCertificate.setRootCertificate(mockRootCertificate);
        doReturn(digitalSignatureCertificate).when(this.secomCertificateProvider).getDigitalSignatureCertificate();
        doReturn(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA).when(this.secomSignatureProvider).getSignatureAlgorithm();
        doReturn("signature".getBytes()).when(this.secomSignatureProvider).generateSignature(any(), any(), any());

        // Mock the rest
        doReturn(new PageImpl<>(Collections.singletonList(this.s125DataSet), Pageable.ofSize(this.queryPageSize), 1))
                .when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetResponseObject.class)
                .consumeWith(response -> {
                    GetResponseObject getResponseObject = response.getResponseBody();
                    assertNotNull(getResponseObject);
                    assertNotNull(getResponseObject.getDataResponseObject());
                    assertNotNull(getResponseObject.getPagination());
                    assertEquals(1, getResponseObject.getDataResponseObject().size());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getData());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata());
                    assertEquals(Boolean.FALSE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDataProtection());
                    assertEquals(Boolean.FALSE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getCompressionFlag());
                    assertEquals(SecomConstants.SECOM_PROTECTION_SCHEME, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getProtectionScheme());
                    assertEquals(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureReference());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue());
                    assertEquals(DatatypeConverter.printHexBinary("signature".getBytes()), getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getDigitalSignature());
                    assertArrayEquals(new String[]{Base64.getEncoder().encodeToString("certificate".getBytes())}, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicCertificate());
                    assertEquals("fc5b132b465442bdde70eb9d7f0e9ec8ad84ec56858e237e3e3cbe6398946a2d", getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicRootCertificateThumbprint());
                    assertEquals(Integer.MAX_VALUE, getResponseObject.getPagination().getMaxItemsPerPage());
                    assertEquals(1, getResponseObject.getPagination().getTotalItems());

                    // Try to parse the incoming data
                    String s125Xml = new String(Base64.getDecoder().decode(getResponseObject.getDataResponseObject().getFirst().getData()));
                    try {
                        Dataset result = S125Utils.unmarshallS125(s125Xml);
                        assertNotNull(result);
                        assertNotNull(result.getId());
                        assertNotNull(result.getDatasetIdentificationInformation());
                        assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
                        assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getDatasetFileIdentifier(), result.getDatasetIdentificationInformation().getDatasetFileIdentifier());
                        assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getProductEdition(), result.getDatasetIdentificationInformation().getProductEdition());
                        assertEquals(this.s125DataSet.getDatasetIdentificationInformation().getDatasetTitle(), result.getDatasetIdentificationInformation().getDatasetTitle());
                    } catch (JAXBException ex) {
                        fail(ex);
                    }
                });
    }

    /**
     * Test that the SECOM Get interface is configured properly and also
     * supports S-100 Exchange Sets if requested.
     */
    @Test
    void testGetExchangeSet() throws CertificateEncodingException, IOException, JAXBException {
        // Mock the SECOM library certificate and signature providers
        X509Certificate mockCertificate = mock(X509Certificate.class);
        doReturn("certificate".getBytes()).when(mockCertificate).getEncoded();
        PublicKey mockPublicKey = mock(PublicKey.class);
        doReturn(mockPublicKey).when(mockCertificate).getPublicKey();
        X509Certificate mockRootCertificate = mock(X509Certificate.class);
        doReturn("rootCertificate".getBytes()).when(mockRootCertificate).getEncoded();
        DigitalSignatureCertificate digitalSignatureCertificate = new DigitalSignatureCertificate();
        digitalSignatureCertificate.setCertificateAlias(new String[]{"secom"});
        digitalSignatureCertificate.setCertificate(new X509Certificate[]{mockCertificate});
        digitalSignatureCertificate.setPublicKey(mockPublicKey);
        digitalSignatureCertificate.setRootCertificate(mockRootCertificate);
        doReturn(digitalSignatureCertificate).when(this.secomCertificateProvider).getDigitalSignatureCertificate();
        doReturn(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA).when(this.secomSignatureProvider).getSignatureAlgorithm();
        doReturn("signature".getBytes()).when(this.secomSignatureProvider).generateSignature(any(), any(), any());

        // Mock the rest
        doReturn(new PageImpl<>(Collections.singletonList(this.s125DataSet), Pageable.ofSize(this.queryPageSize), 1))
                .when(this.datasetService).findAll(any(), any(), any(), any(), any(), any());
        doReturn("packagedExchangeSet".getBytes()).when(this.s100ExchangeSetService).packageToExchangeSet(any(), any(), any());

        // Set the container type to Exchange Set
        this.queryContainerType = ContainerTypeEnum.S100_ExchangeSet;

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidFrom))
                        .queryParam("validTo", DateTimeFormatter.ofPattern(SECOM_DATE_TIME_FORMAT).format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isOk()
                .expectBody(GetResponseObject.class)
                .consumeWith(response -> {
                    GetResponseObject getResponseObject = response.getResponseBody();
                    assertNotNull(getResponseObject);
                    assertNotNull(getResponseObject.getDataResponseObject());
                    assertNotNull(getResponseObject.getPagination());
                    assertEquals(1, getResponseObject.getDataResponseObject().size());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getData());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata());
                    assertEquals(Boolean.FALSE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDataProtection());
                    assertEquals(Boolean.TRUE, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getCompressionFlag());
                    assertEquals(SecomConstants.SECOM_PROTECTION_SCHEME, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getProtectionScheme());
                    assertEquals(DigitalSignatureAlgorithmEnum.SHA3_384_WITH_ECDSA, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureReference());
                    assertNotNull(getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue());
                    assertEquals(DatatypeConverter.printHexBinary("signature".getBytes()), getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getDigitalSignature());
                    assertArrayEquals(new String[]{Base64.getEncoder().encodeToString("certificate".getBytes())}, getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicCertificate());
                    assertEquals("fc5b132b465442bdde70eb9d7f0e9ec8ad84ec56858e237e3e3cbe6398946a2d", getResponseObject.getDataResponseObject().getFirst().getExchangeMetadata().getDigitalSignatureValue().getPublicRootCertificateThumbprint());
                    assertEquals(Integer.MAX_VALUE, getResponseObject.getPagination().getMaxItemsPerPage());
                    assertEquals(1, getResponseObject.getPagination().getTotalItems());

                    // Try to parse the incoming data
                    String exchangeSetBytes = new String(Base64.getDecoder().decode(getResponseObject.getDataResponseObject().getFirst().getData()));
                    assertEquals("packagedExchangeSet", exchangeSetBytes);
                });
    }

    /**
     * Test that the SECOM Get interface will return an HTTP Status BAD_REQUEST
     * if one of the provided query parameters is not formatted properly
     */
    @Test
    void testGetBadRequest() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", "Badly Formatted Date")
                        .queryParam("validTo", "Another Badly Formatted Date")
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Get interface will return an HTTP Status
     * METHOD_NOT_ALLOWED if a method other than a GET is requested.
     */
    @Test
    void testGetMethodNotAllowed() {
        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + GET_INTERFACE_PATH)
                        .queryParam("dataReference", this.queryDataReference)
                        .queryParam("containerType", this.queryContainerType.getValue())
                        .queryParam("dataProductType", this.queryDataProductType)
                        .queryParam("productVersion", this.queryProductVersion)
                        .queryParam("geometry", this.queryGeometry)
                        .queryParam("unlocode", this.queryUnlocode)
                        .queryParam("validFrom", SECOM_DATE_TIME_FORMATTER.format(this.queryValidFrom))
                        .queryParam("validTo", SECOM_DATE_TIME_FORMATTER.format(this.queryValidTo))
                        .queryParam("page", this.queryPage)
                        .queryParam("pageSize", this.queryPageSize)
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Subscription interface is configured properly and
     * returns the expected Subscription Response Object output.
     */
    @Test
    void testSubscription() {
        doReturn(savedSubscriptionRequest).when(this.secomV2SubscriptionService).save(any(), any());

        webTestClient.post()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(subscriptionRequestObject), SubscriptionRequestObject.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionResponseObject.class)
                .consumeWith(response -> {
                    SubscriptionResponseObject subscriptionResponseObject = response.getResponseBody();
                    assertNotNull(subscriptionResponseObject);
                    assertEquals(savedSubscriptionRequest.getUuid(), subscriptionResponseObject.getSubscriptionIdentifier());
                    assertEquals("Subscription successfully created", subscriptionResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Subscription interface is configured properly even
     * when a callback URL is provided, and returns the expected Subscription
     * Response Object output.
     */
    @Test
    void testSubscriptionWithCallback() throws MalformedURLException {
        doReturn(savedSubscriptionRequest).when(this.secomV2SubscriptionService).save(any(), any());

        // Set a callback URL
        this.subscriptionRequestObject.getEnvelope().setCallbackEndpoint(URI.create("http://localhost").toURL());
        this.subscriptionRequestObject.getEnvelope().setPushAll(Boolean.TRUE);

        webTestClient.post()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(subscriptionRequestObject), SubscriptionRequestObject.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionResponseObject.class)
                .consumeWith(response -> {
                    SubscriptionResponseObject subscriptionResponseObject = response.getResponseBody();
                    assertNotNull(subscriptionResponseObject);
                    assertEquals(savedSubscriptionRequest.getUuid(), subscriptionResponseObject.getSubscriptionIdentifier());
                    assertEquals("Subscription successfully created", subscriptionResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Subscription interface will return an HTTP Status
     * BAD_REQUEST if a validation error occurs.
     */
    @Test
    void testSubscriptionBadRequest() {
        doThrow(SecomValidationException.class).when(this.secomV2SubscriptionService).save(any(), any());

        webTestClient.post()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(subscriptionRequestObject), SubscriptionRequestObject.class))
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Subscription interface will return an HTTP Status
     * METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testSubscriptionMethodNotAllowed() {
        doThrow(SecomValidationException.class).when(this.secomV2SubscriptionService).save(any(), any());

        webTestClient.get()
                .uri("/api/secom" + SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Remove Subscription interface is configured properly
     * and returns the expected Remove Subscription Response Object output.
     */
    @Test
    void testRemoveSubscription() {
        doReturn(removeSubscriptionObject.getSubscriptionIdentifier()).when(this.secomV2SubscriptionService).delete(any());

        webTestClient.method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + REMOVE_SUBSCRIPTION_INTERFACE_PATH)
                        .queryParam("subscriptionIdentifier", this.removeSubscriptionObject.getSubscriptionIdentifier())
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(removeSubscriptionObject.getSubscriptionIdentifier()), UUID.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RemoveSubscriptionResponseObject.class)
                .consumeWith(response -> {
                    RemoveSubscriptionResponseObject removeSubscriptionResponseObject = response.getResponseBody();
                    assertNotNull(removeSubscriptionResponseObject);
                    assertEquals(String.format("Subscription %s removed", removeSubscriptionObject.getSubscriptionIdentifier()), removeSubscriptionResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Remove Subscription interface will return an HTTP
     * Status BAD_REQUEST if a validation error occurs.
     */
    @Test
    void testRemoveSubscriptionBadRequest() {
        doThrow(SecomValidationException.class).when(this.secomV2SubscriptionService).delete(any());

        webTestClient.method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path("/api/secom" + REMOVE_SUBSCRIPTION_INTERFACE_PATH)
                        .queryParam("subscriptionIdentifier", this.removeSubscriptionObject.getSubscriptionIdentifier())
                        .build())
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Remove Subscription interface will return an HTTP
     * Status METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testRemoveSubscriptionMethodNotAllowed() {
        doThrow(SecomValidationException.class).when(this.secomV2SubscriptionService).delete(any());

        webTestClient.get()
                .uri("/api/secom" + REMOVE_SUBSCRIPTION_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Test that the SECOM Acknowledgement interface is configured properly
     * and returns the expected Acknowledgement Response Object output.
     */
    @Test
    void testAcknowledgement() {
        webTestClient.method(HttpMethod.POST)
                .uri("/api/secom" + ACKNOWLEDGMENT_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(acknowledgementObject), AcknowledgementObject.class))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AcknowledgementResponseObject.class)
                .consumeWith(response -> {
                    AcknowledgementResponseObject acknowledgementResponseObject = response.getResponseBody();
                    assertNotNull(acknowledgementResponseObject);
                    assertEquals(String.format("Successfully received ACK for %s", acknowledgementObject.getEnvelope().getTransactionIdentifier()), acknowledgementResponseObject.getMessage());
                });
    }

    /**
     * Test that the SECOM Acknowledgement interface will return an HTTP
     * Status BAD_REQUEST if a validation error occurs.
     */
    @Test
    void testAcknowledgementBadRequest() {
        webTestClient.post()
                .uri("/api/secom" + ACKNOWLEDGMENT_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just("Invalid acknowledgement object"), String.class))
                .exchange()
                .expectStatus().isBadRequest();
    }

    /**
     * Test that the SECOM Acknowledgement interface will return an HTTP
     * Status METHOD_NOT_ALLOWED if a method other than a get is requested.
     */
    @Test
    void testAcknowledgementNotAllowed() {
        webTestClient.get()
                .uri("/api/secom" + ACKNOWLEDGMENT_INTERFACE_PATH)
                .header(SecomRequestHeaders.MRN_HEADER, "mrn")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}