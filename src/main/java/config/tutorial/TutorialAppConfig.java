package config.tutorial;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.bss")
public class TutorialAppConfig{
	@Value("${tutorial.api.server}")
	private String apiServer;
	
	@Bean
    public WebClientCustomizer webClientCustomizer() {
        return (webClientBuilder) -> {webClientBuilder.baseUrl(apiServer);};
    }
	
}
