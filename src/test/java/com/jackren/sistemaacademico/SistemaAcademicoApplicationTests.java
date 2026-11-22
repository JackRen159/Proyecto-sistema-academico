package com.jackren.sistemaacademico;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test de arranque del contexto completo de Spring Boot.
 *
 * Valida la carga y composicion de todos los componentes, configuraciones y servicios
 * de la aplicacion en el contexto de Spring.
 * En entornos de prueba unitaria/CI sin instancia externa de PostgreSQL activa,
 * la conexion fisica y la migracion Flyway se proporcionan via beans controlados para validar
 * la integridad del grafo de beans y el arranque del servidor.
 */
@SpringBootTest
@Import(SistemaAcademicoApplicationTests.TestDataSourceConfig.class)
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
})
class SistemaAcademicoApplicationTests {

    @TestConfiguration
    static class TestDataSourceConfig {
        @Bean
        @Primary
        public DataSource dataSource() throws Exception {
            DataSource ds = Mockito.mock(DataSource.class);
            Connection conn = Mockito.mock(Connection.class);
            DatabaseMetaData meta = Mockito.mock(DatabaseMetaData.class);
            Mockito.when(ds.getConnection()).thenReturn(conn);
            Mockito.when(ds.getConnection(Mockito.anyString(), Mockito.anyString())).thenReturn(conn);
            Mockito.when(conn.getMetaData()).thenReturn(meta);
            Mockito.when(conn.getSchema()).thenReturn("public");
            Mockito.when(conn.getCatalog()).thenReturn("sistema_academico");
            Mockito.when(meta.getDatabaseProductName()).thenReturn("PostgreSQL");
            Mockito.when(meta.getDatabaseProductVersion()).thenReturn("16.0");
            Mockito.when(meta.getDatabaseMajorVersion()).thenReturn(16);
            Mockito.when(meta.getDatabaseMinorVersion()).thenReturn(0);
            return ds;
        }
    }

    @MockitoBean
    private Flyway flyway;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext, "El contexto de Spring Boot debe cargar exitosamente");
    }

}


