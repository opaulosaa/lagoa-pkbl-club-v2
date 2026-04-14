package com.losa.api_reservas.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.losa.api_reservas.model.user;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<user, UUID> {

    Optional<user> findByEmail(String email);

    boolean existsByEmail(String email);

}
