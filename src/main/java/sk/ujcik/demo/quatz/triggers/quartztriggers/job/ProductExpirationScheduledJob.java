package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.ujcik.demo.quatz.triggers.quartztriggers.service.ProductExpirationService;

@Slf4j
@Component
public class ProductExpirationScheduledJob implements Job {

    @Autowired
    private ProductExpirationService productExpirationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        productExpirationService.deactivateExpiredProducts();
    }
}
