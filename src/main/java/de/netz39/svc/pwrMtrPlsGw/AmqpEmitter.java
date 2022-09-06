package de.netz39.svc.pwrMtrPlsGw;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;
import io.micronaut.rabbitmq.annotation.RabbitProperty;

@RabbitClient
public interface AmqpEmitter {
    @RabbitProperty(name = "contentType", value = "application/json")
    @RabbitProperty(name = "deliveryMode", value = "2") // Durable messages
    void send(@Binding String destination,
              PulseMessage pulseMessage);
}
