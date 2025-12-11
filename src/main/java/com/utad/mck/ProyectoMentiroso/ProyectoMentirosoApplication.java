package com.utad.mck.ProyectoMentiroso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProyectoMentirosoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoMentirosoApplication.class, args);
	}//
	
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "nombre", defaultValue="World") String name) {
	return String.format("Hello %s!", name);
	}

}