package br.com.rafaelmaia.mar_de_beleza_system.services.impl;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import br.com.rafaelmaia.mar_de_beleza_system.dto.PasswordChangeRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserRequestDTO;
import br.com.rafaelmaia.mar_de_beleza_system.dto.UserResponseDTO;
import br.com.rafaelmaia.mar_de_beleza_system.repository.AppointmentRepository;
import br.com.rafaelmaia.mar_de_beleza_system.repository.SystemUserRepository;
import br.com.rafaelmaia.mar_de_beleza_system.services.UserService;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.BusinessRuleException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.DataIntegrityViolationException;
import br.com.rafaelmaia.mar_de_beleza_system.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SystemUserRepository repository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequestDTO dto) {
        logger.info("Iniciando processo de alteração de senha para o usuário ID: {}", userId);

        // Verifica se a nova senha e a confirmação são iguais
        if (!dto.newPassword().equals(dto.confirmationPassword())) {
            throw new BusinessRuleException("A nova senha e a confirmação não conferem.");
        }

        SystemUser user = repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado."));

        // Verifica se a senha atual informada bate com a senha salva no banco
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessRuleException("A senha atual está incorreta.");
        }

        // Se estiver tudo ok, criptografa e salva a nova senha
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        repository.save(user);

        logger.info("Senha do usuário ID {} alterada com sucesso.", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long id) {
        logger.info("Buscando usuário com ID: {}", id);
        SystemUser user = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado com ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAllUsers(Boolean canBeScheduled) {
        logger.info("Buscando todos os usuários");
        List<SystemUser> userList;
        if (canBeScheduled != null) {
            userList = repository.findByCanBeScheduled(canBeScheduled); // Crie este método no repositório
        } else {
            userList = repository.findAll();
        }
        return userList.stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        logger.info(String.format("Iniciando criação de novo usuário: %s", requestDTO.email()));

        // Validação de email e telefone únicos
        validateEmailAndPhoneUniqueness(requestDTO.email(), requestDTO.contact().phone(), null);

        SystemUser newUser = new SystemUser();
        newUser.setName(requestDTO.name());
        newUser.setEmail(requestDTO.email());
        newUser.setPassword(passwordEncoder.encode(requestDTO.password()));
        newUser.setRole(requestDTO.role());

        Contact newContact = new Contact();
        newContact.setPhone(requestDTO.contact().phone());
        newContact.setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());
        newUser.setContact(newContact);
        newUser.setSpecialties(requestDTO.specialties());

        SystemUser savedUser = repository.save(newUser);
        logger.info("Usuário ID {} criado com sucesso.", savedUser.getId());

        return UserResponseDTO.fromEntity(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) {
        logger.info("Iniciando atualização para o usuário ID: {}", id);

        // Valida se o novo email ou telefone já não pertencem a outro usuario
        validateEmailAndPhoneUniqueness(requestDTO.email(), requestDTO.contact().phone(), id);

        SystemUser userToUpdate = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado com ID: " + id));

        // Se o usuário não tem um contato, cria um novo para ele antes de usar
        if (userToUpdate.getContact() == null) {
            userToUpdate.setContact(new Contact());
        }

        userToUpdate.setName(requestDTO.name());
        userToUpdate.setEmail(requestDTO.email());
        userToUpdate.setRole(requestDTO.role());
        userToUpdate.getContact().setPhone(requestDTO.contact().phone());
        userToUpdate.getContact().setPhoneIsWhatsapp(requestDTO.contact().phoneIsWhatsapp());
        userToUpdate.setSpecialties(requestDTO.specialties());

        // Atualiza a senha apenas se uma nova foi fornecida
        if (requestDTO.password() != null && !requestDTO.password().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(requestDTO.password()));
            logger.info("Senha do usuário ID {} foi atualizada.", id);
        }

        SystemUser updatedUser = repository.save(userToUpdate);
        logger.info("Usuário ID {} atualizado com sucesso.", updatedUser.getId());

        return UserResponseDTO.fromEntity(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Iniciando exclusão do usuário ID: {}", id);

        if (!repository.existsById(id)) {
            throw new ObjectNotFoundException("Usuário não encontrado com ID: " + id);
        }

        // Não permite excluir um profissional que tenha agendamentos
        if (appointmentRepository.existsByProfessionalId(id)) {
            throw new DataIntegrityViolationException(
                    "Este profissional não pode ser excluído pois está vinculado a agendamentos."
            );
        }

        repository.deleteById(id);
        logger.info("Usuário ID {} excluído com sucesso.", id);
    }

    private void validateEmailAndPhoneUniqueness(String email, String phone, Long idToIgnore) {
        Optional<SystemUser> userByEmail = repository.findByEmail(email);
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(idToIgnore)) {
            throw new DataIntegrityViolationException("Email já cadastrado no sistema.");
        }

        Optional<SystemUser> userByPhone = repository.findByContact_Phone(phone);
        if (userByPhone.isPresent() && !userByPhone.get().getId().equals(idToIgnore)) {
            throw new DataIntegrityViolationException("Telefone já cadastrado no sistema.");
        }
    }
}

