package sk.ujcik.demo.quatz.triggers.quartztriggers.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class QuartzMetrics {

    private final Scheduler scheduler;

    public QuartzMetrics(Scheduler scheduler, MeterRegistry meterRegistry) {
        this.scheduler = scheduler;
        Gauge.builder("quartz.trigger.count", this::getMisfiredTriggers)
                .description("Count of Quartz trigger misfires")
                .register(meterRegistry);
    }

    public int getMisfiredTriggers() {
        log.info("Getting misfired triggers");
        try {
            List<String> result = new ArrayList<>();
            for (TriggerKey key : scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup())) {
                Trigger trigger = scheduler.getTrigger(key);
                if (trigger != null && trigger.getNextFireTime() != null && trigger.getNextFireTime().before(new Date())) {
                    result.add(trigger.toString());
                }
            }
            log.info("Found {} misfired triggers", result.size());
            return result.size();
        } catch (SchedulerException e) {
            log.error("Error getting misfired triggers", e);
            throw new IllegalStateException(e);
        }
    }
}
