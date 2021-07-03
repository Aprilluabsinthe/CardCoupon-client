package com.cardcoupon.passbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PassbookClientSideApplication {

    public static void main(String[] args) {
        SpringApplication.run(PassbookClientSideApplication.class, args);
    }

}
