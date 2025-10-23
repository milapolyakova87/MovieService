package app.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public WebClient insecureWebClient() {
        try {
            SslContext sslContext = getInsecureSslContext();
            HttpClient httpClient = HttpClient.create().secure(spec -> spec.sslContext(sslContext));

            return WebClient.builder()
                    .baseUrl("https://api.themoviedb.org/3")
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось создать WebClient с отключённым SSL", e);
        }
    }

    private SslContext getInsecureSslContext() throws SSLException {
        return SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
    }
}
