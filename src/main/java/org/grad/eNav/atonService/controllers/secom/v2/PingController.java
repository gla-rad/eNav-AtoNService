/*
 * Copyright (c) 2024 GLA Research and Development Directorate
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
package org.grad.eNav.atonService.controllers.secom.v2;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;
import org.grad.secomv2.core.interfaces.PingServiceInterface;
import org.grad.secomv2.core.models.PingResponseObject;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

/**
 * The SECOM Ping Service Interface Controller.
 *
 * @author Lawrence Hughes (email: Lawrence.Hughes@gla-rad.org)
 */
@Component
@Path("/")
@Validated
@Slf4j
public class PingController implements PingServiceInterface {

    /**
     * GET /api/secom/v2/ping : returns a PingResponseObject which
     * holds the last private interaction time
     */
    @Tag(name = "SECOM")
    public PingResponseObject ping() {

        PingResponseObject pingResponseObject = new PingResponseObject();
        pingResponseObject.setLastPrivateInteractionTime(Instant.now());
        log.debug("PingSecomController called");
        return pingResponseObject;

    }
}
