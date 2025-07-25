package br.com.rafaelmaia.mar_de_beleza_system.controllers;

import br.com.rafaelmaia.mar_de_beleza_system.controllers.docs.UserControllerDocs;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PasswordChangeRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints para gerenciar usuários/funcionários do sistema")
public class UserController implements UserControllerDocs {

    private final UserService userService;

    // QUALQUER funcionária logada pode ver a lista de outras para poder agendar
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<List<UserResponseDTO>> findAll(
            @RequestParam(required = false) Boolean canBeScheduled) {
        return ResponseEntity.ok(userService.findAllUsers(canBeScheduled));
    }

    // ADMINS ou o próprio usuário podem ver seus detalhes
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal.id")
    @Override
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    // Apenas ADMINS podem criar novos usuários
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserRequestDTO requestDTO) {
        UserResponseDTO newUser = userService.createUser(requestDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newUser.id()).toUri();
        return ResponseEntity.created(uri).body(newUser);
    }

    // ADMINS podem editar qualquer um, STAFFS podem editar apenas a si mesmos
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal.id")
    @Override
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    // Apenas ADMINS podem deletar usuarios
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Override
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody @Valid PasswordChangeRequestDTO dto) {
        userService.changePassword(id, dto);
        return ResponseEntity.noContent().build();
    }
}
