package com.losa.api_reservas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.losa.api_reservas.model.Court;
import com.losa.api_reservas.model.enums.Modalidade;
import com.losa.api_reservas.repository.CourtRepository;

@ExtendWith(MockitoExtension.class)
class CourtServiceTest {

    @InjectMocks
    private CourtService courtService;

    @Mock
    private CourtRepository courtRepository;

    private Court quadraNova;

    @BeforeEach
    void setUp() {
        quadraNova = new Court();
        quadraNova.setId(UUID.randomUUID());
        quadraNova.setName("Quadra Central");
        quadraNova.setModalidade(Modalidade.TENIS);
    }

    @Test
    @DisplayName("Deve criar uma quadra com sucesso quando o nome for inédito")
    void deveCriarQuadraComSucesso() {
        // Arrange: Ensinamos o Mockito que não existe quadra com esse nome
        when(courtRepository.existsByNameIgnoreCase("Quadra Central")).thenReturn(false);
        when(courtRepository.save(any(Court.class))).thenReturn(quadraNova);

        // Act: Tentamos criar
        Court quadraCriada = courtService.criarQuadra(quadraNova);

        // Assert: Verificamos se deu certo e se a quadra foi ativada automaticamente
        assertNotNull(quadraCriada);
        assertTrue(quadraCriada.isAtiva());
        verify(courtRepository, times(1)).save(quadraNova);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar quadra com nome duplicado")
    void naoDeveCriarQuadraComNomeDuplicado() {
        // Arrange: Forçamos o banco a dizer que o nome JÁ EXISTE
        when(courtRepository.existsByNameIgnoreCase("Quadra Central")).thenReturn(true);

        // Act & Assert: Esperamos que o Spring lance a RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courtService.criarQuadra(quadraNova);
        });

        assertEquals("Já existe uma quadra com esse nome.", exception.getMessage());

        // Garante que o sistema barrou antes de tentar salvar no banco
        verify(courtRepository, never()).save(any());
    }
}