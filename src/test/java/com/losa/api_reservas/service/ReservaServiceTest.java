package com.losa.api_reservas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.losa.api_reservas.model.Court;
import com.losa.api_reservas.model.Reserva;
import com.losa.api_reservas.model.enums.StatusReserva;
import com.losa.api_reservas.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @InjectMocks
    private ReservaService reservaService; // classe real de teste

    @Mock
    private ReservaRepository reservaRepository; // banco de dados "fake"

    private Reserva reservaValida;
    private Court quadra;

    @BeforeEach
    void setUp() {
        quadra = new Court();
        quadra.setId(UUID.randomUUID());
        quadra.setName("Quadra Teste");

        reservaValida = new Reserva();
        reservaValida.setDataHoraInicio(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        reservaValida.setDataHoraFim(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));
        reservaValida.setCourt(quadra);
    }

    @Test
    @DisplayName("Deve criar a reserva com sucesso quando não houver conflitos")
    void deveCriarReservaComSucesso() {
        when(reservaRepository.existeConflitoDeHorario(any(), any(), any(), any()))
                .thenReturn(false);

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaValida);

        Reserva reservaCriada = reservaService.criarReserva(reservaValida);

        assertNotNull(reservaCriada);
        assertEquals(StatusReserva.CONFIRMADA, reservaCriada.getStatus());

        verify(reservaRepository, times(1)).save(reservaValida);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar reserva no passado")
    void naoDeveCriarReservaNoPassado() {
        // 1. Cenário: Forçamos a data para o passado (ontem)
        reservaValida.setDataHoraInicio(LocalDateTime.now().minusDays(1));

        // 2 & 3. Ação e Verificação: Checamos se a RuntimeException é lançada com a
        // mensagem certa
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservaService.criarReserva(reservaValida);
        });

        assertEquals("Não é possível criar uma reserva em uma data ou horário no passado.", exception.getMessage());

        // Verifica que o repositório NUNCA foi chamado (barrou antes de ir pro banco)
        verify(reservaRepository, never()).save(any());
    }

}
