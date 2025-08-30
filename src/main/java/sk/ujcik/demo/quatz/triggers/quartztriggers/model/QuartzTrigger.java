package sk.ujcik.demo.quatz.triggers.quartztriggers.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "qrtz_triggers")
public class QuartzTrigger {
    @Id
    private String triggerName;
    private String jobName;
    private String triggerGroup;
    private Long nextFireTime;
}
