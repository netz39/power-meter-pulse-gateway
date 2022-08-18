package de.netz39.svc.pwrMtrPlsGw;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;
import io.micronaut.rabbitmq.annotation.RabbitProperty;

@RabbitClient
public interface AmqpEmitter {
    @RabbitProperty(name = "contentType", value = "application/json")
    void send(@Binding String destination,
              PulseMessage pulseMessage);
}
