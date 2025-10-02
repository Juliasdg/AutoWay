package br.com.fatec.autoway.infra.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI autoWayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AutoWay API")
                        .description("Sistema de pedágio automático via RFID, com autenticação, gestão de usuários, veículos e geração de boletos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Autoway Team")
                                .email("autowayproject@gmail.com")
                                .url("https://github.com/fatec-zona-leste/solucao-iot-para-pedagios-automatizados"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação completa e código-fonte")
                        .url("https://github.com/fatec-zona-leste/solucao-iot-para-pedagios-automatizados"));
    }
}
