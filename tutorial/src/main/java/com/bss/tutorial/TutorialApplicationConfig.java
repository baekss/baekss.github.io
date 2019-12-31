package com.bss.tutorial;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages="com.bss.*")
public class TutorialApplicationConfig implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String[] resourceHandler = new String[]{"/resource/**"};
		String[] resourceLocations = new String[]{"classpath:/static/resource/"};
	    registry.addResourceHandler(resourceHandler).addResourceLocations(resourceLocations);
	}
}
