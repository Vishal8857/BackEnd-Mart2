package com.product.Controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.DTO.TodaysOrderProductResponse;
import com.product.CommanClasses.UserResponse;
import com.product.Entity.Product;
import com.product.Entity.UserOrder;
import com.product.Service.ProductService;
import com.product.Service.UserOrderService;

@RestController
@RequestMapping("api/userOrder")
public class UserOrderController {

	@Autowired
	UserOrderService userOrderService;
	@Autowired
	ProductService productService;
	
	@GetMapping("/addOrder")
	public ResponseEntity<UserResponse> addOrder(
			@RequestParam("userId") long userId,
			@RequestParam("productId") long productId
			)throws IOException{
		UserOrder userOrder= userOrderService.addUserOrder(userId, productId);
		UserResponse response=new UserResponse("Order added successfully...."+ userOrder.getProductId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/orderList")
	public List<Product> orderList(long userId){
		return userOrderService.allOrder(userId);
	}
	
	@GetMapping("/todaysOrderList")
	public List<TodaysOrderProductResponse> todaysOrder(){
		return userOrderService.getTodaysOrder();
	}
	
}
