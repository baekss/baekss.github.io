package config.tutorial;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated
@Configuration
public class TutorialFilterConfig{
	
	@Bean
	public FilterRegistrationBean<CrossDomainFilter> CrossDomainFilter() throws Exception{
	    FilterRegistrationBean<CrossDomainFilter> filterRegistrationBean = new FilterRegistrationBean<>();
	         
	    filterRegistrationBean.setFilter(new CrossDomainFilter());
	    filterRegistrationBean.addUrlPatterns("/api/*");
	         
	    return filterRegistrationBean;    
	}
}
