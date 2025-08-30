package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.JobBuilder.newJob;

@Configuration
public class ProductExpirationJITJobConfiguration {
    public static final String PRODUCT_EXPIRATION_JIT_JOB_NAME = "ProductExpirationJITJob";

    @Bean(name = "productExpirationJITJobDetail")
    public JobDetail productExpirationJobDetail() {
        return newJob(ProductExpirationJITJob.class)
                .storeDurably()
                .withIdentity(PRODUCT_EXPIRATION_JIT_JOB_NAME)
                .withDescription("Job for executing product expiration just in time, every expired product has its own trigger")
                .build();
    }
}
