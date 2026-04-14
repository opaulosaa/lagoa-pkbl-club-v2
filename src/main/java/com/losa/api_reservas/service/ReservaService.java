package com.losa.api_reservas.service;

import java.time.LocalDateTime; // <-- Não esqueça de importar a classe de tempo
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.losa.api_reservas.model.Reserva;
import com.losa.api_reservas.model.enums.StatusReserva;
import com.losa.api_reservas.repository.ReservaRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Transactional
    public Reserva criarReserva(Reserva novaReserva) {

        // --- 1. REGRAS DE TEMPO (Rodam antes de qualquer consulta ao banco) ---

        // Verifica se a data/hora de início já passou (agora)
        if (novaReserva.getDataHoraInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Não é possível criar uma reserva em uma data ou horário no passado.");
        }

        // Verifica se o usuário tentou colocar o horário de fim antes (ou igual) ao
        // horário de início
        if (!novaReserva.getDataHoraFim().isAfter(novaReserva.getDataHoraInicio())) {
            throw new RuntimeException("O horário de término da reserva deve ser posterior ao horário de início.");
        }

        // ----------------------------------------------------------------------

        // --- 2. REGRA DE CONFLITO (Se as datas são válidas, aí sim checamos o banco)
        // ---
        boolean ocupada = reservaRepository.existeConflitoDeHorario(
                novaReserva.getCourt().getId(),
                novaReserva.getDataHoraInicio(),
                novaReserva.getDataHoraFim(),
                StatusReserva.CANCELADA);

        if (ocupada) {
            throw new RuntimeException("A quadra selecionada já possui uma reserva ativa para este horário.");
        }

        // --- 3. REGRA DE HORA CHEIA E DURAÇÃO MÍNIMA ---
        // Garante que os minutos sejam sempre 00 (ex: 15:00, e não 15:15)
        if (novaReserva.getDataHoraInicio().getMinute() != 0 || novaReserva.getDataHoraFim().getMinute() != 0) {
            throw new RuntimeException(
                    "As reservas devem ser feitas em horas cheias (ex: 14:00, 15:00). Minutos quebrados não são permitidos.");
        }

        // Como garantimos que o fim é depois do início E que são horas cheias,
        // a diferença mínima já será obrigatoriamente de 1 hora exata.

        // --- 4. REGRA DE HORÁRIO DE FUNCIONAMENTO (06:00 às 22:00) ---
        int horaInicio = novaReserva.getDataHoraInicio().getHour();
        int horaFim = novaReserva.getDataHoraFim().getHour();

        if (horaInicio < 6 || horaFim > 22) {
            throw new RuntimeException("O estabelecimento funciona apenas das 06:00 às 22:00.");
        }

        // Se passou por todas as barreiras, confirma a reserva e salva
        novaReserva.setStatus(StatusReserva.CONFIRMADA);
        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public Reserva cancelarReserva(UUID id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        reserva.setStatus(StatusReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    public Optional<Reserva> buscarPorId(UUID id) {
        return reservaRepository.findById(id);
    }
}