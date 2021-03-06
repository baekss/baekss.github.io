package com.bss.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bss.repository.TutorialRepository;

import model.tutorial.Product;

@Service
public class TutorialService {
	
	@Autowired
	TutorialRepository repository;
	
	public List<Product> getProducts(long categoryId) throws Exception{
		List<Product> products = repository.getProducts(); 
		return products;
	}
	
	public Product getProduct(String id, int price) throws Exception{
		return repository.getProduct(id, price);
	}
}
