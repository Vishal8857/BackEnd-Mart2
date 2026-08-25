package com.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.product.Service.OrderService;


@Component
public class ReportScheduler {

    @Autowired
    private OrderService userOrderService;

    @Scheduled(
        cron = "0 */15 * * * *",
        zone = "Asia/Kolkata"
    )
    public void generateReport() {

        userOrderService.generateAndSendReport();
    }
}
