package com.jackren.sistemaacademico.config;

import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.Usuario;
import com.jackren.sistemaacademico.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.bootstrap.admin.enabled:true}")
    private boolean enabled;

    @Value("${security.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${security.bootstrap.admin.password:Admin123*}")
    private String adminPassword;

    public AdminBootstrapRunner(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Bootstrap de usuario administrador desactivado por configuracion.");
            return;
        }

        if (!usuarioRepository.existsByUsername(adminUsername)) {
            Usuario admin = new Usuario(
                    adminUsername,
                    passwordEncoder.encode(adminPassword),
                    Rol.ADMINISTRADOR
            );
            usuarioRepository.save(admin);
            log.info("Bootstrap exitoso: usuario inicial creado con rol ADMINISTRADOR y username [{}]", adminUsername);
        } else {
            log.debug("El usuario administrador [{}] ya existe en el sistema.", adminUsername);
        }
    }
}

