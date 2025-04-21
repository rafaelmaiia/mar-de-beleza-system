package br.com.rafaelmaia.mar_de_beleza_system.config;

import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Client;
import br.com.rafaelmaia.mar_de_beleza_system.domain.entity.Contact;
import br.com.rafaelmaia.mar_de_beleza_system.domain.enums.Gender;
import br.com.rafaelmaia.mar_de_beleza_system.repository.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Configuration
@Profile("local")
public class LocalConfig {

    @Autowired
    private ClientRepository repository;

    @PostConstruct
    public void startDB() {
        Contact contact = Contact.builder()
                .phone("123456789")
                .phoneIsWhatsapp(true)
                .build();

        Client c1 = Client.builder()
                .name("Rafael")
                .birthDate(LocalDate.of(2000, 10, 24))
                .gender(Gender.MALE)
                .contact(contact)
                .build();

        Client c2 = Client.builder()
                .name("Luiz")
                .birthDate(LocalDate.now())
                .gender(Gender.MALE)
                .contact(Contact.builder()
                        .phone("987654321")
                        .phoneIsWhatsapp(true)
                        .build())
                .build();
        repository.saveAll(List.of(c1, c2));
    }
}
