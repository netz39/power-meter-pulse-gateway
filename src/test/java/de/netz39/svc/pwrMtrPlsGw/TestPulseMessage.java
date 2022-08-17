package de.netz39.svc.pwrMtrPlsGw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class TestPulseMessage {
    @Test
    public void testConstructor() {
        for (final long timestamp: Arrays.asList(-1L, 0L, 1660739950000L)){
            final PulseMessage pm = PulseMessage.withTimestamp(timestamp);
            assertNotNull(pm);
            assertEquals(timestamp, pm.getTimestamp());
        }
    }

    @Test
    public void testSerialization() {
        ObjectMapper mapper = new ObjectMapper();
        for (final long timestamp: Arrays.asList(-1L, 0L, 1660739950000L)) {
            // Test a valid JSON input
            final PulseMessage msg = PulseMessage.withTimestamp(timestamp);
            String json = assertDoesNotThrow(
                    () -> mapper.writeValueAsString(msg)
            );
            assertNotNull(json);
            assertEquals("{\"timestamp\":"+timestamp+"}", json);
        }

    }

    @Test
    public void testDeserialization() {
        ObjectMapper mapper = new ObjectMapper();

        for (final long timestamp: Arrays.asList(-1L, 0L, 1660739950000L)) {
            // Test a valid JSON input
            final PulseMessage msg = assertDoesNotThrow(
                    () -> mapper.readValue("{\"timestamp\":"+timestamp+"}",
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
