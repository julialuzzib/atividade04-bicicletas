package br.edu.ifc.bikes;

import br.edu.ifc.bikes.dto.UsuarioResponseDTO;
import br.edu.ifc.bikes.entity.Usuario;
import br.edu.ifc.bikes.jwt.JwtToken;
import br.edu.ifc.bikes.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioAutenticacaoIT {

    private static final String USERNAME = "list-admin@test.local";

    @LocalServerPort
    int port;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    WebTestClient testClient;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        usuarioRepository.findByUsername(USERNAME).ifPresent(usuarioRepository::delete);
        Usuario admin = new Usuario();
        admin.setUsername(USERNAME);
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole(Usuario.Role.ADMIN);
        usuarioRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        usuarioRepository.findByUsername(USERNAME).ifPresent(usuarioRepository::delete);
    }

    @Test
    void loginAdmin_ComTokenPodeListarUsuarios() {
        JwtToken token = testClient.post()
                .uri("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"username":"list-admin@test.local","password":"123456"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody();

        assertThat(token).isNotNull();
        assertThat(token.getToken()).isNotBlank();

        testClient.get()
                .uri("/api/v1/usuarios")
                .header("Authorization", "Bearer " + token.getToken())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDTO.class)
                .value(usuarios -> assertThat(usuarios)
                        .extracting(UsuarioResponseDTO::username)
                        .contains(USERNAME));
    }

    @Test
    void listarUsuarios_SemToken_Retorna401() {
        testClient.get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
