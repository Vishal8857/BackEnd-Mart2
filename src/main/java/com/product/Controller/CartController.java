package com.product.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.DTO.CartDTO;
import com.product.CommanClasses.UserResponse;
import com.product.Service.CartService;
import com.product.response.CartResponse;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService service;

	@GetMapping("/list")
	public ResponseEntity<List<CartResponse>> cartProduct(Authentication authentication) {
		Long userId = (Long) authentication.getPrincipal();
		List<CartResponse> cartList = service.cartList(userId);

		return ResponseEntity.status(HttpStatus.OK).body(cartList);
	}

	@PostMapping("/create")
	public ResponseEntity<UserResponse> addToCart(@RequestBody CartDTO cartDto, Authentication authentication) {

		Long userId = (Long) authentication.getPrincipal();
		String response = service.addCart(userId, cartDto);
		UserResponse responses = new UserResponse(response);
		return ResponseEntity.status(HttpStatus.CREATED).body(responses);
	}
}
