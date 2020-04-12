package com.bss.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import model.tutorial.Product;
import reactor.core.publisher.Mono;

@Service
public class TutorialService {
	private static Logger logger = LoggerFactory.getLogger(TutorialService.class);
	
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
		//Circuit Breaker 발생시킴
		Mono<List> mono = webClient.get().uri("/api/products/"+categoryId).retrieve().bodyToMono(List.class);
		products = mono.block();
		return products;
	}
	
	public List<Product> getTempProduct(long categoryId) throws Exception{
		List<Product> products = new ArrayList<Product>();
		products.add(new Product("Temp-A상품","L",12000));
		products.add(new Product("Temp-B상품","M",15000));
		products.add(new Product("Temp-C상품","S",22000));
		return products; 
	}
}
