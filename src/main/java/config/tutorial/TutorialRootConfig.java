package config.tutorial;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages="com.bss", useDefaultFilters=true, excludeFilters={@Filter(type=FilterType.ANNOTATION, value=Controller.class)})
public class TutorialRootConfig{
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
	    return new WebMvcConfigurer() {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/api/**")
	            .allowedOrigins("http://m.bss.co.kr:8080")
	            .allowedMethods(new String[]{HttpMethod.DELETE.name(), HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name(), HttpMethod.POST.name(), HttpMethod.PUT.name()})
	            .allowedHeaders(new String[]{"Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Headers", "Access-Control-Request-Method"})
	            .allowCredentials(true);
	        }
	    };
	}
}
