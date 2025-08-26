package sk.ujcik.demo.quatz.triggers.quartztriggers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sk.ujcik.demo.quatz.triggers.quartztriggers.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootApplication
public class QuartztriggersApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartztriggersApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ProductService productService) {
        return args -> {
            Thread.sleep(1_000);
            int numberOfProducts = 100_000;
//            int numberOfProducts = 10_000;
            log.info("Generating {} products", numberOfProducts);
            productService.generateProducts(numberOfProducts, 5 * 60);
            log.info("Finished generating of products");
        };
    }


}
