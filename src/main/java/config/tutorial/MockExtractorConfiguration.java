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
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
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
		{
			this.objectMapper = newObjectMapper();
		}
		private WebClient webClient;
		private ObjectMapper objectMapper;
		
		public MockExtractor() {
			super();
			this.injectWebClient();
		}

		public ObjectMapper newObjectMapper() {
			return Jackson2ObjectMapperBuilder
					.json()
					//직렬화 또는 역직렬화시 FIELD로만 핸들링 하게 한다.
					.visibility(PropertyAccessor.ALL, Visibility.NONE)
					.visibility(PropertyAccessor.FIELD, Visibility.ANY)
					.build();
		} 
		
		private void injectWebClient() {
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
												.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
												
												clientDefaultCodecs.defaultCodecs()
												.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
												}).build();
			//WebClient는 불변객체 이므로 타 host와의 연동이 필요할 땐 mutate()를 호출하여 커스터마이징 한다.
			this.webClient = WebClient.builder()
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.clientConnector(connector)
					.exchangeStrategies(strategies)
					.baseUrl("http://m.bss.com:9800")
					.build();
		}
		
		//custom annotation을 만들어서 pointcut을 확대
		@AfterReturning(pointcut="@within(org.springframework.stereotype.Repository)", returning="retVal")
		public void saveMock(JoinPoint joinPoint, Object retVal) throws Exception {
			String packageName = joinPoint.getTarget().getClass().getPackageName();
			String className = joinPoint.getTarget().getClass().getSimpleName();
			String methodName = joinPoint.getSignature().getName();
			
			//primitive type 도 wrapper class 명으로 저장된다.
			String parameterTypes = Arrays.stream(joinPoint.getArgs())
					.map(arg->arg.getClass().getTypeName())
					.collect(Collectors.joining(","));
			
			Object returnObject = retVal;
			
			MockMethod body = new MockMethod(methodName, parameterTypes, objectMapper.writeValueAsString(returnObject), new Mock(packageName, className));
			
			Mono<Void> mono = this.webClient.post().uri("/api/mock").bodyValue(body).retrieve().bodyToMono(Void.class);
			mono.subscribe(s->s.TYPE.toString());
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



