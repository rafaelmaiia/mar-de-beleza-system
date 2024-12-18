package br.com.rafaelmaia.mar_de_beleza_system.config;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("local")
public class LocalConfig {

    @Autowired
    private ClientRepository repository;

    @Bean
    public void startDB() {
        Client c1 = new Client(null, "Rafael", "rafael@gmail.com", "85912344321");
        Client c2 = new Client(null, "Luiz", "luiz@gmail.com", "85943214321");
        repository.saveAll(List.of(c1, c2));
    }
}
