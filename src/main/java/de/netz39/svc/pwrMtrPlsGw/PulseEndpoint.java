package de.netz39.svc.pwrMtrPlsGw;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This controller accepts a PulseMessage body on <code>POST /pulse</code>.
 *
 * <p>A bearer token must be provided as `Authorization` if configured.</p>
 */
@Controller("/pulse")
public class PulseEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(PulseEndpoint.class);

    @Property(name="api-token")
    String apiToken;

    @Post
    @ApiResponse(responseCode = "201", description = "AMQP call successful")
    @ApiResponse(responseCode = "400", description = "Invalid call arguments")
    @ApiResponse(responseCode = "504", description = "Timeout waiting for backend response")
    public HttpStatus pulse(@Body PulseMessage body,
                            @Header(HttpHeaders.AUTHORIZATION) @Nullable String authorization) {
        // Check Authorization if configured
        if (! isValidAuth(authorization)) {
            LOGGER.warn("Unauthorized access!");
            return HttpStatus.UNAUTHORIZED;
        }

        LOGGER.info("Received timestamp {}", body.getTimestamp());

        // TODO AMQP call

        return HttpStatus.CREATED;
    }

    boolean isValidAuth(final String authorization) {
        return apiToken == null ||
               apiToken.isEmpty() ||
               ("Bearer " + apiToken).equals(authorization);
    }
}
