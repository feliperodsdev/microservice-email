package com.microservices.sendemail;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SendemailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SendemailApplication.class, args);
	}

}
