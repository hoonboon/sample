package com.hoonboon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoConfigClientApplication.class, args);
	}
}


@RefreshScope
@RestController
class MessageRestController {

    @Value("${message:Hello default}")
    private String message;

    @RequestMapping("/message")
    String getMessage() {
        return this.message;
    }
}

@RefreshScope
@RestController
class UserRestController {
	
	@Value("${user.role: Undefined}")
	private String role;
	
	@Value("${user.password: Undefined}")
	private String password;
	
	@RequestMapping(
			value = "/whoami/{username}",
			method = RequestMethod.GET,
			produces = MediaType.TEXT_PLAIN_VALUE)
	String whoami(@PathVariable("username") String username) {
		return 
			String.format(
					"Hello %s!!! You're a(n) %s, " 
					+ "but only if your password is '%s'!\n", 
					username, role, password);
	}
	
}

