/*
 * Copyright (c) 2023 GLA Research and Development Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.grad.eNav.atonService.services;

import jakarta.persistence.EntityManager;
import org.grad.eNav.atonService.exceptions.DataNotFoundException;
import org.grad.eNav.atonService.models.domain.DatasetContent;
import org.grad.eNav.atonService.models.domain.DatasetContentLog;
import org.grad.eNav.atonService.models.domain.s125.S125Dataset;
import org.grad.eNav.atonService.models.dtos.datatables.*;
import org.grad.eNav.atonService.models.enums.DatasetOperation;
import org.grad.eNav.atonService.models.enums.DatasetType;
import org.grad.eNav.atonService.repos.DatasetContentLogRepo;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.SearchResultTotal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DatasetContentLogServiceTest {

    /**
     * The Tested Service.
     */
    @InjectMocks
    @Spy
    DatasetContentLogService datasetContentLogService;

    /**
     * The Entity Manager mock.
     */
    @Mock
    EntityManager entityManager;

    /**
     * The Dataset Service mock.
     */
    @Mock
    DatasetService datasetService;

    /**
     * The Dataset Content Repo mock.
     */
    @Mock
    DatasetContentLogRepo datasetContentLogRepo;

    // Test Variables
    private Pageable pageable;
    private List<DatasetContentLog> datasetContentLogList;
    private S125Dataset s125Dataset;
    private DatasetContentLog newDatasetContentLog;
    private DatasetContentLog existingDatasetContentLog;
    private DatasetContentLog deltaDatasetContentLog;

    /**
     * Common setup for all the tests.
     */
    @BeforeEach
    void setUp() {
        // Create a temp geometry factory to get a test geometries
        // Test Variables
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

        // Create a pageable definition
        this.pageable = PageRequest.of(0, 5);

        // Initialise the Dataset Content Log list
        this.datasetContentLogList = new ArrayList<>();
        for(long i=0; i<10; i++) {
            DatasetContentLog datasetContentLog = new DatasetContentLog();
            datasetContentLog.setId(BigInteger.valueOf(i));
            datasetContentLog.setDatasetType(DatasetType.S125);
            datasetContentLog.setSequenceNo(BigInteger.ONE);
            datasetContentLog.setGeneratedAt(LocalDateTime.now());
            datasetContentLog.setGeometry(factory.createPoint(new Coordinate(i%180, i%90)));
            datasetContentLog.setOperation(DatasetOperation.UPDATED);
            datasetContentLog.setContent("Existing Dataset Content " + i);
            datasetContentLog.setContentLength(BigInteger.valueOf(datasetContentLog.getContent().length()));
            this.datasetContentLogList.add(datasetContentLog);
        }

        // Create a Dataset with a UUID
        this.s125Dataset = new S125Dataset("ExistingDataset");
        this.s125Dataset.setUuid(UUID.randomUUID());
        this.s125Dataset.setCreatedAt(LocalDateTime.now());
        this.s125Dataset.setLastUpdatedAt(LocalDateTime.now());
        this.s125Dataset.setGeometry(factory.createPoint(new Coordinate(52.98, 2.28)));
        this.s125Dataset.setDatasetContent(new DatasetContent());
        this.s125Dataset.getDatasetContent().setSequenceNo(BigInteger.ONE);
        this.s125Dataset.getDatasetContent().setContent("Existing Dataset Content");

        // Create a new Dataset Content Log entry
        this.newDatasetContentLog = new DatasetContentLog();
        this.newDatasetContentLog.setDatasetType(DatasetType.S125);
        this.newDatasetContentLog.setSequenceNo(BigInteger.ZERO);
        this.newDatasetContentLog.setGeneratedAt(LocalDateTime.now());
        this.newDatasetContentLog.setGeometry(this.s125Dataset.getGeometry());
        this.newDatasetContentLog.setOperation(DatasetOperation.CREATED);
        this.newDatasetContentLog.setContent("New Dataset Content");
        this.newDatasetContentLog.setContentLength(BigInteger.valueOf(this.newDatasetContentLog.getContent().length()));

        // Create an existing Dataset Content Log entry
        this.existingDatasetContentLog = new DatasetContentLog();
        this.existingDatasetContentLog.setId(BigInteger.ONE);
        this.existingDatasetContentLog.setDatasetType(DatasetType.S125);
        this.existingDatasetContentLog.setSequenceNo(BigInteger.ONE);
        this.existingDatasetContentLog.setGeneratedAt(LocalDateTime.now());
        this.existingDatasetContentLog.setGeometry(this.s125Dataset.getGeometry());
        this.existingDatasetContentLog.setOperation(DatasetOperation.UPDATED);
        this.existingDatasetContentLog.setContent("Existing Dataset Content");
        this.existingDatasetContentLog.setContentLength(BigInteger.valueOf(this.existingDatasetContentLog.getContent().length()));
        this.existingDatasetContentLog.setDelta("Existing Dataset Content Delta");
        this.existingDatasetContentLog.setDeltaLength(BigInteger.valueOf(this.existingDatasetContentLog.getDelta().length()));

        // Create another existing Dataset Content Log containing a delta
        this.deltaDatasetContentLog = new DatasetContentLog();
        this.deltaDatasetContentLog.setId(BigInteger.TWO);
        this.deltaDatasetContentLog.setDatasetType(DatasetType.S125);
        this.deltaDatasetContentLog.setSequenceNo(BigInteger.TWO);
        this.deltaDatasetContentLog.setGeneratedAt(LocalDateTime.now());
        this.deltaDatasetContentLog.setGeometry(this.s125Dataset.getGeometry());
        this.deltaDatasetContentLog.setOperation(DatasetOperation.UPDATED);
        this.deltaDatasetContentLog.setContent("Another Dataset Content");
        this.deltaDatasetContentLog.setContentLength(BigInteger.valueOf(this.deltaDatasetContentLog.getContent().length()));
        this.deltaDatasetContentLog.setDelta("Another Dataset Content Delta");
        this.deltaDatasetContentLog.setDeltaLength(BigInteger.valueOf(this.deltaDatasetContentLog.getDelta().length()));
    }

    /**
     * Test that we can successfully retrieve a specific dataset content log
     * if the log's ID is provided.
     */
    @Test
    void testFindOne() {
        doReturn(Optional.of(this.existingDatasetContentLog)).when(this.datasetContentLogRepo).findById(any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.findOne(this.existingDatasetContentLog.getId());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDatasetContentLog.getId(), result.getId());
        assertEquals(this.existingDatasetContentLog.getUuid(), result.getUuid());
        assertEquals(this.existingDatasetContentLog.getDatasetType(), result.getDatasetType());
        assertEquals(this.existingDatasetContentLog.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.existingDatasetContentLog.getGeometry(), result.getGeometry());
        assertEquals(this.existingDatasetContentLog.getOperation(), result.getOperation());
        assertEquals(this.existingDatasetContentLog.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.existingDatasetContentLog.getContent(), result.getContent());
        assertEquals(this.existingDatasetContentLog.getContentLength(), result.getContentLength());
    }

    /**
     * Test that if the provided dataset content log ID does not exist, then
     * the retrieval function will throw a DataNotFound exception.
     */
    @Test
    void testFindOneNotFound() {
        doReturn(Optional.empty()).when(this.datasetContentLogRepo).findById(any());

        // Perform the service call
        assertThrows(
                DataNotFoundException.class,
                ()-> this.datasetContentLogService.findOne(this.existingDatasetContentLog.getId())
        );
    }

    /**
     * Test that we can successfully retrieve the original dataset content log
     * (i.e. the one with sequence number equal to ZERO (0)), by providing an
     * existing UUID identifier.
     */
    @Test
    void testFindOriginal() {
        doReturn(Optional.of(this.existingDatasetContentLog)).when(this.datasetContentLogRepo).findOriginalForUuid(any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.findOriginal(this.s125Dataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDatasetContentLog.getId(), result.getId());
        assertEquals(this.existingDatasetContentLog.getUuid(), result.getUuid());
        assertEquals(this.existingDatasetContentLog.getDatasetType(), result.getDatasetType());
        assertEquals(this.existingDatasetContentLog.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.existingDatasetContentLog.getGeometry(), result.getGeometry());
        assertEquals(this.existingDatasetContentLog.getOperation(), result.getOperation());
        assertEquals(this.existingDatasetContentLog.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.existingDatasetContentLog.getContent(), result.getContent());
        assertEquals(this.existingDatasetContentLog.getContentLength(), result.getContentLength());
    }

    /**
     * Test that if we request the optional entry of the content log for an
     * invalid UUID, then the service will return null.
     */
    @Test
    void testFindOriginalNotFound() {
        doReturn(Optional.empty()).when(this.datasetContentLogRepo).findOriginalForUuid(any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.findOriginal(this.s125Dataset.getUuid());

        // Test the result
        assertNull(result);
    }

    /**
     * Test that we can correctly retrieve the delta dataset content log
     * entries, i.e. the entries that have a sequence number larger than
     * ZERO (0).
     */
    @Test
    void testFindDeltas() {
        doReturn(Arrays.asList(
                this.existingDatasetContentLog,
                this.deltaDatasetContentLog)
        ).when(this.datasetContentLogRepo).findByUuid(any());

        // Perform the service call
        List<DatasetContentLog> result = this.datasetContentLogService.findDeltas(this.s125Dataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(BigInteger.ONE, result.get(0).getSequenceNo());
        assertEquals(BigInteger.TWO, result.get(1).getSequenceNo());
    }

    /**
     * Test that we can find the latest content log of a dataset specified by its
     * dataset UUID, without requiring a reference date-time.
     */
    @Test
    void testFindLatest() {
        doReturn(Collections.singletonList(this.existingDatasetContentLog)).when(this.datasetContentLogRepo).findLatestForUuid(any(), any(), any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.findLatest(this.s125Dataset.getUuid());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDatasetContentLog.getId(), result.getId());
        assertEquals(this.existingDatasetContentLog.getUuid(), result.getUuid());
        assertEquals(this.existingDatasetContentLog.getDatasetType(), result.getDatasetType());
        assertEquals(this.existingDatasetContentLog.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.existingDatasetContentLog.getGeometry(), result.getGeometry());
        assertEquals(this.existingDatasetContentLog.getOperation(), result.getOperation());
        assertEquals(this.existingDatasetContentLog.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.existingDatasetContentLog.getContent(), result.getContent());
        assertEquals(this.existingDatasetContentLog.getContentLength(), result.getContentLength());
    }

    /**
     * Test that we can find the latest content log of a dataset specified by its
     * dataset UUID, as well as a reference date-time.
     */
    @Test
    void testFindLatestWithReferenceDateTime() {
        doReturn(Collections.singletonList(this.existingDatasetContentLog)).when(this.datasetContentLogRepo).findLatestForUuid(any(), any(), any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.findLatest(this.s125Dataset.getUuid(), LocalDateTime.now());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDatasetContentLog.getId(), result.getId());
        assertEquals(this.existingDatasetContentLog.getUuid(), result.getUuid());
        assertEquals(this.existingDatasetContentLog.getDatasetType(), result.getDatasetType());
        assertEquals(this.existingDatasetContentLog.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.existingDatasetContentLog.getGeometry(), result.getGeometry());
        assertEquals(this.existingDatasetContentLog.getOperation(), result.getOperation());
        assertEquals(this.existingDatasetContentLog.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.existingDatasetContentLog.getContent(), result.getContent());
        assertEquals(this.existingDatasetContentLog.getContentLength(), result.getContentLength());
    }

    /**
     * Test that we can find the latest content log of a dataset specified by its
     * dataset UUID, even if it does not exist, since it will be autogenerated
     * on the fly.
     */
    @Test
    void testFindLatestIfNotExists() {
        doReturn(Collections.emptyList()).when(this.datasetContentLogRepo).findLatestForUuid(any(), any(), any());
        doReturn(this.s125Dataset).when(this.datasetService).findOne(eq(this.s125Dataset.getUuid()));
        doReturn(this.existingDatasetContentLog).when(this.datasetContentLogRepo).save(any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.findLatest(this.s125Dataset.getUuid(), LocalDateTime.now());

        // Test the result
        assertNotNull(result);
        assertEquals(this.existingDatasetContentLog.getId(), result.getId());
        assertEquals(this.existingDatasetContentLog.getUuid(), result.getUuid());
        assertEquals(this.existingDatasetContentLog.getDatasetType(), result.getDatasetType());
        assertEquals(this.existingDatasetContentLog.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.existingDatasetContentLog.getGeometry(), result.getGeometry());
        assertEquals(this.existingDatasetContentLog.getOperation(), result.getOperation());
        assertEquals(this.existingDatasetContentLog.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.existingDatasetContentLog.getContent(), result.getContent());
        assertEquals(this.existingDatasetContentLog.getContentLength(), result.getContentLength());
    }

    /**
     * Test that we can search for all the datasets currently present in the
     * database and matching the provided criteria, through a paged call.
     */
    @Test
    void testFindAllPaged() {
        // Mock the repository query
        doAnswer((inv) -> new PageImpl<>(this.datasetContentLogList, this.pageable, this.datasetContentLogList.size()))
                .when(this.datasetContentLogRepo)
                .findAll(any(Pageable.class));

        // Perform the service call
        Page<DatasetContentLog> result = this.datasetContentLogService.findAll(pageable);

        // Test the result
        assertNotNull(result);
        assertEquals(5, result.getSize());

        // Test each of the result entries
        for(int i=0; i < result.getSize(); i++){
            assertNotNull(result.getContent().get(i));
            assertEquals(this.datasetContentLogList.get(i).getId(), result.getContent().get(i).getId());
            assertEquals(this.datasetContentLogList.get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(this.datasetContentLogList.get(i).getDatasetType(), result.getContent().get(i).getDatasetType());
            assertEquals(this.datasetContentLogList.get(i).getGeneratedAt(), result.getContent().get(i).getGeneratedAt());
            assertEquals(this.datasetContentLogList.get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertEquals(this.datasetContentLogList.get(i).getOperation(), result.getContent().get(i).getOperation());
            assertEquals(this.datasetContentLogList.get(i).getSequenceNo(), result.getContent().get(i).getSequenceNo());
            assertEquals(this.datasetContentLogList.get(i).getContent(), result.getContent().get(i).getContent());
            assertEquals(this.datasetContentLogList.get(i).getContentLength(), result.getContent().get(i).getContentLength());
        }
    }

    /**
     * Test that we can retrieve the paged list of datatable entries for a
     * Datatables pagination request (which by the way also includes search and
     * sorting definitions).
     */
    @Test
    void testHandleDatatablesPagingRequest() {
        // First create the pagination request
        DtPagingRequest dtPagingRequest = new DtPagingRequest();
        dtPagingRequest.setStart(0);
        dtPagingRequest.setLength(5);

        // Set the pagination request columns
        dtPagingRequest.setColumns(new ArrayList());
        Stream.of("id", "datasetType", "uuid","operation","sequenceNo")
                .map(DtColumn::new)
                .forEach(dtPagingRequest.getColumns()::add);

        // Set the pagination request ordering
        DtOrder dtOrder = new DtOrder();
        dtOrder.setColumn(0);
        dtOrder.setDir(DtDirection.asc);
        dtPagingRequest.setOrder(Collections.singletonList(dtOrder));

        // Set the pagination search
        DtSearch dtSearch = new DtSearch();
        dtSearch.setValue("search-term");
        dtPagingRequest.setSearch(dtSearch);

        // Mock the full text query
        SearchQuery<?> mockedQuery = mock(SearchQuery.class);
        SearchResult<?> mockedResult = mock(SearchResult.class);
        SearchResultTotal mockedResultTotal = mock(SearchResultTotal.class);
        doReturn(5L).when(mockedResultTotal).hitCount();
        doReturn(mockedResultTotal).when(mockedResult).total();
        doReturn(this.datasetContentLogList.subList(0, 5)).when(mockedResult).hits();
        doReturn(mockedResult).when(mockedQuery).fetch(any(), any());
        doReturn(mockedQuery).when(this.datasetContentLogService).getDatasetContentLogSearchQueryByText(any(), any());

        // Perform the service call
        Page<DatasetContentLog> result = this.datasetContentLogService.handleDatatablesPagingRequest(dtPagingRequest);

        // Validate the result
        assertNotNull(result);
        assertEquals(5, result.getSize());

        // Test each of the result entries
        for(int i=0; i < result.getSize(); i++){
            assertNotNull(result.getContent().get(i));
            assertEquals(this.datasetContentLogList.get(i).getId(), result.getContent().get(i).getId());
            assertEquals(this.datasetContentLogList.get(i).getUuid(), result.getContent().get(i).getUuid());
            assertEquals(this.datasetContentLogList.get(i).getDatasetType(), result.getContent().get(i).getDatasetType());
            assertEquals(this.datasetContentLogList.get(i).getGeneratedAt(), result.getContent().get(i).getGeneratedAt());
            assertEquals(this.datasetContentLogList.get(i).getGeometry(), result.getContent().get(i).getGeometry());
            assertEquals(this.datasetContentLogList.get(i).getOperation(), result.getContent().get(i).getOperation());
            assertEquals(this.datasetContentLogList.get(i).getSequenceNo(), result.getContent().get(i).getSequenceNo());
            assertEquals(this.datasetContentLogList.get(i).getContent(), result.getContent().get(i).getContent());
            assertEquals(this.datasetContentLogList.get(i).getContentLength(), result.getContent().get(i).getContentLength());
        }
    }

    /**
     * Test that we can successfully save a new dataset content log into the
     * database and the updated result will be returned.
     */
    @Test
    void testSave() {
        doReturn(this.newDatasetContentLog).when(this.datasetContentLogRepo).save(any());

        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.save(this.newDatasetContentLog);

        // Test the result
        assertNotNull(result);
        assertEquals(this.newDatasetContentLog.getId(), result.getId());
        assertEquals(this.newDatasetContentLog.getUuid(), result.getUuid());
        assertEquals(this.newDatasetContentLog.getDatasetType(), result.getDatasetType());
        assertEquals(this.newDatasetContentLog.getGeneratedAt(), result.getGeneratedAt());
        assertEquals(this.newDatasetContentLog.getGeometry(), result.getGeometry());
        assertEquals(this.newDatasetContentLog.getOperation(), result.getOperation());
        assertEquals(this.newDatasetContentLog.getSequenceNo(), result.getSequenceNo());
        assertEquals(this.newDatasetContentLog.getContent(), result.getContent());
        assertEquals(this.newDatasetContentLog.getContentLength(), result.getContentLength());
    }

    /**
     * Test that we can successfully generate the content log of a dataset
     * provided it already has a content generated. Note that the dataset
     * ID is set to null since this is a new content object and hasn't been
     * store in the database yet.
     */
    @Test
    void testGenerateDatasetContent() {
        // Perform the service call
        DatasetContentLog result = this.datasetContentLogService.generateDatasetContentLog(this.s125Dataset, DatasetOperation.OTHER);

        // Test the result
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(this.s125Dataset.getUuid(), result.getUuid());
        assertEquals(DatasetType.S125, result.getDatasetType());
        assertNotNull(result.getGeneratedAt());
        assertEquals(this.s125Dataset.getGeometry(), result.getGeometry());
        assertEquals(DatasetOperation.OTHER, result.getOperation());
        assertEquals(this.s125Dataset.getDatasetContent().getSequenceNo(), result.getSequenceNo());
        assertEquals(this.s125Dataset.getDatasetContent().getContent(), result.getContent());
    }

}