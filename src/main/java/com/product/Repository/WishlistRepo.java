package com.product.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.product.Entity.Product;
import com.product.Entity.Wishlist;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface WishlistRepo extends JpaRepository<Wishlist, Long>{
	
	@Query(value ="""
			select p.* 
			from wishlist w 
			join product p ON w.product_id=p.id 
			where w.user_id=:userId
			""", nativeQuery = true)
	public List<Product> findWishlistProduct(@Param("userId") Long userId);
}
