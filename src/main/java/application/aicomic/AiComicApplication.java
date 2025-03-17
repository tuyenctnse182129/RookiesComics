package application.aicomic;

import application.aicomic.config.MomoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(MomoConfig.class)
public class AiComicApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiComicApplication.class, args);
    }

}
