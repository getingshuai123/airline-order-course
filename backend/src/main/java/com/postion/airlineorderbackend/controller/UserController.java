package com.postion.airlineorderbackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postion.airlineorderbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/Users")
@RequiredArgsConstructor
public class UserController {
	
	// service层依赖
	private final UserService userService;
	
	

}
