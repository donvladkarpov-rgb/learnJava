package vk.limit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VkSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkSpringBootApplication.class, args);
    }

}
