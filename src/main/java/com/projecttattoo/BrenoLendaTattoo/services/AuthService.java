package com.projecttattoo.BrenoLendaTattoo.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projecttattoo.BrenoLendaTattoo.dto.LoginDto;
import com.projecttattoo.BrenoLendaTattoo.models.Logins;
import com.projecttattoo.BrenoLendaTattoo.repositories.LoginsRepository;

@Service
public class AuthService {

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticatedService authenticatedService;

    // Método unificado para login de Cliente e Artista
    public ResponseEntity<Map<String, String>> login(LoginDto loginDto) {
        Logins user = loginsRepository.findByEmail(loginDto.email());
        
        System.out.println(user.getEmail() + " | " + user.getSenha() + " | " + user.getPassword() + " | " + user.getUserRole());
        
        if (user == null) {
        	System.out.println("Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuário não encontrado"));
        }

        if (!encoder.matches(loginDto.senha(), user.getSenha())) {
        	System.out.println("Senha incorreta");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Senha incorreta"));
        }

        String token = authenticatedService.setToken(user);
        return ResponseEntity.ok(Map.of(
            "token", token,
            "role", user.getUserRole().toString()
        ));
    }
}