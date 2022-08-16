package de.netz39.svc.pwrMtrPlsGw;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Power Meter Pulse Gateway",
                version = "0.1",
                description = "Gateway for receiving power meter pulses from an ESP32",
                license = @License(name = "MIT", url = "https://github.com/netz39/power-meter-pulse-gateway/blob/main/LICENSE.txt"),
                contact = @Contact(name = "Netz39 e.V.", email = "kontakt@netz39.de")
        )
)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
