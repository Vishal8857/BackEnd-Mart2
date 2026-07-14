package com.product.Service;

import java.io.File;
import java.io.FileOutputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.DTO.TodaysOrderProductResponse;
import com.product.Entity.Product;
import com.product.Entity.User;
import com.product.Entity.UserOrder;
import com.product.Repository.ProductRepo;
import com.product.Repository.UserOrderRepo;
import com.product.Repository.UserRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserOrderService {

	private static final Logger logger=LoggerFactory.getLogger(UserOrderService.class);
	@Autowired
	UserOrderRepo userOrderRepo;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	UserRepo userRepo;
	
	//predefine class in java lab
	@Autowired
    private JavaMailSender mailSender;
	
	public UserOrder addUserOrder(long userId, long productId) {
		UserOrder userOrder=new UserOrder();
		userOrder.setUserID(userId);
		userOrder.setProductId(productId);
		userOrder.setTimestamp(LocalDateTime.now() );
		return userOrderRepo.save(userOrder);
	}
	
	//orders for specific user
	public List<Product> allOrder(long userId)
	{
		List<Product> order=new ArrayList<Product>();
		List<UserOrder> orderlist=userOrderRepo.findAll();
		List<Product> productList=productService.allProduct();
		
		if(orderlist.isEmpty()) {
			return null;
		}
		else {
		for(int i=0;i< orderlist.size();i++) {
			
			if(userId==orderlist.get(i).getUserID()) {
				for(int j=0; j<productList.size();j++)
				{
					if(orderlist.get(i).getProductId()==productList.get(j).getId()) {
						order.add(productList.get(j));
					}
				}
			}
		}
	}
		return order;
	}
	
	public List<TodaysOrderProductResponse> getTodaysOrder(){
		
		LocalDate today = LocalDate.now();
		
//		DayOfWeek weekStart=today.getDayOfWeek();//shows todays day like Monday, Tuesday 
//		int weekend=today.getDayOfMonth();//shows count of days in the current month 
//	    LocalDateTime startOfDay = today.atStartOfDay();
//	    LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
		LocalDate weekStart = today.with(DayOfWeek.MONDAY);
		LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

		LocalDateTime startOfWeek = weekStart.atStartOfDay();
		LocalDateTime endOfWeek = weekEnd.atTime(LocalTime.MAX);
		
	    List<UserOrder> orders=userOrderRepo.findByTimestampBetween(startOfWeek, endOfWeek);
	    
	    List<TodaysOrderProductResponse> response=new ArrayList<>();
		for( UserOrder order:orders) {
			Product product=productRepo.findById(order.getProductId()).orElse(null);
			User user=userRepo.findById(order.getUserID()).orElse(null);
				response.add(new TodaysOrderProductResponse(
						product.getName(),
						product.getPrice(),
						order.getTimestamp(),
						user.getMail()
						));
		}
	    return response;
	}
	
	//For generate Excel file of todays order list
	private File generateExcel() throws Exception {

	    List<TodaysOrderProductResponse> orders =
	    		getTodaysOrder();

	    Workbook workbook = new XSSFWorkbook();

	    Sheet sheet = workbook.createSheet("Orders");

	    Row header = sheet.createRow(0);
	    header.createCell(0).setCellValue("Serial Number");
	    header.createCell(1).setCellValue("Product Name");
	    header.createCell(2).setCellValue("Product Price");
	    header.createCell(3).setCellValue("Order Date");
	    header.createCell(4).setCellValue("UserName");

	    int rowNum = 1;
	    int grandTotal=0;
	    logger.info("Total orders: " + orders.size());
	    for (TodaysOrderProductResponse order : orders) {
	    	
	        Row row = sheet.createRow(rowNum++);
	        row.createCell(0).setCellValue(rowNum);
	        row.createCell(1).setCellValue(order.getProductName());
	        row.createCell(2).setCellValue(order.getPrice());
	        row.createCell(3).setCellValue(order.getTimestamp().toString());
	        row.createCell(4).setCellValue(order.getUserMail());
	        grandTotal+=order.getPrice();
	    }
	 // Grand Total Row
	    Row totalRow = sheet.createRow(rowNum);

	    totalRow.createCell(0).setCellValue("Grand Total");
	    totalRow.createCell(1).setCellValue("");
	    totalRow.createCell(2).setCellValue(grandTotal);
	    totalRow.createCell(3).setCellValue("");
	    totalRow.createCell(4).setCellValue(rowNum);
	    
	    File file = new File("daily-orders.xlsx");

	    FileOutputStream fos = new FileOutputStream(file);

	    workbook.write(fos);

	    workbook.close();

	    fos.close();

	    return file;
	}
	
	//use to generate Excel file by calling generateExcel() and call sendMail method to send mail 
	public void generateAndSendReport() {

        try {

            File file = generateExcel();
            logger.info("Before sendMail()");
            sendMail(file);
            logger.info("After sendMail()");
        } catch (Exception e) {

            logger.error("Error occure while generating Weekly report.."+e);
        }
    }
	
	//create mail Data and send mail  
	private void sendMail(File file)
	        throws MessagingException {

	    MimeMessage mimeMessage =
	            mailSender.createMimeMessage();

	    MimeMessageHelper helper =
	            new MimeMessageHelper(mimeMessage, true);
	    
	    helper.setTo("vbhosale909591@gmail.com");
	    helper.setSubject("Daily Orders Report");
	    helper.setText("Hi Team, Please find attached today's order report.");
	    helper.addAttachment(file.getName(), file);
	    
	    logger.info("Excel exists: " + file.exists());
	    logger.info("Path: " + file.getAbsolutePath());
	    mailSender.send(mimeMessage);
	}
}
