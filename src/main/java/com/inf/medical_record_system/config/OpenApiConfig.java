package com.inf.medical_record_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medicalRecordOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Record System API")
                        .version("1.0")
                        .description("REST API documentation for the Medical Record System project"));
    }
}