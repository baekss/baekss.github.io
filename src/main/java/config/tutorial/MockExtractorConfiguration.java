package config.tutorial;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

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
			//http 통신시 타임아웃 설정
			HttpClient httpClient = HttpClient.create()
		            .tcpConfiguration(client ->
		                    client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
		                    .doOnConnected(conn -> conn
		                            .addHandlerLast(new ReadTimeoutHandler(10))
		                            .addHandlerLast(new WriteTimeoutHandler(10))));
		     
		    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);    
			
			//직렬화, 역직렬화를 위한 strategies 
			ExchangeStrategies strategies = ExchangeStrategies.builder()
											.codecs(clientDefaultCodecs -> {
												clientDefaultCodecs.defaultCodecs()
												.jackson2JsonEncoder(new Jackson2JsonEncoder(newObjectMapper(), MediaType.APPLICATION_JSON));
												
												clientDefaultCodecs.defaultCodecs()
												.jackson2JsonDecoder(new Jackson2JsonDecoder(newObjectMapper(), MediaType.APPLICATION_JSON));
												}).build();
			//WebClient는 thread safe하지 않으므로 builder를 통해 메소드 내에서 객체를 새로 생성하여 사용
			this.builder = WebClient.builder()
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.clientConnector(connector)
					.exchangeStrategies(strategies)
					.baseUrl("http://m.bss.com:9800");
		}
		
		//custom annotation을 만들어서 pointcut을 확대
		@AfterReturning(pointcut="@within(org.springframework.stereotype.Repository)", returning="retVal")
		public void saveMock(JoinPoint joinPoint, Object retVal) throws Exception {
			String packageName = joinPoint.getTarget().getClass().getPackageName();
			String className = joinPoint.getTarget().getClass().getSimpleName();
			String methodName = joinPoint.getSignature().getName();
			String parameterTypes = Arrays.stream(joinPoint.getArgs())
					.map(arg->arg.getClass().getName())
					.collect(Collectors.joining(","));
			Object returnObject = retVal;
			
			MockMethod body = new MockMethod(methodName, parameterTypes, newObjectMapper().writeValueAsString(returnObject), new Mock(packageName, className));
			
			WebClient webClient = builder.build();
			webClient.post().uri("/api/mock").bodyValue(body).retrieve().bodyToMono(Void.class).block();
		}
	}
	
	class Mock{
		private String packageName;
		private String className;
		
		Mock(String packageName, String className) {
			super();
			this.packageName = packageName;
			this.className = className;
		}
	}
	
	class MockMethod {
		private String methodName;
		private String parameterTypes;
		private String returnObject;
		private Mock mock;
		
		MockMethod(String methodName, String parameterTypes, String returnObject, Mock mock) {
			super();
			this.methodName = methodName;
			this.parameterTypes = parameterTypes;
			this.returnObject = returnObject;
			this.mock = mock;
		}
	}
}



