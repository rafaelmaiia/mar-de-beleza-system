package br.com.rafaelmaia.mar_de_beleza_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class MarDeBelezaSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarDeBelezaSystemApplication.class, args);
	}
}
