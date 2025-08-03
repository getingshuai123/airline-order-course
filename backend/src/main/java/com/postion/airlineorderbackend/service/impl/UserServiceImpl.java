package com.postion.airlineorderbackend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	// 获取全部User信息
	@Override
	@Transactional(readOnly = true)
	public List<UserDto> getAllUsers() {
		List<User> allUser = userRepository.findAll();
		return allUser.stream().map(this::convertUserToUserDto).collect(Collectors.toList());
	}

	// 获取当前User信息
	@Override
	@Transactional(readOnly = true)
	public Optional<UserDto> getUserById(Long id) {
		return userRepository.findById(id).map(this::convertUserToUserDto);
	}

	/**
	 * 将User实体转换为UserDto
	 * @param user
	 * @return userDto
	 */
	private UserDto convertUserToUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setUsername(user.getUsername());
		return userDto;
	}
}
