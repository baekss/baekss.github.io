package com.bss.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import model.tutorial.Product;
import model.tutorial.Size;

@Repository
public class TutorialRepository {
	
	public List<Product> getProducts() {
		List<Product> products = new ArrayList<Product>();
		products.add(new Product("Temp-A상품",Size.L,12000));
		products.add(new Product("Temp-B상품",Size.M,15000));
		products.add(new Product("Temp-C상품",Size.S,22000));
		return products;
	}
	
	public Product getProduct(String id, int price) {
		return new Product("Temp-B상품",Size.L,15000);
	}
	
	
}
