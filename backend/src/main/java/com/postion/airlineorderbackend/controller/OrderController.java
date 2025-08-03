package com.postion.airlineorderbackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
	
	// service层依赖
	private final OrderService orderService;
	
	// 获取所有订单
	@GetMapping
	public List<OrderDto> getAllOrders(){
		return orderService.getAllOrders();
	}
	
	// 获取选中的订单
	@GetMapping("/{id}")
	public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
		Optional<OrderDto> getOrder = orderService.getOrderById(id);
		return getOrder.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

}
