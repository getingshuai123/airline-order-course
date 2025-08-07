package com.postion.airlineorderbackend.service;

import java.util.List;
import java.util.Optional;

import com.postion.airlineorderbackend.dto.OrderDto;

public interface OrderService {
	
	// 获取所有订单
	List<OrderDto> getAllOrders();
	
	// 获取选中的订单
	Optional<OrderDto> getOrderById(Long id);
	
	// 订单为未支付状态，调用他系统进行状态扭转
	OrderDto payOrder(Long id);
	
	// 取消订单
	OrderDto cancelOrder(Long id);
	
	// 重试 调用异步方法
	void requestTicketIssuance(Long id);
}
