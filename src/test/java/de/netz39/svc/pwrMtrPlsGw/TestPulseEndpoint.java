package de.netz39.svc.pwrMtrPlsGw;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.rabbitmq.exception.RabbitClientException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Singleton
@Replaces(AmqpEmitter.class)
class AmqpTestEmitter implements AmqpEmitter {
    public static RuntimeException except = null;
    public static String lastDestination = null;
    public static PulseMessage lastMessage = null;

    public static void reset() {
        except = null;
        lastDestination = null;
        lastMessage = null;
    }
    @Override
    public void send(String destination, PulseMessage pulseMessage) {
        lastDestination = destination;
        lastMessage = pulseMessage;

        if (except != null)
            throw except;
    }
}


@MicronautTest(rebuildContext = true)
public class TestPulseEndpoint {
    @Inject
    @Client("/")
    HttpClient client;

    @BeforeEach
    public void resetAmqpTestEmitter() {
        AmqpTestEmitter.reset();
    }

    @Test
    public void testValid() {
        final PulseMessage msg =  PulseMessage.withTimestamp(Instant.EPOCH);
        final HttpRequest<PulseMessage> req = HttpRequest.POST("/pulse", msg);
        assertDoesNotThrow(
                () -> client.toBlocking().exchange(req)
        );

        assertEquals(msg.getTimestamp(), AmqpTestEmitter.lastMessage.getTimestamp());
        assertEquals("pulses", AmqpTestEmitter.lastDestination);
    }

    // Ignoring an authorisation token when none is expected is a feature of the current implementation,
    // but not promised by the API. We do not test this case and the behavior may change in the future.

    @Test
    public void testInvalidBody() {
        final HttpRequest<String> req = HttpRequest.POST("/pulse", "{}");
        final HttpClientResponseException e = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(req)
        );
        assertEquals(HttpStatus.BAD_REQUEST.getCode(), e.getStatus().getCode());
    }

    @Test
    public void testMissingRabbitMQ() {
        AmqpTestEmitter.except = new RabbitClientException("Test Exception");

        final PulseMessage msg =  PulseMessage.withTimestamp(Instant.EPOCH);
        final HttpRequest<PulseMessage> req = HttpRequest.POST("/pulse", msg);

        final HttpClientResponseException e = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(req)
        );
        assertEquals(HttpStatus.BAD_GATEWAY.getCode(), e.getStatus().getCode());
    }

    @Test
    @Property(name = "api-token", value = "abc")
    public void TestValidAuth() {
        final PulseMessage msg =  PulseMessage.withTimestamp(Instant.EPOCH);
        final MutableHttpRequest<PulseMessage> req = HttpRequest.POST("/pulse", msg).bearerAuth("abc");
        assertDoesNotThrow(
                () -> client.toBlocking().exchange(req)
        );

        assertEquals(msg.getTimestamp(), AmqpTestEmitter.lastMessage.getTimestamp());
    }

    @Test
    @Property(name = "api-token", value = "abc")
    public void TestInvalidAuth() {
        final PulseMessage msg =  PulseMessage.withTimestamp(Instant.EPOCH);
        final MutableHttpRequest<PulseMessage> req = HttpRequest.POST("/pulse", msg);
        final HttpClientResponseException e = assertThrows(
                HttpClientResponseException.class,
                () -> client.toBlocking().exchange(req)
        );
        assertEquals(HttpStatus.UNAUTHORIZED.getCode(), e.getStatus().getCode());
    }

}
