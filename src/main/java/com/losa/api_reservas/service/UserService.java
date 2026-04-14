package com.losa.api_reservas.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.losa.api_reservas.model.user;
import com.losa.api_reservas.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public user criarUsuario(user novoUsuario) {
        if (userRepository.existsByEmail(novoUsuario.getEmail())) {
            throw new RuntimeException("Já existe um usuario cadastrado com o e-mail informado");
        }

        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getPassword());
        novoUsuario.setPassword(senhaCriptografada);

        return userRepository.save(novoUsuario);
    }

    public List<user> listarTodos() {
        return userRepository.findAll();
    }

    public Optional<user> buscarPorId(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional
    public user atualizarUsuario(UUID id, user dadosAtualizados) {
        user usuarioExistente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado."));
        usuarioExistente.setName(dadosAtualizados.getName());

        return userRepository.save(usuarioExistente);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        userRepository.deleteById(id);

    }

}
