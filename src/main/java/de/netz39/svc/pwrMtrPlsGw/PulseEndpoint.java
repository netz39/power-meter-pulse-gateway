package de.netz39.svc.pwrMtrPlsGw;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.rabbitmq.exception.RabbitClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.inject.Inject;
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

    @Property(name = "pulse-binding")
    String pulseDestination;

    @Inject
    AmqpEmitter emitter;


    @Post
    @Operation(summary = "Send a pulse message")
    @ApiResponse(responseCode = "201", description = "AMQP call successful")
    @ApiResponse(responseCode = "400", description = "Invalid call arguments")
    @ApiResponse(responseCode = "502", description = "Timeout or error while connecting to RabbitMQ, please retry later!")
    public HttpStatus pulse(@Body PulseMessage body,
                            @Parameter(description = "Authorization string as `Beaker <token>`, see README for details")
                            @Header(HttpHeaders.AUTHORIZATION) @Nullable String authorization) {
        // Check Authorization if configured
        if (! isValidAuth(authorization)) {
            LOGGER.warn("Unauthorized access!");
            return HttpStatus.UNAUTHORIZED;
        }

        LOGGER.debug("Received timestamp {}", body.getTimestamp());

        // c.f. exception handling in onRabbitClientException
        emitter.send(pulseDestination, body);

        return HttpStatus.CREATED;
    }

    boolean isValidAuth(final String authorization) {
        return apiToken == null ||
               apiToken.isEmpty() ||
               ("Bearer " + apiToken).equals(authorization);
    }

    @Error(exception=RabbitClientException.class)
    public HttpResponse<String> onRabbitClientException(RabbitClientException e) {
        return HttpResponse.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
    }
}
