package com.book_management.book;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookApplication.class, args);
	}

//    @Bean
//    CommandLineRunner logEnv() {
//        return args -> {
//            System.out.println("DB_URL=" + System.getenv("DB_URL"));
//            System.out.println("DB_USERNAME=" + System.getenv("DB_USERNAME"));
//            System.out.println("DB_PASSWORD=" + System.getenv("DB_PASSWORD"));
//        };
//    }

}
