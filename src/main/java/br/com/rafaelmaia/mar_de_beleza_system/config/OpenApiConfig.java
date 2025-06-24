package br.com.rafaelmaia.mar_de_beleza_system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // --- INÍCIO DA SEÇÃO DE SEGURANÇA ADICIONADA NO SWAGGER ---
                // 1. ADICIONA A EXIGÊNCIA DE SEGURANÇA GLOBALMENTE
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // 2. DEFINE O ESQUEMA DE SEGURANÇA
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                // --- FIM DA SEÇÃO DE SEGURANÇA ---

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
