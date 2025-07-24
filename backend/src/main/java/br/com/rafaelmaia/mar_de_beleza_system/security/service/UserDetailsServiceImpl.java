package br.com.rafaelmaia.mar_de_beleza_system.security.service;

import br.com.rafaelmaia.mar_de_beleza_system.repository.SystemUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SystemUserRepository repository;

    public UserDetailsServiceImpl(SystemUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + username));

        return user;
    }
}