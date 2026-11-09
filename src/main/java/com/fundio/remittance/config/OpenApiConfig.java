
package com.fundio.remittance.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "FUN-DIO Remittance Rates API",
        version = "1.0.0",
        description = """
            REST API for real-time currency exchange rates
            and international remittance calculations.

            Features:
            - Retrieve exchange rates
            - Convert currencies
            - Check API health

            Exchange rate provider: Frankfurter
            """,
        license = @License(
            name = "Internal Use"
        )
    )
)
public class OpenApiConfig {
}
