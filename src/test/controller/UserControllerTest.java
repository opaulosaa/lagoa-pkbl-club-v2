package controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.losa.api_reservas.model.user;
import com.losa.api_reservas.model.enums.Perfil;
import com.losa.api_reservas.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc // Liga o "Postman" interno do Spring Boot
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // O objeto que dispara as requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // O conversor que transforma Java em JSON

    @MockBean // O MockBean substitui o seu Service real por um de mentira dentro do contexto
              // do Spring
    private UserService userService;

    @Test
    @DisplayName("Deve retornar 201 Created ao enviar um JSON de usuário válido")
    void deveCriarUsuarioRetornar201() throws Exception {

        // 1. Arrange: Criamos os dados da requisição
        user novoUsuario = new user();
        novoUsuario.setName("Carlos");
        novoUsuario.setEmail("carlos@email.com");
        novoUsuario.setPassword("senha123");
        novoUsuario.setPerfil(Perfil.ALUNO);

        // O que o Service deve devolver quando o Controller chamar ele
        user usuarioSalvo = new user();
        usuarioSalvo.setId(UUID.randomUUID());
        usuarioSalvo.setName("Carlos");
        usuarioSalvo.setEmail("carlos@email.com");
        usuarioSalvo.setPerfil(Perfil.ALUNO);

        when(userService.criarUsuario(any(user.class))).thenReturn(usuarioSalvo);

        // 2 & 3. Act & Assert: Disparamos o POST e validamos a resposta!
        mockMvc.perform(post("/api/users") // Ou /api/usuarios, dependendo do seu Controller
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(novoUsuario))) // Transforma o objeto em texto JSON

                // Validações do MockMvc (O que nós esperávamos ver no Postman)
                .andExpect(status().isCreated()) // Espera o Status 201
                .andExpect(jsonPath("$.id").exists()) // Espera que o JSON de volta tenha um "id"
                .andExpect(jsonPath("$.name").value("Carlos")) // Espera que o nome seja Carlos
                .andExpect(jsonPath("$.password").doesNotExist()); // Espera que a senha não vaze no JSON!
    }
}
