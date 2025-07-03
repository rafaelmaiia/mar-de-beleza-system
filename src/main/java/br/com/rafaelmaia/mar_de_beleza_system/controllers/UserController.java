package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.AppUser;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Role;
import br.com.rafaelmaia.mar_de_beleza_system.dto.RegisterRequest;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppUserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for Admins to manage users")
public class UserController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        // Verificação se o email já existe
        if (appUserRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Email já está em uso.");
        }

        var user = new AppUser();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        // Por padrão, novos usuários criados por aqui são funcionárias (USER)
        user.setRole(Role.USER);

        appUserRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Funcionária criada com sucesso!");
    }

    // Lembrete: adicionar outros endpoints aqui, como listar usuários, desativar funcionária, etc.
}
