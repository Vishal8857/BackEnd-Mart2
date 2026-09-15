package com.product.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.product.Entity.CartEntity;

public interface CartRepo extends JpaRepository<CartEntity, Long>{

	@Query("""
			select c from CartEntity c 
			join fetch c.product 
			where c.user.id=:userId 
			""")
	public List<CartEntity> cartDataList(@Param(value = "userId") Long userId);
	
	 Optional<CartEntity> findByUserIdAndProductId(
	            Long userId,
	            Long productId
	    );
	
	 Optional<CartEntity> findByProductIdAndUserId(Long productId,Long userId);
}
