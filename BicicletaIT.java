package br.edu.ifc.bikes;

import br.edu.ifc.bikes.dto.BicicletaRequestDTO;
import br.edu.ifc.bikes.dto.BicicletaResponseDTO;
import br.edu.ifc.bikes.entity.Usuario;
import br.edu.ifc.bikes.entity.BicicletaStatus;
import br.edu.ifc.bikes.jwt.JwtUtils;
import br.edu.ifc.bikes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/bicicletas/bicicletas-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/bicicletas/bicicletas-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BicicletaIT {

    @Autowired
    UsuarioRepository usuarioRepository;

    @LocalServerPort
    int port;

    WebTestClient testClient;
    String adminToken;

    @BeforeEach
    void setUp() {
        testClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
        ensureUser("bikes-admin@test.com", Usuario.Role.ADMIN);
        adminToken = "Bearer " + JwtUtils.createToken("bikes-admin@test.com", "ADMIN").getToken();
    }

    @Test
    void bicicleta_AdminPodeCadastrarLocalizarListarAtualizarEExcluir() {
        BicicletaRequestDTO request = new BicicletaRequestDTO("IT-BIKE-1", BicicletaStatus.LIVRE);
        BicicletaResponseDTO created = testClient.post()
                .uri("/api/v1/bicicletas")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BicicletaResponseDTO.class)
                .returnResult().getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.codigo()).isEqualTo("IT-BIKE-1");
        assertThat(created.status()).isEqualTo(BicicletaStatus.LIVRE);

        testClient.get()
                .uri("/api/v1/bicicletas/IT-BIKE-1")
                .header("Authorization", adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BicicletaResponseDTO.class)
                .value(response -> assertThat(response.id()).isEqualTo(created.id()));

        testClient.get()
                .uri("/api/v1/bicicletas")
                .header("Authorization", adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BicicletaResponseDTO.class)
                .value(response -> assertThat(response).extracting(BicicletaResponseDTO::codigo)
                        .contains("IT-BIKE-1"));

        testClient.put()
                .uri("/api/v1/bicicletas/IT-BIKE-1")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("IT-BIKE-1", BicicletaStatus.MANUTENCAO))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BicicletaResponseDTO.class)
                .value(response -> assertThat(response.status()).isEqualTo(BicicletaStatus.MANUTENCAO));

        testClient.delete()
                .uri("/api/v1/bicicletas/IT-BIKE-1")
                .header("Authorization", adminToken)
                .exchange()
                .expectStatus().isNoContent();

        testClient.get()
                .uri("/api/v1/bicicletas/IT-BIKE-1")
                .header("Authorization", adminToken)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void bicicleta_CodigoDuplicado_RetornaConflito() {
        BicicletaRequestDTO request = new BicicletaRequestDTO("IT-BIKE-2", BicicletaStatus.LIVRE);
        createBike(request);

        testClient.post()
                .uri("/api/v1/bicicletas")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void bicicleta_CamposInvalidos_RetornaUnprocessableEntity() {
        testClient.post()
                .uri("/api/v1/bicicletas")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("", BicicletaStatus.LIVRE))
                .exchange()
                .expectStatus().isEqualTo(422);

        testClient.post()
                .uri("/api/v1/bicicletas")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BicicletaRequestDTO("IT-BIKE-INVALID", null))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    void bicicleta_SemAutenticacao_RetornaProibido() {
        testClient.get()
                .uri("/api/v1/bicicletas")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private void createBike(BicicletaRequestDTO request) {
        testClient.post()
                .uri("/api/v1/bicicletas")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated();
    }

    private void ensureUser(String username, Usuario.Role role) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseGet(Usuario::new);
        usuario.setUsername(username);
        usuario.setPassword("unused");
        usuario.setRole(role);
        usuarioRepository.save(usuario);
    }
}
