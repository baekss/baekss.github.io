package config.tutorial;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@ComponentScan(basePackages="com.bss", useDefaultFilters=true, excludeFilters={@Filter(type=FilterType.ANNOTATION, value=Controller.class)})
public class TutorialRootConfig{
	
	
}
