package vk.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class VkSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkSpringBootApplication.class, args);
    }

}
