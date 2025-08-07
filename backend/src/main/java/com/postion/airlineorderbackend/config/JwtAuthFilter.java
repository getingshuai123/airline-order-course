package com.postion.airlineorderbackend.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.postion.airlineorderbackend.service.JwtService;

import lombok.RequiredArgsConstructor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
/**
 * 这里只负责认证和授权登记，真正拦截有Spring Security后续机制根据这个过滤器的等级结果来执行
 */
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain) throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String username;
		
		// 第一层过滤（快速过滤游客或要注册的新用户，到下一个过滤器）
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		// 将header无关的信息去掉（"Bearer "）
		jwt = authHeader.substring(7);
		
		// 从jwt信息中提取用户名
		username = jwtService.extractUsername(jwt);

		// 将读出来的username进行校验
		// 有用户名且今天的"已认证访客列表"（SecurityContextHolder）没有有这个用户的记录，防止同一个请求的重复认证
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			
			// 查该用户
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			
			// 确认jwt和用户信息（userDetails）是否比配且有效
			if (jwtService.isTokenValid(jwt, userDetails)) {
				
				// 认证成功，创建一个凭证（包含用户信息，密码（不做，这里不安全），放入用户权限）
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities()
			    );
				
				// 在凭证上记录一些额外信息
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				// 认证成功之后，将用户记录到已认证访客列表"（SecurityContextHolder）
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		// 用户放行
		filterChain.doFilter(request, response);

	}
}
