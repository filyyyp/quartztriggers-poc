package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
public class SchedulingProductExpirationJobConfiguration {
    public static final String SCHEDULING_PRODUCT_EXPIRATION_JOB_NAME = "SchedulingProductExpirationJob";

   @Bean(name = "schedulingProductExpirationJobDetail")
    public JobDetail schedulingProductExpirationJobDetail() {
       return newJob(SchedulingProductExpirationJob.class)
               .storeDurably()
               .withIdentity(SCHEDULING_PRODUCT_EXPIRATION_JOB_NAME)
               .withDescription("Job for scheduling product expiration triggers, check for product expiration and if it is expired schedule")
               .build();
   }

    @Bean(name = "schedulingProductExpirationTrigger")
    public Trigger trigger(
            @Qualifier("schedulingProductExpirationJobDetail") JobDetail job,
            @Value("${app.scheduling-product-expiration-interval-seconds}") int schedulingProductExpirationIntervalSeconds
            ) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("SchedulingProductExpirationTrigger")
                .withDescription("Trigger for scheduling product expiration, triggers every minute forever")
                .startAt(futureDate(10, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(schedulingProductExpirationIntervalSeconds))
                .build();
    }
}
