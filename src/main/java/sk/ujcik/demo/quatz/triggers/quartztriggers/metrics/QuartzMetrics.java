package sk.ujcik.demo.quatz.triggers.quartztriggers.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.stereotype.Component;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.QuartzTrigger;
import sk.ujcik.demo.quatz.triggers.quartztriggers.repository.QuartzTriggersRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sk.ujcik.demo.quatz.triggers.quartztriggers.job.ProductExpirationJITJobConfiguration.PRODUCT_EXPIRATION_JIT_JOB_NAME;

@Slf4j
@Component
public class QuartzMetrics {

    private final QuartzTriggersRepository quartzTriggersRepository;
    private final Scheduler scheduler;
    @Value("${spring.quartz.job-store-type}")
    private JobStoreType jobStoreType;

    public QuartzMetrics(
            Scheduler scheduler,
            MeterRegistry meterRegistry,
            QuartzTriggersRepository quartzTriggersRepository

    ) {
        this.scheduler = scheduler;
        this.quartzTriggersRepository = quartzTriggersRepository;
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
        for (CustomTrigger trigger : getTriggers()) {
            if (trigger != null && trigger.getNextFireTime() != null && trigger.getNextFireTime().before(new Date())) {
                result.add(trigger.toString());
            }
        }
        return result.size();
    }

    public int getTriggersCount() {
        return getTriggers().size();
    }

    public Duration calculateAverageMisfireTime() {
        List<CustomTrigger> triggers = getTriggers();
        Date now = new Date();
        Instant instant = now.toInstant();
        List<Duration> misfireDurations = triggers.stream()
                .map(CustomTrigger::getNextFireTime)
                .filter(nextFireTime -> nextFireTime != null && nextFireTime.before(now))
                .map(misfiredNexFireTime -> Duration.between(misfiredNexFireTime.toInstant(), instant))
                .toList();
        log.info("Misfire durations: {}", misfireDurations.size());
        if (misfireDurations.isEmpty()) {
            return Duration.ZERO;
        }
        Duration duration = misfireDurations.stream().reduce(Duration::plus).orElse(Duration.ZERO).dividedBy(misfireDurations.size());
        log.info("Average misfire time: {}ms", duration.toMillis());
        return duration;
    }

    private List<CustomTrigger> getTriggers() {
        if (jobStoreType == JobStoreType.JDBC) {
            return getProductExpirationTriggersWithCustomRepository().stream().map(CustomTrigger::fromQuartzTrigger).toList();
        } else {
            return getProductExpirationTriggers().stream().map(CustomTrigger::fromTrigger).toList();
        }
    }

    private List<? extends Trigger> getProductExpirationTriggers() {
        try {
            Instant startTime = Instant.now();
            List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(JobKey.jobKey(PRODUCT_EXPIRATION_JIT_JOB_NAME));
            log.info("Getting triggers took {} ms, count {}", Duration.between(startTime, Instant.now()).toMillis(), triggersOfJob.size());
            return triggersOfJob;
        } catch (SchedulerException e) {
            log.error("Error getting triggers", e);
            throw new IllegalStateException(e);
        }
    }

    private List<QuartzTrigger> getProductExpirationTriggersWithCustomRepository() {
        Instant startTime = Instant.now();
        List<QuartzTrigger> expiredTriggers = quartzTriggersRepository.findByJobName(PRODUCT_EXPIRATION_JIT_JOB_NAME);
        log.info("Custom repo Getting triggers took {} ms, count {}", Duration.between(startTime, Instant.now()).toMillis(), expiredTriggers.size());
        return expiredTriggers;
    }

    @Getter
    @Setter
    private static class CustomTrigger {
        private Date nextFireTime;

        public static CustomTrigger fromQuartzTrigger(QuartzTrigger quartzTrigger) {
            CustomTrigger customTrigger = new CustomTrigger();
            customTrigger.nextFireTime = quartzTrigger.getNextFireTime() > 0 ? new Date(quartzTrigger.getNextFireTime()) : null;

            return customTrigger;
        }

        public static CustomTrigger fromTrigger(Trigger trigger) {
            CustomTrigger customTrigger = new CustomTrigger();
            customTrigger.nextFireTime = trigger.getNextFireTime();
            return customTrigger;
        }
    }
}
