package com.postion.airlineorderbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders/{id}")
@RequiredArgsConstructor
public class OrderActionController {
	
	// service层
	private final OrderService orderService;
	
	// 订单为未支付状态，调用他系统进行状态扭转
	@PostMapping("/pay")
	public ResponseEntity<OrderDto> pay(@PathVariable Long id){
		try {
			return ResponseEntity.ok(orderService.payOrder(id));
		}catch(IllegalStateException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	// 取消订单
	@PostMapping("/cancel")
	public ResponseEntity<OrderDto> cancel(@PathVariable Long id){
		try {
			return ResponseEntity.ok(orderService.cancelOrder(id));
		}catch(IllegalStateException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	// 重试(前台状态为大量支付完成，待出票的状态，确认航空公司状态，二次更新，手动触发更新状态，并返回给前台刷新状态)
	@PostMapping("/retry-ticketing")
	public ResponseEntity<Void> retryTicketing(@PathVariable Long id){
		// 调用异步方法，返回202 Accept
		orderService.requestTicketIssuance(id);
		return ResponseEntity.accepted().build();
	}

}
