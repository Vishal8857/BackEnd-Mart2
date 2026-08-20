package com.product.response;

public class UserResponseDto {

	private long id;
	private String name;
	private String surname;
	private String mobile;
	private String mail;
	private String address;
	private String pinCode;
	private String role;
	private String massage;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMassage() {
		return massage;
	}
	public void setMassage(String massage) {
		this.massage = massage;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public UserResponseDto(long id, String name, String surname, String mobile, String mail, String address, String pinCode,
			String role, String massage) {
		super();
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.mobile = mobile;
		this.mail = mail;
		this.address = address;
		this.pinCode = pinCode;
		this.role = role;
		this.massage=massage;
	}
	
	public UserResponseDto() {
		// TODO Auto-generated constructor stub
	}
	
}
