package nl.gerimedica.assignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gerimedica Hospital Management API")
                        .version("1.0.0")
                        .description("REST API for managing patients and their appointments in the hospital system. " +
                                "This API provides endpoints for creating, retrieving, and managing patient appointments.")
                        .contact(new Contact()
                                .name("Gerimedica Team")
                                .email("support@gerimedica.nl")
                                .url("https://www.gerimedica.nl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
