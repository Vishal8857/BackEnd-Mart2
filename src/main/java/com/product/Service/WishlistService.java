package com.product.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.product.Entity.Product;
import com.product.Repository.WishlistRepo;
import com.product.response.ProductResponse;

@Service
public class WishlistService {

	@Autowired
	WishlistRepo wishlistRepo;
	
	public List<ProductResponse> wishlist(Long userId){
		List<Product> productList=wishlistRepo.findWishlistProduct(userId);
		List<ProductResponse> productResponse=new ArrayList<>();
		
		for(Product product:productList) {
			ProductResponse response=new ProductResponse();
			
			response.setId(product.getId());
			response.setCategoryName(product.getCategory().getName());
			response.setCategoryId(product.getCategory().getCategoryId());
			response.setDescription(product.getDescription());
			response.setImage(product.getImage());
			response.setPrice(product.getPrice());
			response.setName(product.getName());
			response.setMessage("Wishlist Products...");
			
			productResponse.add(response);
		}
		return productResponse;
	}
}
