package com.panicar.epistemic.harmony.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI epistemicHarmonyAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Epistemic Harmony API")
                        .version("1.0.0")
                        .description("REST API for rating and comparing scientific theories and philosophical statements. " +
                                "Supports interdisciplinary analysis between empirical science and philosophical reasoning.")
                        .contact(new Contact()
                                .name("Epistemic Harmony Team")
                                .email("admin@epistemicharmony.com")));
    }
}