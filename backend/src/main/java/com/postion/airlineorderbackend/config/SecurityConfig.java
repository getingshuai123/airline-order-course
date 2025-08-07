package com.postion.airlineorderbackend.config;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Springboot的配置文件
@Configuration
// SpringSecurity相关的配置，SpringSecurity相关bean的处理
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthFilter jwtAuthFilter;

	// 当你试图登录时，Spring Security 会调用这个方法，传入你输入的用户名（username），并期望得到一个实现了 UserDetails接口的对象。
	// 这个对象包含了用户的核心信息，如密码、权限（角色）以及账户是否被锁定等状态。
	// Spring Security中常用的处理用户的service
	private final UserDetailsService userDetailsService;
	
	private static final String[] PUBLIC_URLS = {
			// 根路径和前端静态资源
			"/", 
			"/index.html", 
			"*.js", 
			"/*.css", 
			"/*.ico", 
			"/*.png", 
			"/*.assets/**",
			
			// 所有认证相关api
			"/api/auth/**",
			
			// 所有订单相关api
//			"/api/orders/**",
			
			// swagger文档
			"/swagger-ui.html", 
			"/swagger-ui/**", 
			"/v3/api-docs/**", 
			"/webjars/**"
	};
	

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 显示应用CORS配置，当你使用 Spring Security 时，你必须在 SecurityFilterChain 中显式地调用 .cors()
				// 来应用你的 corsConfigurationSource Bean。
				// 如果不调用，你的 CORS 配置将对 Spring Security 的过滤器链无效。
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				
				// lambda DSL配置CSRF(跨站请求伪造)
				// 有JWT的话这里一般会关掉
				.csrf(AbstractHttpConfigurer::disable)
				
				// 配置授权规则
				.authorizeHttpRequests(authz -> authz
						// 明确放行所有公共路径
//						.requestMatchers(PUBLIC_URLS).permitAll() // Spring Security 6.x以上可用
						.antMatchers(PUBLIC_URLS).permitAll()
						// 其他任何请求都需要身份验证
						.anyRequest().authenticated())
				
				// 配合会话管理为无状态，因为用JWT
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				
				// 关联自定义的AuthenticationProvider
				.authenticationProvider(authenticationProvider())
				
				// 在UsernamePasswordAuthenticationFilter之前添加JWT过滤器
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * 
	 * 配置CORS（跨域资源共享）
	 * 允许来自不同源的请求访问后端API
	 * 
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		// (重要修改)允许任何来源，或服务器的公网IP。用"*"在开发和测试中最方便
//    	corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
//    	corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
		corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
		
		// 允许的HTTP方法
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		
		// 允许的请求头
		corsConfiguration.setAllowedHeaders(
				Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Cache-Control"));
		
		// 是否允许发送Cookie
		corsConfiguration.setAllowCredentials(true);
		
		// 预检请求的有效期，单位秒
		corsConfiguration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		// 对所有URL应用这个配置
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	/**
	 * 
	 * 认证提供
	 * 定义一个 AuthenticationProvider Bean，用于处理基于 DAO 的身份验证。
	 * Spring Security 会自动检测到这个 Bean 并使用它
	 * 
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	/**
	 * 
	 * 认证管理
	 * 从 AuthenticationConfiguration 中获取 AuthenticationManager Bean
	 * 
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/**
	 * 
	 * 定义密码编码器 Bean，使用 BCrypt 算法
	 * 
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
