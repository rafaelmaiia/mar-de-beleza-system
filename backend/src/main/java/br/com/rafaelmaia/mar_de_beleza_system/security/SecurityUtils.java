package br.com.rafaelmaia.mar_de_beleza_system.security;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.SystemUser;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    public static UserDetails toUserDetails(SystemUser user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }
}
