package com.postion.airlineorderbackend.dto;

import com.postion.airlineorderbackend.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class OrderDto {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal amount;
    private LocalDateTime creationDate;
    private UserDto user;
    // 用于聚合航班模拟信息
    private Map<String, Object> flightInfo;
    
    @Data
    public static class UserDto {
    	private Long id;
    	private String username; 
    }
}
