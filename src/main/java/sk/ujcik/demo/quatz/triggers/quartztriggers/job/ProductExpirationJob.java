package sk.ujcik.demo.quatz.triggers.quartztriggers.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.ujcik.demo.quatz.triggers.quartztriggers.service.ProductService;

import java.util.List;

@Slf4j
@Component
public class ProductExpirationJob implements Job {

    @Autowired
    private ProductService productService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long productId = (Long) context.getMergedJobDataMap().get("productId");
        log.debug("Deactivating product with id: {}", productId);
        productService.deactivateProduct(productId);

    }
}
