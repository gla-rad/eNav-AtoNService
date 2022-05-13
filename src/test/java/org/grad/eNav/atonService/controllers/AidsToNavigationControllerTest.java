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

package org.grad.eNav.atonService.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.grad.eNav.atonService.TestingConfiguration;
import org.grad.eNav.atonService.exceptions.DataNotFoundException;
import org.grad.eNav.atonService.models.domain.s125.AidsToNavigation;
import org.grad.eNav.atonService.models.domain.s125.BeaconCardinal;
import org.grad.eNav.atonService.models.dtos.datatables.*;
import org.grad.eNav.atonService.models.dtos.s125.AidsToNavigationDto;
import org.grad.eNav.atonService.services.AidsToNavigationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.cloud.openfeign.support.SortJacksonModule;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AidsToNavigationController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(TestingConfiguration.class)
class AidsToNavigationControllerTest {

    /**
     * The Mock MVC.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * The JSON Object Mapper.
     */
    @Autowired
    ObjectMapper objectMapper;

    /**
     * The Aids To Navigation Service mock.
     */
    @MockBean
    AidsToNavigationService aidsToNavigationService;

    // Test Variables
    private List<AidsToNavigation> aidsToNavigationList;
    private Pageable pageable;
    private AidsToNavigation existingAidsToNavigation;
    private GeometryFactory factory;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        this.factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Allow the object mapper to deserialize pages
        this.objectMapper.registerModule(new PageJacksonModule());
        this.objectMapper.registerModule(new SortJacksonModule());

        // Initialise the station nodes list
        this.aidsToNavigationList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            AidsToNavigation node = new BeaconCardinal();
            node.setId(BigInteger.valueOf(i));
            node.setAtonNumber("AtonNumber" + i);
            node.setIdCode("ID"+i);
            node.setTextualDescription("Description of AtoN No" + i);
            node.setTextualDescriptionInNationalLanguage("National Language Description of AtoN No" + i);
            node.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            this.aidsToNavigationList.add(node);
        }

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);

        // Create a AtoN message with an ID
        existingAidsToNavigation = new BeaconCardinal();
        existingAidsToNavigation.setId(BigInteger.valueOf(1));
        existingAidsToNavigation.setAtonNumber("AtonNumber001");
        existingAidsToNavigation.setIdCode("ID001");
        existingAidsToNavigation.setTextualDescription("Description of AtoN No 1");
        existingAidsToNavigation.setTextualDescriptionInNationalLanguage("National Language Description of AtoN No 1" );
        existingAidsToNavigation.setGeometry(factory.createPoint(new Coordinate(1, 1)));
    }

    /**
     * Test that we can retrieve the Aids to Navigation currently in the database
     * in a paged result.
     */
    @Test
    void testGetAidsToNavigation() throws Exception {
        // Created a result page to be returned by the mocked service
        Page<AidsToNavigation> page = new PageImpl<>(this.aidsToNavigationList.subList(0, 5), this.pageable, this.aidsToNavigationList.size());
        doReturn(page).when(this.aidsToNavigationService).findAll(any(), any(), any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(get("/api/atons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Parse and validate the response
        Page<AidsToNavigationDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getContent().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertEquals(page.getContent().get(i).getId(), result.getContent().get(i).getId());
            assertEquals(page.getContent().get(i).getAtonNumber(), result.getContent().get(i).getAtonNumber());
            assertEquals(page.getContent().get(i).getIdCode(), result.getContent().get(i).getIdCode());
            assertEquals(page.getContent().get(i).getTextualDescription(), result.getContent().get(i).getTextualDescription());
            assertEquals(page.getContent().get(i).getTextualDescriptionInNationalLanguage(), result.getContent().get(i).getTextualDescriptionInNationalLanguage());
        }
    }

    /**
     * Test that the API supports the jQuery Datatables server-side paging
     * and search requests.
     */
    @Test
    void testGetNodesForDatatables() throws Exception {
        // Create a test datatables paging request
        DtColumn dtColumn = new DtColumn("id");
        dtColumn.setName("ID");
        dtColumn.setOrderable(true);
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(this.aidsToNavigationList.size());
        dtPagingRequest.setDraw(1);
        dtPagingRequest.setSearch(new DtSearch());
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));
        dtPagingRequest.setColumns(Collections.singletonList(dtColumn));

        // Created a result page to be returned by the mocked service
        Page<AidsToNavigation> page = new PageImpl<>(this.aidsToNavigationList.subList(0, 5), this.pageable, this.aidsToNavigationList.size());
        doReturn(page).when(this.aidsToNavigationService).handleDatatablesPagingRequest(any());

        // Perform the MVC request
        MvcResult mvcResult = this.mockMvc.perform(post("/api/atons/dt")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(dtPagingRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse and validate the response
        DtPage<AidsToNavigationDto> result = this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(page.getSize(), result.getData().size());

        // Validate the entries one by one
        for(int i=0; i< page.getSize(); i++) {
            assertEquals(page.getContent().get(i).getId(), result.getData().get(i).getId());
            assertEquals(page.getContent().get(i).getAtonNumber(), result.getData().get(i).getAtonNumber());
            assertEquals(page.getContent().get(i).getIdCode(), result.getData().get(i).getIdCode());
            assertEquals(page.getContent().get(i).getTextualDescription(), result.getData().get(i).getTextualDescription());
            assertEquals(page.getContent().get(i).getTextualDescriptionInNationalLanguage(), result.getData().get(i).getTextualDescriptionInNationalLanguage());
        }
    }

    /**
     * Test that we can correctly delete an existing Aids to Navigation by using
     * a valid ID.
     */
    @Test
    void testDeleteAidsToNavigation() throws Exception {
        // Perform the MVC request
        this.mockMvc.perform(delete("/api/atons/{id}", this.existingAidsToNavigation.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * Test that if we do NOT find the Aids to Navigation we are trying to
     * delete, an HTTP NOT_FOUND response will be returned.
     */
    @Test
    void testDeleteAidsToNavigationNotFound() throws Exception {
        doThrow(DataNotFoundException.class).when(this.aidsToNavigationService).delete(any());

        // Perform the MVC request
        this.mockMvc.perform(delete("/api/aton/{id}", this.existingAidsToNavigation.getId()))
                .andExpect(status().isNotFound());
    }

}