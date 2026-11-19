package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Enums - Frente 9 (Seguridad, Configuración y Auditoría V2)")
class Frente9EntityTest {

    @Test
    @DisplayName("Rol enum debe incluir roles especializados V2")
    void testRolEnumValoresV2() {
        assertNotNull(Rol.valueOf("COORDINADOR_ACADEMICO"));
        assertNotNull(Rol.valueOf("COORDINADOR_CONVIVENCIA"));
        assertNotNull(Rol.valueOf("ORIENTADOR_ESCOLAR"));
        assertNotNull(Rol.valueOf("COORDINADOR"));
        assertNotNull(Rol.valueOf("HISTORICO_RESTRINGIDO"));
        assertEquals(11, Rol.values().length);
    }

    @Test
    @DisplayName("Usuario debe inicializar estado ACTIVA y permitir ciclo de vida V2")
    void testUsuarioCicloDeVidaV2() {
        Usuario usuario = new Usuario("rector1", "hash123", Rol.RECTOR);
        assertEquals(EstadoUsuario.ACTIVA, usuario.getEstado());
        assertTrue(usuario.getActivo());
        assertFalse(usuario.getDebeCambiarPassword());
        assertEquals(0, usuario.getIntentosFallidos());

        usuario.setEstado(EstadoUsuario.BLOQUEADA);
        assertEquals(EstadoUsuario.BLOQUEADA, usuario.getEstado());
        assertFalse(usuario.getActivo());

        usuario.setDebeCambiarPassword(true);
        assertTrue(usuario.getDebeCambiarPassword());

        usuario.setPersonaTipo(TipoPersonaUsuario.PROFESOR);
        usuario.setPersonaId(15L);
        assertEquals(TipoPersonaUsuario.PROFESOR, usuario.getPersonaTipo());
        assertEquals(15L, usuario.getPersonaId());
    }

    @Test
    @DisplayName("UsuarioRol y OrganoColegiadoMiembro deben asignar propiedades correctamente")
    void testUsuarioRolYOrganoColegiado() {
        Usuario usuario = new Usuario("docente1", "hash", Rol.PROFESOR);
        usuario.setId(1L);

        UsuarioRol ur = new UsuarioRol(usuario, Rol.COORDINADOR_ACADEMICO, false);
        ur.setMotivoAsignacion("Encargo académico 2026");
        assertEquals(Rol.COORDINADOR_ACADEMICO, ur.getRol());
        assertFalse(ur.getEsPrincipal());
        assertTrue(ur.getActivo());
        assertEquals("Encargo académico 2026", ur.getMotivoAsignacion());

        AnioLectivo anio = new AnioLectivo();
        anio.setId(2026L);
        anio.setNombre("Año 2026");

        OrganoColegiadoMiembro miembro = new OrganoColegiadoMiembro(
                usuario, TipoOrganoColegiado.CONSEJO_ACADEMICO, "Acta 01-2026", anio);
        assertEquals(TipoOrganoColegiado.CONSEJO_ACADEMICO, miembro.getTipoOrgano());
        assertEquals("Acta 01-2026", miembro.getActaDesignacion());
        assertTrue(miembro.getActivo());
    }

    @Test
    @DisplayName("TokenRecuperacionPassword debe gestionar tokenHash y expiracion")
    void testTokenRecuperacion() {
        Usuario usuario = new Usuario("usuario1", "hash", Rol.ESTUDIANTE);
        LocalDateTime exp = LocalDateTime.now().plusMinutes(15);
        TokenRecuperacionPassword token = new TokenRecuperacionPassword(usuario, "hashsha256token", exp);

        assertFalse(token.getUsado());
        assertEquals("hashsha256token", token.getTokenHash());
        assertEquals(exp, token.getFechaExpiracion());
    }

    @Test
    @DisplayName("InstitucionDatosLegales y PoliticaTratamientoDatos deben estructurar datos oficiales")
    void testConfiguracionInstitucionalEntidades() {
        InstitucionDatosLegales inst = new InstitucionDatosLegales();
        inst.setNombreOficial("Colegio Departamental");
        inst.setCodigoDane("123456789012");
        inst.setNit("900123456-7");
        inst.setResolucionAprobacion("Res. 1234");
        inst.setCaracter(CaracterInstitucion.OFICIAL);
        inst.setNombreRectorFirmante("Dr. Rector");
        inst.setNombreSecretarioFirmante("Lic. Secretaria");

        assertEquals("123456789012", inst.getCodigoDane());
        assertEquals(CaracterInstitucion.OFICIAL, inst.getCaracter());

        PoliticaTratamientoDatos pol = new PoliticaTratamientoDatos(
                1, "Política V1", "Contenido", "Aviso", true);
        assertEquals(1, pol.getNumeroVersion());
        assertTrue(pol.getEsVigente());

        ConsentimientoPoliticaDatos consentimiento = new ConsentimientoPoliticaDatos(
                new Usuario(), pol, "127.0.0.1", "Mozilla/5.0", OrigenConsentimiento.LOGIN_PRIMER_INGRESO);
        assertEquals(OrigenConsentimiento.LOGIN_PRIMER_INGRESO, consentimiento.getOrigen());
        assertEquals("127.0.0.1", consentimiento.getDireccionIp());
    }

    @Test
    @DisplayName("ConfiguracionSieeVersion y ParametroInstitucional deben instanciarse correctamente")
    void testSieeYParametro() {
        ConfiguracionSieeVersion siee = new ConfiguracionSieeVersion();
        siee.setNumeroVersion(1);
        siee.setNombreVersion("SIEE V1");
        siee.setEstado(EstadoVersionConfiguracion.VIGENTE);
        siee.setEscalaMinima(new BigDecimal("1.00"));
        siee.setEscalaMaxima(new BigDecimal("5.00"));
        siee.setEscalaAprobatoria(new BigDecimal("3.00"));

        assertEquals(EstadoVersionConfiguracion.VIGENTE, siee.getEstado());
        assertEquals(new BigDecimal("3.00"), siee.getEscalaAprobatoria());

        ParametroInstitucional param = new ParametroInstitucional(
                "PLAZO_ARCO_DIAS_HABILES", "15", TipoDatoParametro.INTEGER, "Plazo ARCO", "SEGURIDAD");
        assertEquals("PLAZO_ARCO_DIAS_HABILES", param.getClave());
        assertEquals("15", param.getValor());
        assertEquals(TipoDatoParametro.INTEGER, param.getTipoDato());
    }

    @Test
    @DisplayName("AuditoriaEvento debe almacenar datos canónicos y hashes encadenados")
    void testAuditoriaEvento() {
        AuditoriaEvento evento = new AuditoriaEvento(
                null,
                "admin",
                "ADMINISTRADOR",
                "SEGURIDAD",
                "CREAR_USUARIO",
                "Usuario",
                "10",
                null,
                "{\"username\":\"nuevo\"}",
                "Creación por Secretaría",
                ResultadoAuditoria.EXITO,
                "corr-12345",
                "192.168.1.1",
                "Postman",
                "0000000000000000000000000000000000000000000000000000000000000000",
                "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2"
        );

        assertEquals("admin", evento.getUsername());
        assertEquals("SEGURIDAD", evento.getModulo());
        assertEquals(ResultadoAuditoria.EXITO, evento.getResultado());
        assertEquals("corr-12345", evento.getCorrelationId());
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000", evento.getHashAnterior());
    }
}
