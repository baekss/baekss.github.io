package config.tutorial;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class TutorialServletInitializer extends SpringBootServletInitializer {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		Class[] configClass = new Class[]{TutorialAppConfig.class, MockExtractorConfiguration.class};
		application.sources(configClass);
		return application;
	}

}
