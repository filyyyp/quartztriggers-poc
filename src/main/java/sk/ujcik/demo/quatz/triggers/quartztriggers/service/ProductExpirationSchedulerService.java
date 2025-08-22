package sk.ujcik.demo.quatz.triggers.quartztriggers.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductExpirationSchedulerService {

    private final ProductService productService;
    private final Scheduler scheduler;
    private final JobDetail productExpirationJobDetail;

    public ProductExpirationSchedulerService(
            ProductService productService,
            Scheduler scheduler,
            @Qualifier("productExpirationJobDetail")
            JobDetail productExpirationJobDetail
    ) {
        this.productService = productService;
        this.scheduler = scheduler;
        this.productExpirationJobDetail = productExpirationJobDetail;
    }

    @Transactional
    public void scheduleProductsForExpiration()  {
        List<Product> productsWithExpiration = productService.findProductsWithExpiration(60);
        log.info("Found {} products with expiration to schedule", productsWithExpiration.size());

        productsWithExpiration.forEach(product -> {
            Trigger expirationTrigger = TriggerBuilder.newTrigger()
                    .forJob(productExpirationJobDetail)
                    .withIdentity("ExpirationJobTrigger" + product.getId())
                    .withDescription("Trigger for product expiration")
                    .startAt(Date.from(product.getExpirationDate().toInstant()))
                    .usingJobData("productId", product.getId())
                    .build();
            try {
                scheduler.scheduleJob(expirationTrigger);
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }

        });

    }
}
