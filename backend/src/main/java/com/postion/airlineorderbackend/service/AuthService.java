package com.postion.airlineorderbackend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.dto.AuthRequest;
import com.postion.airlineorderbackend.dto.AuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;

	private final JwtService jwtService;

	/**
	 * 
	 * 用户验证并生成token登录
	 * 
	 */
	public AuthResponse login(AuthRequest request) {
		// 用户验证
		Authentication authenticaion =  authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		// 调取验证成功用户的个人信息
		final UserDetails userDetails = (UserDetails) authenticaion.getPrincipal();
		
		// 生成token
		final String jwt = jwtService.generateToken(userDetails);

		// token打包发回给用户
		return AuthResponse.builder().token(jwt).build();

	}

}
