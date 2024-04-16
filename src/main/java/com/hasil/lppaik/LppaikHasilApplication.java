package com.hasil.lppaik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LppaikHasilApplication {

	public static void main(String[] args) {
		SpringApplication.run(LppaikHasilApplication.class, args);
	}

}
