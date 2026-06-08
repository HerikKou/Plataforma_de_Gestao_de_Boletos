package com.boletos.boleto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@SpringBootApplication
@EnableCaching
public class BoletoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoletoServiceApplication.class, args);
    }
}
