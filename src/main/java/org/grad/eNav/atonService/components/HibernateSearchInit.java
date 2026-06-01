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

package org.grad.eNav.atonService.components;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.grad.eNav.atonService.models.domain.DatasetContent;
import org.grad.eNav.atonService.models.domain.DatasetContentLog;
import org.grad.eNav.atonService.models.domain.s125.AidsToNavigation;
import org.grad.eNav.atonService.models.domain.s125.S125Dataset;
import org.grad.eNav.atonService.models.domain.s125.S125DatasetIdentification;
import org.grad.eNav.atonService.models.domain.secom.SubscriptionRequest;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.util.common.SearchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * The HibernateSearchInit Component Class
 *
 * This component initialises the Lucence search indexes for the database. This
 * is a persistent content that will remain available through the whole
 * application.
 *
 * @author Nikolaos Vastardis (email: Nikolaos.Vastardis@gla-rad.org)
 */
@Profile("!test")
@Component()
@Slf4j
public class HibernateSearchInit implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * The Entity Manager.
     */
    @PersistenceContext
    EntityManager em;

    /**
     * Override the application event handler to index the database.
     *
     * @param event the context refreshed event
     */
    @Override
    @Transactional
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        // Log the indexing process commencing
        log.info("Hibernate Search indexing commencing...");

        // Perform the indexing
        runMassIndex();
    }

    /**
     * Running the actual indexing operation asynchronously to
     * allow the service to continue.
     */
    @SneakyThrows
    @Async
    @Transactional
    public void runMassIndex() {
        Search.session(em)
                .massIndexer(Arrays.asList(
                        S125Dataset.class,
                        S125DatasetIdentification.class,
                        DatasetContent.class,
                        AidsToNavigation.class,
                        SubscriptionRequest.class,
                        DatasetContentLog.class))
                .threadsToLoadObjects(7)
                .startAndWait();

        // And log the result
        log.info("Hibernate Search indexing completed successfully");
    }

}
