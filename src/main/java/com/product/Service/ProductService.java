package com.product.Service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.custom.exception.ProductNotFoundException;
import com.product.Entity.Product;
import com.product.Repository.ProductRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	private ProductRepo produtRepo;

	public Product createProduct(Product product) {
		
		return produtRepo.save(product);
	}

	@Cacheable(value = "productList", key = "")
	public List<Product> allProduct() {
		logger.info("Fetching from DB");
		List<Product> productList = produtRepo.findAll();

		return productList;
	}

	@Cacheable(value = "product", key = "#id")
	public Product getProduct(Long id) {
		logger.info("finding Product from DB");
		return produtRepo.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
	}

	@Cacheable(value = "prod", key = "#productId")
	public Optional<Product> getProd(Long productId) {
		logger.info("finding Product from DB");
		return produtRepo.findById(productId);
	}
}
