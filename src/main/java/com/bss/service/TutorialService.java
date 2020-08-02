package com.bss.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bss.repository.TutorialRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import model.tutorial.Product;
import reactor.core.publisher.Mono;

@Service
public class TutorialService {
	private static Logger logger = LoggerFactory.getLogger(TutorialService.class);
	
	@Autowired
	TutorialRepository repository;
	
	@Autowired 
	WebClient.Builder builder;
	
	@PostConstruct
	public void init() {
		logger.debug("TutorialService init");
	}
	
	@HystrixCommand(fallbackMethod = "getTempProduct")
	public List<Product> getCategoryProducts(long categoryId) throws Exception{
		List<Product> products = null;
		WebClient webClient = builder.build();
		//application.properties에서 api서버의 포트번호를 바꿔 의도적으로 Circuit Breaker 발생시킴
		Mono<List> mono = webClient.get().uri("/api/products/"+categoryId).retrieve().bodyToMono(List.class);
		products = mono.block();
		return products;
	}
	
	public List<Product> getTempProduct(long categoryId) throws Exception{
		List<Product> products = repository.getProducts(); 
		return products;
	}
}
