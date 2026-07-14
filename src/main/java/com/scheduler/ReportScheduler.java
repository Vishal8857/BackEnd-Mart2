package com.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.product.Service.UserOrderService;

@Component
public class ReportScheduler {

    @Autowired
    private UserOrderService userOrderService;

    @Scheduled(cron = "0 * * * * *",zone = "Asia/Kolkata")
    public void generateReport() {
    	System.out.println("Scheduler triggered at: " + LocalDateTime.now());
    	userOrderService.generateAndSendReport();

        System.out.println("Report generated successfully");
    }
}