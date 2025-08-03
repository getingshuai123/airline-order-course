package com.postion.airlineorderbackend.service;

import java.util.List;
import java.util.Optional;

import com.postion.airlineorderbackend.dto.OrderDto.UserDto;

public interface UserService {
	// 查所有用户信息
	List<UserDto> getAllUsers();
	
	// 查个人用户信息
	Optional<UserDto> getUserById(Long id);

}
