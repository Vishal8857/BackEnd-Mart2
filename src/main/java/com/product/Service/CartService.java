package com.product.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.DTO.CartDTO;
import com.custom.exception.ProductNotFoundException;
import com.product.Entity.CartEntity;
import com.product.Entity.Product;
import com.product.Entity.User;
import com.product.Repository.CartRepo;
import com.product.Repository.ProductRepo;
import com.product.response.CartResponse;

@Service
public class CartService {

	private final CartRepo cartRepo;
	private final ProductRepo productRepo;

	public CartService(CartRepo cartRepo, ProductRepo productRepo) {
		this.cartRepo = cartRepo;
		this.productRepo = productRepo;
	}

	@Cacheable(value = "cart", key = "#userId")
	public List<CartResponse> cartList(Long userId) {

		List<CartEntity> cart = cartRepo.cartDataList(userId);

		return cart.stream().map(cartData -> {

			CartResponse response = new CartResponse();

			response.setQuntity(cartData.getQuantity());
			response.setProductName(cartData.getProduct().getName());
			response.setDescripation(cartData.getProduct().getDescription());
			response.setImage(cartData.getProduct().getImage());
			response.setPrice(cartData.getProduct().getPrice());
			response.setCartId(cartData.getId());
			response.setProductId(cartData.getProduct().getId());

			return response;

		}).toList();
	}

	
	public String addCart(Long userId, CartDTO cartDto) {

		CartEntity cart = new CartEntity();

		Product product = productRepo.findById(cartDto.getProductId())
				.orElseThrow(() -> new ProductNotFoundException("Product Not found exception..."));
		Optional<CartEntity> cartData = cartRepo.findByProductIdAndUserId(cartDto.getProductId(), userId);

		if (cartData.isPresent()) {
			CartEntity cartQuntity = cartData.get();
			cartQuntity.setQuantity(cartQuntity.getQuantity() + cartDto.getQuntity());

			cartRepo.save(cartQuntity);
			return "Product Quntity has incresed...!";
		} else {
			cart.setQuantity(cartDto.getQuntity());
			User user = new User();
			user.setId(userId);
			cart.setUser(user);

			cart.setProduct(product);

			cartRepo.save(cart);

			return "Product added to cart successfully...!";
		}

	}
}