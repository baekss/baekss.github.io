package com.bss.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bss.service.TutorialService;

import model.tutorial.Product;

@Controller
public class TutorialController {

	@Autowired
	private TutorialService tutorialService;
	
	@RequestMapping(path="/products/{categoryId}", method=RequestMethod.GET)
	@ResponseBody
	public List<Product> getCategoryProducts(@PathVariable("categoryId") long categoryId) throws Exception{
		List<Product> products = tutorialService.getProduct(categoryId);
		return products;
	}
}
