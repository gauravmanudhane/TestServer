package com.ethoca.customer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello-msg-api")
public class HelloMsgController {

	@GetMapping("/msg")
	public String getMsg() {
		return "Hello";
	}
}