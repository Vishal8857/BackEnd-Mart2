package com.product.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DTO.OrderItemRequest;
import com.DTO.OrderRequest;
import com.DTO.TodaysOrderProductResponse;
import com.product.Entity.OrderEntity;
import com.product.Entity.OrderItem;
import com.product.Entity.Product;
import com.product.Entity.User;
import com.product.Repository.OrderRepo;
import com.product.Repository.ProductRepo;
import com.product.Repository.UserRepo;
import com.product.response.OrderItemResponse;
import com.product.response.OrderResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final OrderRepo orderRepo;
	private final ProductRepo productRepo;
	private final UserRepo userRepo;
	private final JavaMailSender mailSender;

	public OrderService(OrderRepo orderRepo, ProductRepo productRepo, UserRepo userRepo, JavaMailSender mailSender) {

		this.orderRepo = orderRepo;
		this.productRepo = productRepo;
		this.userRepo = userRepo;
		this.mailSender = mailSender;
	}

	// =========================================================
	// CREATE ORDER
	// =========================================================

	@Transactional
	public OrderResponse createOrder(Long userId, OrderRequest request) {

		User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		if (request == null || request.getItems() == null || request.getItems().isEmpty()) {

			throw new RuntimeException("Order must contain at least one product");
		}

		OrderEntity order = new OrderEntity();

		order.setUser(user);
		order.setOrderDate(LocalDateTime.now());

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (OrderItemRequest itemRequest : request.getItems()) {

			if (itemRequest.getProductId() == null) {
				throw new RuntimeException("Product ID is required");
			}

			if (itemRequest.getQuantity() <= 0) {
				throw new RuntimeException("Quantity must be greater than zero");
			}

			Product product = productRepo.findById(itemRequest.getProductId())
					.orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

			OrderItem orderItem = new OrderItem();

			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(itemRequest.getQuantity());

			// Price at the time of purchase
			orderItem.setPrice(BigDecimal.valueOf(product.getPrice()));
			
			order.getOrderItems().add(orderItem);

			BigDecimal itemTotal =
					BigDecimal.valueOf(product.getPrice())
			               .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

			totalAmount = totalAmount.add(itemTotal);
		}

		order.setTotalAmount(totalAmount);

		// Save Order + OrderItems because of CascadeType.ALL
		OrderEntity savedOrder = orderRepo.save(order);

		// =========================
		// Create Response DTO
		// =========================

		OrderResponse response = new OrderResponse();

		response.setOrderId(savedOrder.getId());
		response.setOrderDate(savedOrder.getOrderDate());
		response.setTotalAmount(savedOrder.getTotalAmount());

		List<OrderItemResponse> itemResponses = new ArrayList<>();

		for (OrderItem item : savedOrder.getOrderItems()) {

			OrderItemResponse itemResponse = new OrderItemResponse();

			itemResponse.setProductId(item.getProduct().getId());
			itemResponse.setProductName(item.getProduct().getName());
			itemResponse.setQuantity(item.getQuantity());
			itemResponse.setPrice(item.getPrice());
			itemResponses.add(itemResponse);
		}

		response.setItems(itemResponses);

		return response;
	}

	// =========================================================
	// GET ORDERS OF SPECIFIC USER
	// =========================================================

	public List<OrderEntity> getUserOrders(Long userId) {

		// Check user exists
		userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		return orderRepo.findByUserId(userId);
	}

	// =========================================================
	// GET THIS WEEK'S ORDERS
	// =========================================================

	@Transactional(readOnly = true)
	public List<TodaysOrderProductResponse> getTodaysOrder() {

		LocalDate today = LocalDate.now();

		LocalDate weekStart = today.with(DayOfWeek.MONDAY);
		LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

		LocalDateTime startOfWeek = weekStart.atStartOfDay();
		LocalDateTime endOfWeek = weekEnd.atTime(LocalTime.MAX);

		List<OrderEntity> orders = orderRepo.findByOrderDateBetween(startOfWeek, endOfWeek);

		List<TodaysOrderProductResponse> response = new ArrayList<>();

		for (OrderEntity order : orders) {

			User user = order.getUser();

			for (OrderItem item : order.getOrderItems()) {

				Product product = item.getProduct();

				response.add(new TodaysOrderProductResponse(product.getName(), item.getPrice(), order.getOrderDate(),
						user.getMail()));
			}
		}

		return response;
	}

	// =========================================================
	// GENERATE EXCEL
	// =========================================================

	private File generateExcel() throws Exception {

		List<TodaysOrderProductResponse> orders = getTodaysOrder();

		Workbook workbook = new XSSFWorkbook();

		try {

			Sheet sheet = workbook.createSheet("Orders");

			// Header
			Row header = sheet.createRow(0);

			header.createCell(0).setCellValue("Serial Number");
			header.createCell(1).setCellValue("Product Name");
			header.createCell(2).setCellValue("Product Price");
			header.createCell(3).setCellValue("Order Date");
			header.createCell(4).setCellValue("User Email");

			int rowNum = 1;

			BigDecimal grandTotal = BigDecimal.ZERO;

			logger.info("Total order items in report: {}", orders.size());

			for (TodaysOrderProductResponse order : orders) {

				Row row = sheet.createRow(rowNum);

				row.createCell(0).setCellValue(rowNum);
				row.createCell(1).setCellValue(order.getProductName());
				row.createCell(2).setCellValue(order.getPrice().doubleValue());				row.createCell(3).setCellValue(order.getTimestamp().toString());
				row.createCell(4).setCellValue(order.getUserMail());
				
				grandTotal =grandTotal.add(order.getPrice());

				rowNum++;
			}

			// Grand Total
			Row totalRow = sheet.createRow(rowNum);

			totalRow.createCell(0).setCellValue("Grand Total");
			totalRow.createCell(2).setCellValue(grandTotal.doubleValue());
			totalRow.createCell(4).setCellValue(orders.size());

			// Auto-size columns
			for (int i = 0; i < 5; i++) {
				sheet.autoSizeColumn(i);
			}

			// Create file
			File file = new File("daily-orders.xlsx");

			try (FileOutputStream fos = new FileOutputStream(file)) {

				workbook.write(fos);
			}

			logger.info("Excel file generated: {}", file.getAbsolutePath());

			return file;

		} finally {

			workbook.close();
		}
	}

	// =========================================================
	// GENERATE EXCEL + SEND EMAIL
	// =========================================================

	public void generateAndSendReport() {

		try {

			File file = generateExcel();

			logger.info("Excel report generated: {}", file.getAbsolutePath());

			sendMail(file);

			logger.info("Order report email sent successfully");

		} catch (Exception e) {

			logger.error("Error occurred while generating order report", e);
		}
	}

	// =========================================================
	// SEND EMAIL
	// =========================================================

	private void sendMail(File file) throws MessagingException {

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo("vbhosale909591@gmail.com");
		helper.setSubject("Order Report");
		helper.setText("""
				Hi Team,

				Please find attached the order report.

				Regards,
				E-Commerce Application
				""");

		helper.addAttachment(file.getName(), file);

		logger.info("Excel exists: {}", file.exists());
		logger.info("Excel path: {}", file.getAbsolutePath());

		mailSender.send(mimeMessage);
	}
}