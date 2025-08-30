package sk.ujcik.demo.quatz.triggers.quartztriggers.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductExpirationSchedulerService {

    private final ProductService productService;
    private final Scheduler scheduler;
    private final JobDetail productExpirationJobDetail;
    @Value("${app.schedule-triggers-for-nearest-seconds}")
    private int scheduleTriggersForNearestSeconds;

    public ProductExpirationSchedulerService(
            ProductService productService,
            Scheduler scheduler,
            @Qualifier("productExpirationJITJobDetail")
            JobDetail productExpirationJobDetail
    ) {
        this.productService = productService;
        this.scheduler = scheduler;
        this.productExpirationJobDetail = productExpirationJobDetail;
    }

    @Transactional
    public void scheduleProductsForExpiration()  {
        int batchSize = 5000;
        int expirationInSeconds = scheduleTriggersForNearestSeconds;
        OffsetDateTime expirationDate = OffsetDateTime.now().plusSeconds(expirationInSeconds);
        PageRequest pageRequest = PageRequest.of(0, batchSize, Sort.by("id"));
        Page<Product> productsWithExpiration = productService.findProductsWithExpiration(expirationDate, pageRequest);
        log.info("==========================================================");
        log.info("Scheduling {} products for expiration", productsWithExpiration.getTotalElements());
        log.info("==========================================================");
        scheduleBatchOfProductsForExpiration(productsWithExpiration.getContent());

        while (productsWithExpiration.hasNext()) {
            Pageable pageable = productsWithExpiration.nextPageable();
            log.info("Getting next page for: {}", pageable);
            productsWithExpiration = productService.findProductsWithExpiration(expirationDate, pageable);
            scheduleBatchOfProductsForExpiration(productsWithExpiration.getContent());
        }
        log.info("==========================================================");
        log.info("Scheduled {} products for expiration", productsWithExpiration.getTotalElements());
        log.info("==========================================================");
    }

    private void scheduleBatchOfProductsForExpiration(List<Product> products) {
        Instant start = Instant.now();
        log.info("Scheduling batch of {} products for expiration, ids: {}", products.size(), products.stream().map(Product::getId).toList());
        products.forEach(this::scheduleProductExpiration);
        log.info("Scheduled batch of {} products for expiration in {} seconds", products.size(), Duration.between(start, Instant.now()).getSeconds());
    }

    private void scheduleProductExpiration(Product product) {
        Trigger expirationTrigger = TriggerBuilder.newTrigger()
                .forJob(productExpirationJobDetail)
                .withIdentity("ExpirationJobTrigger" + product.getId(), "DEFAULT")
                .withDescription("Trigger for product expiration")
                .startAt(Date.from(product.getExpirationDate().toInstant()))
                .usingJobData("productId", product.getId())
                .build();
        try {
            scheduler.scheduleJob(expirationTrigger);
        } catch (SchedulerException e) {
            log.error("Error scheduling product expiration trigger", e);
            throw new RuntimeException(e);
        }
    }

    //TODO dopracovat aby neplanoval produkty na deaktivaciu ktore uz boli naplanovane
}
