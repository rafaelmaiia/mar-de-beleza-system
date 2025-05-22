package br.com.rafaelmaia.mar_de_beleza_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI(){
     return new OpenAPI()
            .info(new Info()
                .title("Sistema de Agendamento de Atendimentos - Salão Mar de Beleza")
                    .version("v1")
                    .description("Sistema criado para administrar os atendimentos de serviços como cílios e sobrancelhas " +
                            "no Salão Mar de Beleza")
                    .license(new License()
                        .name("Apache 2.0")
                    )
            );
    }
}
