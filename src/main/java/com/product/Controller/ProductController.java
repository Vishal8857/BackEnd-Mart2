package com.product.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.product.Entity.Category;
import com.product.Entity.Product;
import com.DTO.ProductDTO;
import com.product.CommanClasses.UserResponse;
import com.product.Repository.CategoryRepo;
import com.product.Service.ProductService;
import com.product.response.ProductResponse;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryRepo categoryRepository;

	// ✅ Create product with image
	@PostMapping("/create")
	public ResponseEntity<UserResponse> createProduct(@RequestParam("name") String name,
			@RequestParam("description") String description, @RequestParam("price") double price,
			@RequestParam("categoryId") Long categoryId, @RequestParam("image") MultipartFile imageFile)
			throws IOException {

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setPrice(price);
		product.setCategory(category);
		product.setImage(imageFile.getBytes()); // Store image as BLOB

		Product savedProduct = productService.createProduct(product);
		UserResponse response = new UserResponse("Product added successfully...", savedProduct.getName());
		logger.info("New Product added successfully...");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/allProduct")
	public List<Product> allProduct() {
		return productService.allProduct();
	}
	
	@GetMapping("/allProductPage")
	public ResponseEntity<Page<ProductResponse>> getAllProduct(
			@RequestParam(value="page", defaultValue = "0") int page,
			@RequestParam(value="size", defaultValue = "10") int size
			){
		Page<ProductResponse> pageProduct=productService.getProductList(page, size);
		return ResponseEntity.status(HttpStatus.OK).body(pageProduct);
	}

	@GetMapping("/image")
	public ResponseEntity<byte[]> getProductImage(@RequestParam Long id) {
		Optional<Product> optionalProduct = productService.getProd(id);

		if (optionalProduct.isEmpty() || optionalProduct.get().getImage() == null) {
			return ResponseEntity.notFound().build();
		}

		Product product = optionalProduct.get();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE) // or PNG
				.body(product.getImage());
	}

	@GetMapping("/getProduct/{productId}")
	public Product getProduct(@PathVariable Long productId) {
		return productService.getProduct(productId);
	}
	
	@DeleteMapping("/deleteProduct/{productID}")
	public ResponseEntity<UserResponse> deleteProduct(@PathVariable Long productId ){
		
		productService.deleteUser(productId);
		UserResponse response=new UserResponse("Product Deleted successfully.. with id:"+ productId);	
		logger.info("product deleted successfully with id: "+productId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PutMapping(value="/updateProduct/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ProductResponse> updateProduct(
			@PathVariable Long productId,
			@ModelAttribute ProductDTO productDTO){
				
		ProductResponse productResponse=productService.updateProduct(productId, productDTO);
		
		return ResponseEntity.status(HttpStatus.OK).body(productResponse);
	}
}
