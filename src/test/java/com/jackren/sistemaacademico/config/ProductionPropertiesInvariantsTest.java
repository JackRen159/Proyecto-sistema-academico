package com.jackren.sistemaacademico.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ProductionPropertiesInvariantsTest {

    @Test
    @DisplayName("S1: application-prod.properties debe forzar ddl-auto=validate y flyway.enabled=true")
    void applicationProdPropertiesDebeCumplirInvariantesDeSeguridad() throws IOException {
        ClassPathResource resource = new ClassPathResource("application-prod.properties");
        assertTrue(resource.exists(), "application-prod.properties debe existir");

        Properties props = PropertiesLoaderUtils.loadProperties(resource);
        assertEquals("validate", props.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("true", props.getProperty("spring.flyway.enabled"));
        assertEquals("graceful", props.getProperty("server.shutdown"));
        assertEquals("true", props.getProperty("server.compression.enabled"));
        assertTrue(props.getProperty("spring.datasource.url").contains("DB_URL"));
    }
}
