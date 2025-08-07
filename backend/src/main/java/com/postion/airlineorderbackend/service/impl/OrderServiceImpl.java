package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.postion.airlineorderbackend.Mapper.OrderMapper;
import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.dto.OrderDto.UserDto;
import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repository.OrderRepository;
import com.postion.airlineorderbackend.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    // log
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    // repository层依赖
    private final OrderRepository orderRepository;

    // DaoToDto
    private final OrderMapper orderMapper;

    // 模拟调用航司API出票
    private final AirlineApiClient airlineApiClient;

    // 获取所有订单
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {

        List<Order> allOrders = orderRepository.findAll();

        return allOrders.stream().map(orderMapper::toOrderDto).collect(Collectors.toList());
    }

    // 获取选中的订单
    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long id) {

        return orderRepository.findById(id).map(orderMapper::toOrderDto);
    }

    // 订单为未支付状态，调用他系统进行状态扭转
    @Override
    @Transactional
    public OrderDto payOrder(Long id) {
    	
        log.info("开始处理订单，订单ID: {}", id);
        
        // 获取对应ID的订单数据
        Optional<OrderDto> order = getOrderById(id);

        // 订单存在
        if(order.isPresent()){
            // 状态机校验：只有PENDING_PAYMENT（待支付）状态下的订单才能进行支付操作
            if(order.get().getStatus() != OrderStatus.PENDING_PAYMENT) {

            // 状态不符合，不可以进行支付操作
            }else{
                log.info("该订单不是未支付状态，无法进行支付，请确认！订单ID: {}", id);
            }
        // 订单不存在
        }else{
            log.info("订单不存在，请重试！订单ID: {}", id);
        }
        return null;
    }

    // 取消订单
    @Override
    public OrderDto cancelOrder(Long id) {
    	
    	log.info("开始处理订单，订单ID: {}", id);
    	
    	// 获取对应ID的订单数据
        Optional<OrderDto> order = getOrderById(id);

        // 订单存在
        if(order.isPresent()){
            // 状态机校验：只有PENDING_PAYMENT（待支付）状态下的订单才能进行支付操作
            if(order.get().getStatus() != OrderStatus.PENDING_PAYMENT) {
            	
            	
            }
        }
    	

        return null;
    }

    // 重试 调用异步方法
    @Override
    public void requestTicketIssuance(Long id) {
        // TODO Auto-generated method stub
    }

}
