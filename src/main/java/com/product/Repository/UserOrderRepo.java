package com.product.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.Entity.UserOrder;

@Repository
public interface UserOrderRepo extends JpaRepository<UserOrder, Long>{

	List<UserOrder> findByTimestampBetween(LocalDate weekStart, LocalDate weekEnd);
	List<UserOrder> findByTimestampBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

	
}
