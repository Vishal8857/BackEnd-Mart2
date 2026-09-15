package com.product.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product.Entity.OrderEntity;
import com.product.response.TodaysOrderProductResponse;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {

	@Query("""
		    SELECT DISTINCT o
		    FROM OrderEntity o
		    JOIN FETCH o.orderItems oi
		    JOIN FETCH oi.product
		    WHERE o.user.id = :userId
		    ORDER BY o.orderDate DESC
		""")
		List<OrderEntity> findOrdersByUserId(
		        @Param("userId") Long userId
		);

    @Query("""
        SELECT new com.product.response.TodaysOrderProductResponse(
            p.name,
            oi.price,
            o.orderDate,
            u.mail
        )
        FROM OrderEntity o
        JOIN o.orderItems oi
        JOIN oi.product p
        JOIN o.user u
        WHERE o.orderDate BETWEEN :start AND :end
    """)
    List<TodaysOrderProductResponse> findOrdersForReport(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}