/*
 * Copyright (c) 2025 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grad.eNav.atonService.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.ValidationException;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonService.components.SecomCertificateProviderImpl;
import org.grad.eNav.atonService.components.SecomSignatureProviderImpl;
import org.grad.eNav.atonService.models.UnLoCodeMapEntry;
import org.grad.eNav.atonService.models.domain.DatasetContent;
import org.grad.eNav.atonService.models.domain.s125.S125Dataset;
import org.grad.eNav.atonService.services.DatasetService;
import org.grad.eNav.atonService.services.S100ExchangeSetService;
import org.grad.eNav.atonService.services.UnLoCodeService;
import org.grad.eNav.atonService.utils.GeometryUtils;
import org.grad.eNav.atonService.utils.WKTUtils;
import org.grad.secom.core.base.DigitalSignatureCertificate;
import org.grad.secom.core.base.SecomConstants;
import org.grad.secom.core.models.*;
import org.grad.secom.core.models.enums.ContainerTypeEnum;
import org.grad.secom.core.models.enums.SECOM_DataProductType;
import org.grad.secom.core.utils.SecomPemUtils;
import org.hibernate.HibernateException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Alternative implementation of the GetSecomController interface
 * using Springboot to test the performance
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@Component
@RestController
@Validated
@Slf4j
public class GetSecomControllerTest
{
    String GET_INTERFACE_PATH = "/api/objectTest";
    /**
     * The Dataset Service.
     */
    @Autowired
    DatasetService datasetService;

    /**
     * The SECOM Exchange Set Service.
     */
    @Autowired
    S100ExchangeSetService s100ExchangeSetService;

    /**
     * The UN/LOCODE Service.
     */
    @Autowired
    UnLoCodeService unLoCodeService;

    @Autowired
    SecomCertificateProviderImpl secomCertificateProvider;

    @Autowired
    SecomSignatureProviderImpl secomSignatureProvider;

    // Class Variables
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);

    /**
     * GET /api/secom/v1/dataset : Returns the S-125 dataset entries as,
     * specified by the SECOM standard.
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
     * @return the S-125 dataset information
     */

    @GetMapping(value = "/api/objectTest", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tag(name = "SECOM")
    @Transactional
    public ResponseEntity<GetResponseObject> get(@RequestParam(required = false) UUID dataReference,
                                                 @RequestParam(required = false) ContainerTypeEnum containerType,
                                                 @RequestParam(required = false) SECOM_DataProductType dataProductType,
                                                 @RequestParam(required = false) String productVersion,
                                                 @RequestParam(required = false) String geometry,
                                                 @RequestParam(required = false) String unlocode,
                                                 @RequestParam(required = false) Instant validFrom,
                                                 @RequestParam(required = false) Instant validTo,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer pageSize) {

        log.debug("SECOM TEST request to get page of Dataset");
        Optional.ofNullable(dataReference).ifPresent(v -> log.debug("Data Reference specified as: {}", dataReference));
        Optional.ofNullable(containerType).ifPresent(v -> log.debug("Container Type specified as: {}", containerType));
        Optional.ofNullable(dataProductType).ifPresent(v -> log.debug("Data Product Type specified as: {}", dataProductType));
        Optional.ofNullable(geometry).ifPresent(v -> log.debug("Geometry specified as: {}", geometry));
        Optional.ofNullable(unlocode).ifPresent(v -> log.debug("UNLOCODE specified as: {}", unlocode));
        Optional.ofNullable(validFrom).ifPresent(v -> log.debug("Valid From time specified as: {}", validFrom));
        Optional.ofNullable(validTo).ifPresent(v -> log.debug("Valid To time specified as: {}", validTo));

        // Init local variables
        Geometry jtsGeometry = null;
        Pageable pageable = Optional.ofNullable(page)
                .map(p -> PageRequest.of(p, Optional.ofNullable(pageSize).orElse(Integer.MAX_VALUE)))
                .map(Pageable.class::cast)
                .orElse(Pageable.unpaged());
        LocalDateTime validFromLdt = Optional.ofNullable(validFrom)
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);
        LocalDateTime validToLdt = Optional.ofNullable(validTo)
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault()))
                .orElse(null);

        // Parse the arguments
        final ContainerTypeEnum reqContainerType = Optional.ofNullable(containerType)
                .orElse(ContainerTypeEnum.S100_DataSet);
        final SECOM_DataProductType reqDataProductType = Optional.ofNullable(dataProductType)
                .orElse(SECOM_DataProductType.S125);
        if(Objects.nonNull(geometry)) {
            try {
                jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, WKTUtils.convertWKTtoGeometry(geometry));
            } catch (ParseException ex) {
                //throw new ValidationException(ex.getMessage());
                log.error(ex.getMessage());
                //throw new RuntimeException(ex);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if(Objects.nonNull(unlocode)) {
            jtsGeometry = GeometryUtils.joinGeometries(jtsGeometry, Optional.ofNullable(unlocode)
                    .map(this.unLoCodeService::getUnLoCodeMapEntry)
                    .map(UnLoCodeMapEntry::getGeometry)
                    .orElseGet(() -> this.geometryFactory.createEmpty(0)));
        }

        // Initialise the data response object list
        final List<DataResponseObject> dataResponseObjectList = new ArrayList<>();

        // We only support specifically S-125 Datasets
        if(reqDataProductType == SECOM_DataProductType.S125) {
            // Retrieve all matching datasets
            Page<S125Dataset> result;
            try {
                result = this.datasetService.findAll(dataReference, jtsGeometry, validFromLdt, validToLdt, Boolean.FALSE, pageable);
            } catch (Exception ex) {
                log.error("Error while retrieving the dataset query results: {} ", ex.getMessage());
                //throw new RuntimeException(ex);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Package as S100 Datasets
            if(reqContainerType == ContainerTypeEnum.S100_DataSet) {
                try {
                    result.stream()
                            .map(S125Dataset::getDatasetContent)
                            .filter(Objects::nonNull)
                            .map(DatasetContent::getContent)
                            .map(String::getBytes)
                            .map(bytes -> {
                                // Create and populate the data response object
                                final DataResponseObject dataResponseObject = new DataResponseObject();
                                dataResponseObject.setData(Base64.getEncoder().encode(bytes));
                                //dataResponseObject.setData(bytes);

                                // And return the data response object
                                return dataResponseObject;
                            })
                            .forEach(dataResponseObjectList::add);
                } catch (HibernateException ex) {
                    log.error("Error while retrieving the dataset query results: {} ", ex.getMessage());
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }
            // Package as S100 Exchange Sets
            else if(reqContainerType == ContainerTypeEnum.S100_ExchangeSet) {
                // Create and populate the data response object
                final DataResponseObject dataResponseObject = new DataResponseObject();
                try {
                    dataResponseObject.setData(this.s100ExchangeSetService.packageToExchangeSet(result.getContent(), validFromLdt, validToLdt));
                } catch (IOException | JAXBException ex) {
                    log.error("Error while packaging the exchange set response: {} ", ex.getMessage());
                    //throw new ValidationException(ex.getMessage());
                    //throw new RuntimeException(ex);
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Flag that this is compressed in the exchange metadata
                dataResponseObject.setExchangeMetadata(new SECOM_ExchangeMetadataObject());
                dataResponseObject.getExchangeMetadata().setCompressionFlag(Boolean.TRUE);

                // And add it to the data response list
                dataResponseObjectList.add(dataResponseObject);
            }
        }

        // Generate the Get Response Object
        final GetResponseObject getResponseObject = new GetResponseObject();
        getResponseObject.setDataResponseObject(dataResponseObjectList);
        getResponseObject.setPagination(new PaginationObject(
                dataResponseObjectList.size(),
                Optional.ofNullable(pageSize).orElse(Integer.MAX_VALUE)));

        // Sign the Response object
        getResponseObject.getDataResponseObject().forEach(data -> {

            DigitalSignatureCertificate certificate = secomCertificateProvider.getDigitalSignatureCertificate();
            byte[] signature = secomSignatureProvider.generateSignature(certificate, secomSignatureProvider.getSignatureAlgorithm(), data.getData());
            data.getExchangeMetadata().setDataProtection(false);
            data.getExchangeMetadata().setProtectionScheme("SECOM");
            data.getExchangeMetadata().setDigitalSignatureReference(secomSignatureProvider.getSignatureAlgorithm());
            DigitalSignatureValue digitalSignatureValue = new DigitalSignatureValue();

            try {
                digitalSignatureValue.setDigitalSignature(Base64.getEncoder().encodeToString(signature));
                digitalSignatureValue.setPublicRootCertificateThumbprint(SecomPemUtils.getCertThumbprint(certificate.getCertificate(), SecomConstants.CERTIFICATE_THUMBPRINT_HASH));
                digitalSignatureValue.setPublicCertificate(Base64.getEncoder().encodeToString(certificate.getCertificate().getEncoded()));
            } catch (CertificateEncodingException | NoSuchAlgorithmException e){
                throw new RuntimeException(e);
            }

            data.getExchangeMetadata().setDigitalSignatureValue(digitalSignatureValue);

        });

        // And final return the Get Response Object
        return ResponseEntity.ok(getResponseObject);

    }
}
