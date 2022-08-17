package de.netz39.svc.pwrMtrPlsGw;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.context.annotation.Bean;
import net.jcip.annotations.Immutable;

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
    static PulseMessage withTimestamp(final long timestamp) {
        return new PulseMessage(timestamp);
    }

    @JsonProperty("timestamp")
    private final long timestamp;

    PulseMessage(@JsonProperty(value="timestamp", required = true) long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
