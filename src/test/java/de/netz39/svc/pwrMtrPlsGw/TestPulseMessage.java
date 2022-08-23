package de.netz39.svc.pwrMtrPlsGw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class TestPulseMessage {
    @Test
    public void testConstructor() {
        for (final Instant timestamp: Arrays.asList(Instant.EPOCH, Instant.now())){
            final PulseMessage pm = PulseMessage.withTimestamp(timestamp);
            assertNotNull(pm);
            assertEquals(timestamp, pm.getTimestamp());
        }
    }

    @Test
    public void testSerialization() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        for (final Instant timestamp: Arrays.asList(Instant.EPOCH, Instant.now())){
            // Test a valid JSON input
            final PulseMessage msg = PulseMessage.withTimestamp(timestamp);
            String json = assertDoesNotThrow(
                    () -> mapper.writeValueAsString(msg)
            );
            assertNotNull(json);
            assertEquals("{\"timestamp\":\""+timestamp+"\"}", json);
        }

    }

    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        for (final Instant timestamp: Arrays.asList(Instant.EPOCH, Instant.now())){
            // Test a valid JSON input
            final PulseMessage msg = assertDoesNotThrow(
                    () -> mapper.readValue("{\"timestamp\":\""+timestamp+"\"}",
                            PulseMessage.class)
            );
            assertNotNull(msg);
            assertEquals(timestamp, msg.getTimestamp());
        }

        // Test with missing timestamp
        assertThrows(
                MismatchedInputException.class,
                () -> mapper.readValue("{}", PulseMessage.class)
        );

    }
}
