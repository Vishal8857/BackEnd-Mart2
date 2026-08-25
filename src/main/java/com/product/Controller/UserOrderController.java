package com.product.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DTO.OrderRequest;
import com.DTO.TodaysOrderProductResponse;
import com.product.Entity.OrderEntity;
import com.product.Service.OrderService;
import com.product.response.OrderResponse;

@RestController
@RequestMapping("/userOrder")
public class UserOrderController {

    private final OrderService userOrderService;

    public UserOrderController(OrderService userOrderService) {
        this.userOrderService = userOrderService;
    }

    // =====================================================
    // CREATE ORDER
    // =====================================================

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication,
            @RequestBody OrderRequest request) {

        Long userId = (Long) authentication.getPrincipal();

        OrderResponse response =
                userOrderService.createOrder(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =====================================================
    // GET USER ORDERS
    // =====================================================

    @GetMapping("/orders")
    public ResponseEntity<List<OrderEntity>> getMyOrders(
            Authentication authentication) {

        // User ID comes from JWT
        Long userId = (Long) authentication.getPrincipal();

        List<OrderEntity> orders =
                userOrderService.getUserOrders(userId);

        return ResponseEntity.ok(orders);
    }


    // =====================================================
    // TODAY'S ORDERS - ADMIN
    // =====================================================

    @GetMapping("/todaysOrderList")
    public ResponseEntity<List<TodaysOrderProductResponse>> todaysOrder() {

        List<TodaysOrderProductResponse> orders =
                userOrderService.getTodaysOrder();

        return ResponseEntity.ok(orders);
    }
}