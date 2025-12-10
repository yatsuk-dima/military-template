package ua.edu.viti.military.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Military Supply Management System API")
                        .version("1.0.0")
                        .description("REST API для системи управління матеріально-технічним забезпеченням (МТЗ). " +
                                "Система дозволяє керувати категоріями постачання, складами та матеріалами.")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@military-supply.ua")));
    }
}
