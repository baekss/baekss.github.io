package config.tutorial;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class MockExtractorConfiguration{
	
	@Bean(name="mockExtractor")
	public MockExtractor mockExtractor() {
		return new MockExtractor();
	}
	
	@Aspect
	class MockExtractor {
		private WebClient.Builder builder;
		
		public MockExtractor() {
			super();
			this.injectWebClientBuilder();
			System.out.println("MockExtractor Aspect init.");
		}

		public ObjectMapper newObjectMapper() {
			return Jackson2ObjectMapperBuilder
					.json()
					//직렬화 또는 역직렬화시 FIELD로만 핸들링 하게 한다.
					.visibility(PropertyAccessor.ALL, Visibility.NONE)
					.visibility(PropertyAccessor.FIELD, Visibility.ANY)
					.build();
		} 
		
		public void injectWebClientBuilder() {
			//직렬화, 역직렬화를 위한 strategies 
			ExchangeStrategies strategies = ExchangeStrategies.builder()
											.codecs(clientDefaultCodecs -> {
												clientDefaultCodecs.defaultCodecs()
												.jackson2JsonEncoder(new Jackson2JsonEncoder(newObjectMapper(), MediaType.APPLICATION_JSON));
												
												clientDefaultCodecs.defaultCodecs()
												.jackson2JsonDecoder(new Jackson2JsonDecoder(newObjectMapper(), MediaType.APPLICATION_JSON));
												}).build();
			//WebClient는 thread safe하지 않으므로 builder를 통해 메소드 내에서 객체를 새로 생성하여 사용
			this.builder = WebClient.builder().exchangeStrategies(strategies).baseUrl("");
			System.out.println("MockExtractor webClient is ok");
		}
		
		//custom annotation을 만들어서 pointcut을 확대
		@AfterReturning(pointcut="@within(org.springframework.stereotype.Repository)", returning="mock")
		public void saveMock(Object mock) throws Exception {
			builder.build();
		}
	}
}



