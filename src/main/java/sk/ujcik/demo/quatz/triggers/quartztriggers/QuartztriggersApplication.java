package sk.ujcik.demo.quatz.triggers.quartztriggers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sk.ujcik.demo.quatz.triggers.quartztriggers.service.ProductService;

@Slf4j
@SpringBootApplication
public class QuartztriggersApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuartztriggersApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            ProductService productService,
            @Value("${app.max-expiration-date-for-generated-products-seconds}")
            int maxExpirationDateForGeneratedProductsSeconds,
            @Value("${app.number-of-generated-products}")
            int numberOfGeneratedProducts
    ) {
        return args -> {
            Thread.sleep(1_000);
            log.info("Generating {} products", numberOfGeneratedProducts);
            productService.generateProducts(numberOfGeneratedProducts, maxExpirationDateForGeneratedProductsSeconds);
            log.info("Finished generating of products");
        };
    }


}
