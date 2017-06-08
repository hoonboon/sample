package com.hoonboon.controller;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

	@GetMapping("/token")
	public Map<String, String> token(HttpSession session) {
		return Collections.singletonMap("token", session.getId());
	}
	
}
