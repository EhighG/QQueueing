package com.example.tes24;

import com.example.tes24.qqueueing.context.loader.Q2ClassLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Tes24Application {

	public static void main(String[] args) {
		new Q2ClassLoader().getLoadedClasses();
		SpringApplication.run(Tes24Application.class, args);
	}

}
