package config.tutorial;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@ComponentScan(basePackages="com.bss", useDefaultFilters=true, excludeFilters={@Filter(type=FilterType.ANNOTATION, value=Controller.class)})
public class TutorialRootConfig{
	@Value("${tutorial.api.server}")
	private String apiServer;
	
	@Bean
    public WebClientCustomizer webClientCustomizer() {
        return (webClientBuilder) -> {webClientBuilder.baseUrl(apiServer);};
    }
}
