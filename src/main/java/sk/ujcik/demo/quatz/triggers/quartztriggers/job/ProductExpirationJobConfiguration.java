package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class ProductExpirationJobConfiguration {
    public static final String PRODUCT_EXPIRATION_JOB_NAME = "ProductExpirationJob";

    @Bean(name = "productExpirationJobDetail")
    public JobDetail productExpirationJobDetail() {
        return newJob(ProductExpirationJob.class)
                .storeDurably()
                .withIdentity(PRODUCT_EXPIRATION_JOB_NAME)
                .withDescription("Job for executing product expiration, every expired product has its own trigger")
                .build();
    }
}
