package com.postion.airlineorderbackend.service.impl;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		// 1. 从数据库或其他地方根据用户名查找用户
		return userRepository.findByUsername(username)
				
				// 2. 将你的用户实体转换成 Spring Security 需要的 UserDetails 对象
				// Spring Security 提供了 User.builder() 来方便地创建 UserDetails 对象
				.map(user -> org.springframework.security.core.userdetails.User.builder()
						.username(user.getUsername())
						.password(user.getPassword())
						.roles(user.getRole())
						.build())
				
				.orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
	}
}
