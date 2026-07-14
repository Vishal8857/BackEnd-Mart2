package com.DTO;

import java.time.LocalDateTime;

public class TodaysOrderProductResponse {

	private String productName;
	private double price;
	private LocalDateTime timestamp;
	private String userMail;

	
	public TodaysOrderProductResponse(String productName, double price, LocalDateTime timestamp, String userMail) {
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}