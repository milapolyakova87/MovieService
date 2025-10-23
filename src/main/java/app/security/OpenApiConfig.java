package app.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Service API")
                        .version("1.0.0")
                        .description("API для работы с фильмами и списками пользователей")
                )
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"));
    }
}
