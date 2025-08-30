package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Configuration
public class ProductExpirationScheduledJobConfiguration {
    public static final String PRODUCT_EXPIRATION_SCHEDULED_JOB_NAME = "ProductExpirationScheduledJob";

    @Bean(name = "productExpirationScheduledJobDetail")
    public JobDetail productExpirationScheduledJobDetail() {
        return newJob(ProductExpirationScheduledJob.class)
                .storeDurably()
                .withIdentity(PRODUCT_EXPIRATION_SCHEDULED_JOB_NAME)
                .withDescription("Job for executing product expiration scheduled, deactivate expired products")
                .build();
    }

    @Bean(name = "productExpirationScheduledTrigger")
    public Trigger trigger(
            @Qualifier("productExpirationScheduledJobDetail") JobDetail job,
            @Value("${app.product-expiration-scheduled-interval-seconds}") int scheduledIntervalInSeconds,
            @Value("${app.scheduled-expiration-enabled}") boolean triggerEnabled
    ) {
        if (!triggerEnabled) {
            return null;
        }
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("ProductExpirationScheduledTrigger")
                .withDescription("Trigger for deactivate products, triggers every minute forever")
                .startAt(futureDate(10, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(scheduledIntervalInSeconds))
                .build();
    }
}
