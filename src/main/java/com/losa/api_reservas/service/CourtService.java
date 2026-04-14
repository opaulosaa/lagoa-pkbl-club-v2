package com.losa.api_reservas.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.losa.api_reservas.model.Court;
import com.losa.api_reservas.repository.CourtRepository;

import jakarta.transaction.Transactional;

@Service
public class CourtService {

    @Autowired
    private CourtRepository courtRepository;

    @Transactional
    public Court criarQuadra(Court novaQuadra) {
        if (courtRepository.existsByNameIgnoreCase(novaQuadra.getName())) {
            throw new RuntimeException("Já existe uma quadra com esse nome.");
        }
        novaQuadra.setAtiva(true);
        return courtRepository.save(novaQuadra);
    }

    public List<Court> listarTodasAtivas() {
        return courtRepository.findByAtivaTrue();
    }

    public Optional<Court> buscarPorId(UUID id) {
        return courtRepository.findById(id);
    }

    @Transactional
    public Court atualizarQuadra(UUID id, Court dadosAtualizados) {
        Court quadraExistente = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quadra não encontrada"));

        if (!quadraExistente.getName().equalsIgnoreCase(dadosAtualizados.getName()) &&
                courtRepository.existsByNameIgnoreCase(dadosAtualizados.getName())) {
            throw new RuntimeException("Já existe outra quadra cadastrada com este nome.");
        }

        quadraExistente.setName(dadosAtualizados.getName());
        quadraExistente.setModalidade(dadosAtualizados.getModalidade());

        return courtRepository.save(quadraExistente);

    }

    @Transactional
    public void deletarQuadra(UUID id) {
        Court quadraExistente = courtRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quadra não encontrada"));

        quadraExistente.setAtiva(false);
        courtRepository.save(quadraExistente);
    }
}
