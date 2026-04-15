package com.losa.api_reservas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.losa.api_reservas.model.user;
import com.losa.api_reservas.model.enums.Perfil;
import com.losa.api_reservas.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private user usuarioExemplo;

    @BeforeEach
    void setUp() {
        usuarioExemplo = new user();
        usuarioExemplo.setId(UUID.randomUUID());
        usuarioExemplo.setName("Paulo");
        usuarioExemplo.setEmail("paulo@email.com");
        usuarioExemplo.setPassword("senha123");
        usuarioExemplo.setPerfil(Perfil.ADMIN);
    }

    @Test
    @DisplayName("Deve salvar um usuário criptografando a senha corretamente")
    void deveSalvarUsuarioComSucesso() {
        // Arrange: Preparamos os mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografadaBCrypt");
        when(userRepository.save(any(user.class))).thenReturn(usuarioExemplo);

        // Act: Executamos o método de salvar
        user usuarioSalvo = userService.criarUsuario(usuarioExemplo);

        // Assert: Verificamos se a senha foi enviada para o codificador
        assertNotNull(usuarioSalvo);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userRepository, times(1)).save(any(user.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail já estiver cadastrado")
    void naoDeveSalvarUsuarioComEmailDuplicado() {
        // Arrange: Simulamos que o e-mail já existe no banco
        when(userRepository.existsByEmail("paulo@email.com")).thenReturn(true);

        // Act & Assert: Verificamos se o erro é lançado
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.criarUsuario(usuarioExemplo);
        });

        assertEquals("Já existe um usuario cadastrado com o e-mail informado", exception.getMessage());

        // Garante que o método save nunca foi chamado
        verify(userRepository, never()).save(any());
    }
}