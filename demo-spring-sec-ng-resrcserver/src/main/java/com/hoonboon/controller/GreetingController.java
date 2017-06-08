package com.hoonboon.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoonboon.dto.MessageDto;

@RestController
public class GreetingController {

	@GetMapping("/resource")
	@CrossOrigin(
			origins="*", maxAge=3600,
			allowedHeaders={ "x-myauth-token", "x-requested-with" })
	public MessageDto home() {
		return new MessageDto("Hello World!!!");
	}

}
