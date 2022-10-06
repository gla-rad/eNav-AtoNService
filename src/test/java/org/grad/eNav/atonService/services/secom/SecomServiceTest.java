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

package org.grad.eNav.atonService.services.secom;

import org.grad.secom.core.exceptions.SecomValidationException;
import org.grad.secom.core.models.SearchObjectResult;
import org.grad.secom.springboot.components.SecomClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SecomServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    SecomService secomService;

    @Mock
    SecomClient discoveryService;

    // Test Variables
    SearchObjectResult[] instances;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Set the discovery service URL variable
        this.secomService.discoveryServiceUrl = "http://localhost:8444/v1/searchService";

        // Create a set of retrieved instances
        SearchObjectResult searchObjectResult1 = new SearchObjectResult();
        searchObjectResult1.setVersion("0.0.1");
        searchObjectResult1.setEndpointUri("http://localhost/");
        SearchObjectResult searchObjectResult2 = new SearchObjectResult();
        searchObjectResult2.setVersion("0.0.2");
        searchObjectResult2.setEndpointUri("http://localhost/");
        this.instances = new SearchObjectResult[]{searchObjectResult1, searchObjectResult2};
    }

    /**
     * That that during its initialisation the SECOM service will construct the
     * SECOM discovery service client.
     */
    @Test
    void testInit() {
        // Perform the service call
        this.secomService.init();

        // Make sure the discovery service was initialise properly
        assertNotNull(this.secomService.discoveryService);
    }

    /**
     * That that during its termination the SECOM service will destroy the
     * SECOM discovery service client.
     */
    @Test
    void testDestroy() {
        // Perform the service call
        this.secomService.init();
        this.secomService.destroy();

        // Make sure the discovery service was destroyed properly
        assertNull(this.secomService.discoveryService);
    }

    /**
     * Test that the SECOM service will contact the SECOM discovery service
     * allocated to it, to discover the requested clients based on their MRNs.
     */
    @Test
    void testGetClient() {
        // And mock a SECOM discovery service client
        this.secomService.discoveryService = mock(SecomClient.class);
        doReturn(Optional.of(this.instances)).when(this.secomService.discoveryService).search(any(), any(), any());

        // Perform the service call
        SecomClient result = this.secomService.getClient("urn:mrn:org:test");

        // Make sure the client seems OK
        assertNotNull(result);
    }

    /**
     * Test that the SECOM service will contact the SECOM discovery service
     * allocated to it, to discover the requested clients based on their MRNs.
     * If the discovered URL does not seem valid, a SecomValidationException
     * will be thrown.
     */
    @Test
    void testGetClientBrokenUrl() {
        // Break the URL of the latest instance
        this.instances[1].setEndpointUri("a broken URL");

        // And mock a SECOM discovery service client
        this.secomService.discoveryService = mock(SecomClient.class);
        doReturn(Optional.of(this.instances)).when(this.secomService.discoveryService).search(any(), any(), any());

        // Perform the service call
        assertThrows(SecomValidationException.class, () -> this.secomService.getClient("urn:mrn:org:test"));
    }

}