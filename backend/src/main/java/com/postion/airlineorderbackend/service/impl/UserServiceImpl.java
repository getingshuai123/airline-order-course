package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.postion.airlineorderbackend.Mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postion.airlineorderbackend.dto.OrderDto.UserDto;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repository.UserRepository;
import com.postion.airlineorderbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	// repository层依赖
	private final UserRepository userRepository;

	// DaoToDTO
	private final OrderMapper  orderMapper;

	// 获取全部User信息
	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAllUsers() {
		List<User> allUser = userRepository.findAll();
		return allUser.stream()
				.map(orderMapper::toUserDto).collect(Collectors.toList());
	}

	// 获取当前User信息
	@Override
	@Transactional(readOnly = true)
	public Optional<UserDto> getUserbyUsername(String username) {
		return userRepository.findByUsername(username).map(orderMapper::toUserDto);
	}

}
