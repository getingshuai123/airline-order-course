package com.postion.airlineorderbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.postion.airlineorderbackend.Mapper.OrderMapper;
import com.postion.airlineorderbackend.adapter.outbound.AirlineApiClient;
import com.postion.airlineorderbackend.exception.BusinessException;
import com.postion.airlineorderbackend.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.dto.OrderDto;
import com.postion.airlineorderbackend.model.Order;
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
    @Transactional(readOnly = true)
    public OrderDto payOrder(Long id) {
        log.info("开始处理订单，订单ID: {}", id);

        // 1. 使用Optional链式调用:查询订单 -> 转换为Dto -> 处理空值
        OrderDto orderDto = getOrderById(id).orElseThrow(() -> new BusinessException(
                // 404
                HttpStatus.NOT_FOUND, "订单不存在，无法支付，订单ID:{}" + id
        ));

        // 2.状态机校验:只有PENDING_PAYMENT（待支付）状态下的订单才能进行支付操作
        if (orderDto.getStatus() != OrderStatus.PENDING_PAYMENT) {
            log.error("该订单不是未支付状态，无法进行支付，请确认！订单ID: {}, 当前订单状态: {}", id, orderDto.getStatus());
            // 400
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST, "仅待支付订单可执行支付操作，当前状态:" + orderDto.getStatus()
            );
        }

        // 3. 调用模拟支付接口（使用AirlineApiClient模拟）
        boolean paymentSuccess;
        String errorMessage = null;

        try {
            // 这里复用出票方法模拟支付（实际项目中应调用专门的支付API）
            // 支付成功会返回类似支付凭证的字符串，失败会抛出异常
            String paymentToken = airlineApiClient.issueTikcket(id);
            paymentSuccess = true;
            log.info("订单支付成功，支付凭证:{}", paymentToken);
        } catch (InterruptedException e) {
            // 处理线程中断（超时）
            log.error("支付请求超时，订单ID:{}", id, e);
            // 408
            throw new BusinessException(
                    HttpStatus.REQUEST_TIMEOUT, "支付请求超时，请稍后重试"
            );
        } catch (RuntimeException e) {
            // 支付失败
            log.error("支付失败，订单:{}", id, e);
            paymentSuccess = false;
            // 错误信息
            errorMessage = e.getMessage();
        }

        // 4. 根据支付结果更新订单状态
        if (!paymentSuccess) {
            // 400
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST, "支付处理失败:" + (errorMessage != null ? errorMessage : "未知错误")
            );
        }

        // 5. 更新订单为已支付状态
        return orderRepository.findById(id).map(order -> {
                    order.setStatus(OrderStatus.PAID);
                    Order updatedOrder = orderRepository.save(order);
                    log.info("订单支付状态更新成功，订单ID: {}", id);
                    return orderMapper.toOrderDto(updatedOrder);
                })
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "订单不存在，支付后状态更新失败，订单ID:" + id
                ));
    }

    // 取消订单
    @Override
    @Transactional(readOnly = true)
    public OrderDto cancelOrder(Long id) {
        log.info("开始处理取消订单，订单ID: {}", id);

        // 1. 查询订单并校验存在性
        OrderDto orderDto = getOrderById(id).orElseThrow(() -> new BusinessException(
                HttpStatus.NOT_FOUND, "订单不存在，无法取消订单，订单ID:{}" + id
        ));

        // 2. 校验订单状态:仅待支付订单可取消
        if (orderDto.getStatus() != OrderStatus.PENDING_PAYMENT) {
            log.error("该订单不是未支付状态，无法取消，请确认！订单ID: {}, 当前订单状态: {}", id, orderDto.getStatus());
            // 400
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST, "仅待支付订单可执行取消操作，当前状态:" + orderDto.getStatus()
            );
        }

        // 3. 调用航司API取消预订（使用AirlineApiClient模拟）
        String errorMessage = null;
        try {
            airlineApiClient.issueTikcket(id);
            log.info("订单的取消请求已同步至航司，订单ID:{}", id);
        } catch (InterruptedException e) {
            log.error("取消订单请求超时，订单ID:{}", id, e);
            throw new BusinessException(
                    HttpStatus.REQUEST_TIMEOUT, "取消订单请求超时，请稍后重试"
            );
        } catch (RuntimeException e) {
            log.error("航司接口返回取消失败，订单ID:{}", id, e);
            errorMessage = e.getMessage();
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST, "取消订单失败:" + (errorMessage != null ? errorMessage : "未知错误")
            );
        }

        // 4. 更新订单状态为已取消
        return orderRepository.findById(id).map(order -> {
                    order.setStatus(OrderStatus.CANCELLED);
                    Order updatedOrder = orderRepository.save(order);
                    log.info("订单取消成功，订单ID: {}", id);
                    return orderMapper.toOrderDto(updatedOrder);
                })
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND, "订单不存在，取消失败，订单ID:" + id
                ));
    }

    // 重试 调用异步方法
    @Override
    @Transactional(readOnly = true)
    public void requestTicketIssuance(Long id) {
        log.info("开始处理订单出票重试，订单ID: {}", id);

        // 1. 查询订单并校验存在性
        OrderDto orderDto = getOrderById(id).orElseThrow(() -> new BusinessException(
                HttpStatus.NOT_FOUND, "订单不存在，无法出票，订单ID:{}" + id
        ));

        // 2. 校验订单状态:仅已支付订单可重试出票
        if (orderDto.getStatus() != OrderStatus.PAID) {
            log.error("该订单不是已支付状态，无法出票，请确认！订单ID: {}, 当前订单状态: {}", id, orderDto.getStatus());
            // 400
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST, "仅已支付订单可执行重试操作，当前状态:" + orderDto.getStatus()
            );
        }

        // 3. 调用航司API重试出票（使用AirlineApiClient模拟）
        try {
            String ticketNo = airlineApiClient.issueTikcket(id);
            // 4. 出票成功后更新订单状态为已出票
            orderRepository.findById(id).ifPresent(order -> {
                order.setStatus(OrderStatus.TICKETED);
                orderRepository.save(order);
                log.info("订单出票成功，票号: {}, 订单ID: {}", ticketNo, id);
                // 填充DTO中的航班信息
                orderDto.setFlightInfo(Map.of(
                        "ticketNo", ticketNo,
                        "issueTime", LocalDateTime.now().toString(),
                        "status", "SUCCESS"
                ));
            });
        } catch (InterruptedException e) {
            log.error("出票请求超时，订单ID:{}", id, e);
            throw new BusinessException(
                    HttpStatus.REQUEST_TIMEOUT, "出票请求超时，请稍后重试"
            );
        } catch (RuntimeException e) {
            log.error("出票失败，将在后续重试，订单ID:{}", id, e);
        }
    }

}
