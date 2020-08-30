package config.tutorial;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder
									.json()
									//직렬화 또는 역직렬화시 FIELD로만 핸들링 하게 한다.
									.visibility(PropertyAccessor.ALL, Visibility.NONE)
									.visibility(PropertyAccessor.FIELD, Visibility.ANY)
									//enum 타입은 name을 이용해 핸들링 한다.
									.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
									.featuresToEnable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
									.build();
		
		WebClient.Builder builder = WebClient.builder()
									.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
									.baseUrl("http://m.bss.com:9800");
		return MockExtractor.create(objectMapper, builder);
	}
	
	@Aspect
	static class MockExtractor {
		
		private WebClient webClient;
		private ObjectMapper objectMapper;
		
		public MockExtractor(ObjectMapper objectMapper, WebClient.Builder builder) {
			this.objectMapper = objectMapper;
			this.injectWebClient(builder);
		}
		
		public static MockExtractor create(ObjectMapper objectMapper, WebClient.Builder builder){
			return new MockExtractor(objectMapper, builder);
		}
		
		private void injectWebClient(WebClient.Builder builder) {
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
			this.webClient = builder
					.clientConnector(connector)
					.exchangeStrategies(strategies)
					.build();
		}
		
		//custom annotation을 만들어서 pointcut을 확대
		@AfterReturning(pointcut="@within(org.springframework.stereotype.Repository)", returning="retVal")
		public void saveMock(JoinPoint joinPoint, Object retVal) throws Exception {
			String packageName = joinPoint.getTarget().getClass().getPackageName();
			String className = joinPoint.getTarget().getClass().getSimpleName();
			String methodName = joinPoint.getSignature().getName();
			
			
			//primitive type 도 wrapper class 명으로 저장된다. 오버로딩 메소드 구분에 어려움 발생.
			/*
			String parameterTypes = Arrays.stream(joinPoint.getArgs())
					.map(arg->arg.getClass().getTypeName())
					.collect(Collectors.joining(","));
			*/
			
			String parameterTypes = joinPoint.getSignature().toLongString();
			int begin = parameterTypes.lastIndexOf("(");
			int end = parameterTypes.lastIndexOf(")");
			parameterTypes = parameterTypes.substring(begin+1, end);
			
			Object returnObject = retVal;
			
			MockMethod body = new MockMethod(methodName, parameterTypes, objectMapper.writeValueAsString(returnObject), new Mock(packageName, className));
			
			Mono<Void> mono = this.webClient.post().uri("/api/mock").bodyValue(body).retrieve().bodyToMono(Void.class);
			mono.subscribe(s->s.TYPE.toString());
		}
	}
	
}