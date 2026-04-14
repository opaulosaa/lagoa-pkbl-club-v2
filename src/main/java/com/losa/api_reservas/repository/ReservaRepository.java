package com.losa.api_reservas.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.losa.api_reservas.model.Reserva;
import com.losa.api_reservas.model.enums.StatusReserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
            "WHERE r.court.id = :quadraId AND r.status = com.losa.api_reservas.model.enums.StatusReserva.CONFIRMADA " +
            "AND (r.dataHoraInicio < :fim AND r.dataHoraFim > :inicio)")
    boolean existeConflitoDeHorario(@Param("quadraId") UUID quadraId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("statusCancelada") StatusReserva statusCancelada);

}
