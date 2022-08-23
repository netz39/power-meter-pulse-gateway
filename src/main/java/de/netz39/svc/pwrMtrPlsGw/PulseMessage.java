package de.netz39.svc.pwrMtrPlsGw;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.context.annotation.Bean;
import net.jcip.annotations.Immutable;

import java.time.Instant;

/**
 * Bean representation of a pulse message.
 *
 * <p>The pulse is represented by JSON of the following form:</p>
 * <code>
 *     {
 *         "timestamp": timestamp(long)
 *     }
 * </code>
 */
@Immutable
@Bean
public class PulseMessage {
    static PulseMessage withTimestamp(final Instant timestamp) {
        return new PulseMessage(timestamp);
    }

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private final Instant timestamp;

    PulseMessage(@JsonProperty(value="timestamp", required = true) Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
