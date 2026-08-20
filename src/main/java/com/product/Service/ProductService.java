package com.product.Service;


import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.DTO.ProductDTO;
import com.custom.exception.ProductNotFoundException;
import com.product.Entity.Category;
import com.product.Entity.Product;
import com.product.Repository.CategoryRepo;
import com.product.Repository.ProductRepo;
import com.product.response.ProductResponse;
import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ProductRepo productRepo;

	@Autowired
	private CategoryRepo categoryRepo;

	
	public Product createProduct(Product product) {

		return productRepo.save(product);
	}

	public List<Product> allProduct() {
		List<Product> productList = productRepo.findAll();

		return productList;
	}

	public Page<ProductResponse> getProductList(int page, int size){
		
		Pageable pageable=PageRequest.of(page, size);
		Page<Product> productList=productRepo.findAll(pageable);
		
		return productList.map(product -> {
			ProductResponse response = new ProductResponse();

			response.setId(product.getId());
			response.setName(product.getName());
			response.setDescription(product.getDescription());
			response.setPrice(product.getPrice());
			response.setCategoryId(product.getCategory().getCategoryId());
			response.setCategoryName(product.getCategory().getName());
			response.setMessage("Product details");
			
			return response;
		});
	}
	
	@Cacheable(value = "product", key = "#id")
	public Product getProduct(Long id) {
		logger.info("finding Product from DB");
		return productRepo.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
	}

	public Optional<Product> getProd(Long productId) {

		return productRepo.findById(productId);
	}

	@CacheEvict(value = "product", key = "#id")
	public void deleteUser(Long id) {
		Product product = productRepo.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id:" + id));

		productRepo.delete(product);
	}

	@Transactional
	@CachePut(value = "products", key = "#id")
	public ProductResponse updateProduct(Long id, ProductDTO dto) throws IOException {

		Product product = productRepo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

		Category category = categoryRepo.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));

		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setCategory(category);

		// Update image only if a new image is provided
		if (dto.getImage() != null && !dto.getImage().isEmpty()) {
			try {
				product.setImage(dto.getImage().getBytes());
			} catch (java.io.IOException e) {
				logger.error("Error while updating product image...");
				e.printStackTrace();
			}
		}

		Product updatedProduct = productRepo.save(product);

		ProductResponse response = new ProductResponse();

		response.setId(updatedProduct.getId());
		response.setName(updatedProduct.getName());
		response.setDescription(updatedProduct.getDescription());
		response.setPrice(updatedProduct.getPrice());
		response.setCategoryId(updatedProduct.getCategory().getCategoryId());
		response.setCategoryName(updatedProduct.getCategory().getName());
		response.setMessage("Product details has updated...");

		return response;
	}

}
