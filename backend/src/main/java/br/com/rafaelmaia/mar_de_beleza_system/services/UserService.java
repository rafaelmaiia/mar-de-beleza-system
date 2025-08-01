package br.com.rafaelmaia.mar_de_beleza_system.services;

import br.com.rafaelmaia.mar_de_beleza_system.dto.PasswordChangeRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserResponseDTO;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO findUserById(Long id);
    List<UserResponseDTO> findAllUsers(Boolean canBeScheduled);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUser(Long id);
    void changePassword(Long userId, PasswordChangeRequestDTO dto);
}

