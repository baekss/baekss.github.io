package config.tutorial;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class TutorialServletInitializer extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.sources(TutorialAppConfig.class, MockExtractorConfiguration.class);
		return application;
	}

}
