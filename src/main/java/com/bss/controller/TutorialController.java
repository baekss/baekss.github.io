package com.bss.controller;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bss.service.TutorialService;

import model.tutorial.Product;

@EnableCircuitBreaker
@Controller
@RequestMapping(path="/")
public class TutorialController {
	
	private static Logger logger = LoggerFactory.getLogger(TutorialController.class);
	
	@Autowired
	private TutorialService tutorialService;
	
	@PostConstruct
	public void init() {
		logger.info("TutorialController init");
	}
	
	@RequestMapping(path="")
	public String appMain()throws Exception {
		return "view";
	}
	
	@RequestMapping(path="circuitBreaker")
	public String appCircuitBreakerTest()throws Exception {
		return "circuitBreakerTest";
	}
	
	@RequestMapping(path="/products/{categoryId}", method=RequestMethod.GET)
	@ResponseBody
	public List<Product> getCategoryProducts(@PathVariable("categoryId") long categoryId) throws Exception{
		List<Product> products = tutorialService.getCategoryProducts(categoryId);
		return products;
	}
}
