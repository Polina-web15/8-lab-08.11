package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.core.env.Environment;


@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		 //ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);

        // Получаем Environment из контекста
       // Environment env = ctx.getEnvironment();
        //String port = env.getProperty("server.port");

       // System.out.println("Server port from config: " + port);
		SpringApplication.run(DemoApplication.class, args);
	}

}
