package vn.iotstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopApplication {

    public static void main(String[] args) {
        System.setProperty("net.bytebuddy.experimental", "true");
        SpringApplication.run(ShopApplication.class, args);
    }
}
