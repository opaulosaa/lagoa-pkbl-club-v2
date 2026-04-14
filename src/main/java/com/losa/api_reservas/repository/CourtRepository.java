package com.losa.api_reservas.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.losa.api_reservas.model.Court;
import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, UUID> {
    boolean existsByNameIgnoreCase(String name);

    List<Court> findByAtivaTrue();
}
