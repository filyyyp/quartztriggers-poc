package sk.ujcik.demo.quatz.triggers.quartztriggers.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sk.ujcik.demo.quatz.triggers.quartztriggers.job.ProductExpirationJobConfiguration.PRODUCT_EXPIRATION_JOB_NAME;

@Slf4j
@Component
public class QuartzMetrics {

    private final Scheduler scheduler;

    public QuartzMetrics(Scheduler scheduler, MeterRegistry meterRegistry) {
        this.scheduler = scheduler;
        Gauge.builder("quartz.trigger.productexpiration.misfire.count", this::getMisfiredTriggersCount)
                .description("Count of Quartz misfired triggers for product expiration job")
                .register(meterRegistry);
        Gauge.builder("quartz.trigger.productexpiration.count", this::getTriggersCount)
                .description("Count of Quartz triggers for product expiration job")
                .register(meterRegistry);
        Gauge.builder("quartz.trigger.productexpiration.misfire.average.time.miliseconds", () -> calculateAverageMisfireTime().toMillis())
                .description("Average time of Quartz misfired triggers for product expiration job")
                .register(meterRegistry);
    }

    public int getMisfiredTriggersCount() {
        List<String> result = new ArrayList<>();
        for (Trigger trigger : getProductExpirationTriggers()) {
            if (trigger != null && trigger.getNextFireTime() != null && trigger.getNextFireTime().before(new Date())) {
                result.add(trigger.toString());
            }
        }
        return result.size();
    }

    public int getTriggersCount() {
        return getProductExpirationTriggers().size();
    }

    public Duration calculateAverageMisfireTime() {
        List<? extends Trigger> triggers = getProductExpirationTriggers();
        Date now = new Date();
        Instant instant = now.toInstant();
        List<Duration> misfireDurations = triggers.stream()
                .map(Trigger::getNextFireTime)
                .filter(nextFireTime -> nextFireTime != null && nextFireTime.before(now))
                .map(misfiredNexFireTime -> Duration.between(misfiredNexFireTime.toInstant(), instant))
                .toList();
        log.info("Misfire durations: {}", misfireDurations.size());
        if (misfireDurations.isEmpty()) {
            return Duration.ZERO;
        }
        return misfireDurations.stream().reduce(Duration::plus).orElse(Duration.ZERO).dividedBy(misfireDurations.size());


    }

    private List<? extends Trigger> getProductExpirationTriggers() {
        try {
            return scheduler.getTriggersOfJob(JobKey.jobKey(PRODUCT_EXPIRATION_JOB_NAME));
        } catch (SchedulerException e) {
            log.error("Error getting triggers", e);
            throw new IllegalStateException(e);
        }
    }

//    TODO premenuj a odfiltruj len skupinu pre tie ktore nas zaujimaju cize ti expiracne
}
