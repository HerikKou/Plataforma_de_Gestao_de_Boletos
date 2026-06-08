package com.boletos.vencimento;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class VencimentoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VencimentoServiceApplication.class, args);
    }
}
