package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class ProductExpirationJobConfiguration {

    @Bean(name = "productExpirationJobDetail")
    public JobDetail productExpirationJobDetail() {
        return newJob(ProductExpirationJob.class)
                .storeDurably()
                .withIdentity("ProductExpirationJob")
                .build();
    }
}
