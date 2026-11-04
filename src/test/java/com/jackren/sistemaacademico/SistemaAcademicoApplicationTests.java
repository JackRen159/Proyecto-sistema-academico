package com.jackren.sistemaacademico;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de arranque del contexto completo de Spring Boot.
 *
 * Deshabilitado temporalmente porque requiere una base de datos PostgreSQL real.
 * Se habilitara en la Fase de pruebas de integracion con Testcontainers,
 * donde el contenedor PostgreSQL se levantara automaticamente.
 *
 * No eliminar: sirve como marcador de la tarea pendiente.
 */
@SpringBootTest
@Disabled("Requiere PostgreSQL real. Se reactivara con Testcontainers en la fase de integracion.")
class SistemaAcademicoApplicationTests {

	@Test
	void contextLoads() {
	}

}
