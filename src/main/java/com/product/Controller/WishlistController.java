package com.product.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.product.Repository.WishlistRepo;
import com.product.Service.WishlistService;
import com.product.response.ProductResponse;

@RestController
@RequestMapping("user/wishlist")
public class WishlistController {

	@Autowired
	WishlistRepo wishlistRepo;
	
	@Autowired
	WishlistService wishlistService;
	
	@GetMapping("/wishlist")
	public ResponseEntity<List<ProductResponse>> wishlistProduct(Authentication authentication){
		
		Long userId=(Long)authentication.getPrincipal();
		
		List<ProductResponse> productList=wishlistService.wishlist(userId);
		
		return ResponseEntity.status(HttpStatus.OK).body(productList);
	}
}
