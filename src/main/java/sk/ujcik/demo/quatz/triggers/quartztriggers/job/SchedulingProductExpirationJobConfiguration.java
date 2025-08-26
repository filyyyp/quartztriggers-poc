package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@Slf4j
@Configuration
public class SchedulingProductExpirationJobConfiguration {

   @Bean(name = "schedulingProductExpirationJobDetail")
    public JobDetail schedulingProductExpirationJobDetail() {
       return newJob(SchedulingProductExpirationJob.class)
               .storeDurably()
               .withIdentity("SchedulingProductExpirationJob")
               .build();
   }

    @Bean(name = "schedulingProductExpirationTrigger")
    public Trigger trigger(@Qualifier("schedulingProductExpirationJobDetail") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Qrtz_Trigger")
                .withDescription("Sample trigger")
                .startAt(futureDate(10, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(60))
                .build();
    }
}
