package sk.ujcik.demo.quatz.triggers.quartztriggers.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.QuartzTrigger;

import java.util.List;

public interface QuartzTriggersRepository extends CrudRepository<QuartzTrigger, String> {
    List<QuartzTrigger> findByJobName(String jobName);

    @Query(value = """
    SELECT q.trigger_name, q.trigger_group, q.next_fire_time
    from  qrtz_triggers q
    WHERE
    to_timestamp(next_fire_time / 1000) < now()
    ORDER BY q.next_fire_time ASC
    """,nativeQuery = true)
    List<QuartzTrigger> findExpiredTriggers();

}
