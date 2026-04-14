package com.losa.api_reservas.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.losa.api_reservas.model.user;
import com.losa.api_reservas.repository.UserRepository;
import com.losa.api_reservas.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public record AutenticacaoDTO(String email, String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AutenticacaoDTO body) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.email(), body.password()));

            user user = userRepository.findByEmail(body.email())
                    .orElseThrow();

            String token = jwtService.gerarToken(user);

            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Email ou senha incorretos");
        }
    }
}
