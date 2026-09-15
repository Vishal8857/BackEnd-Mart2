package com.DTO;

import jakarta.validation.constraints.NotNull;

public class CartDTO {

	@NotNull(message="Please select the product...")
	private Long productId;
	
	private Integer quntity=1;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Integer getQuntity() {
		return quntity;
	}

	public void setQuntity(Integer quntity) {
		this.quntity = quntity;
	}
}
