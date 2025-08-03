package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // repository层依赖
    private final OrderRepository orderRepository;

    // 获取所有订单
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        return allOrders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 获取选中的订单
    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(Long id) {
        return orderRepository.findById(id).map(this::convertToDto);
    }

    // 订单为未支付状态，调用他系统进行状态扭转
    @Override
    public OrderDto payOrder(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    // 取消订单
    @Override
    public OrderDto cancelOrder(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    // 重试 调用异步方法
    @Override
    public Void requestTicketIssuance(Long id) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * 将Order实体转换为OrderDto
     *
     * @param order
     * @return dto
     */
    private OrderDto convertToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setOrderNumber(order.getOrderNumber());
        orderDto.setStatus(order.getStatus());
        orderDto.setAmount(order.getAmount());
        orderDto.setCreationDate(order.getCreationDate());

        if (order.getUser() != null) {
            orderDto.setUser(convertUserToUserDto(order.getUser()));
        }

        return orderDto;
    }

    /**
     * 将User实体转换为UserDto
     *
     * @param user
     * @return
     */
    private UserDto convertUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        return userDto;
    }


}
