package com.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TodaysOrderProductResponse {

	private String productName;
	private BigDecimal price;
	private LocalDateTime timestamp;
	private String userMail;

	
	public TodaysOrderProductResponse(String productName, BigDecimal price, LocalDateTime timestamp, String userMail) {
		super();
		this.productName = productName;
		this.price = price;
		this.timestamp = timestamp;
		this.userMail = userMail;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}