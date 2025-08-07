package com.postion.airlineorderbackend.service;

import java.util.function.Function;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	// 生成token
	String generateToken(UserDetails userDetails);

	// 从Jwt信息中提取用户名
	String extractUsername(String token);

	// 确认Jwt和用户信息（userDetails）是否比配且有效
	boolean isTokenValid(String jwt, UserDetails userDetails);
	
	//解析token，返回想要的信息
	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}
