package sk.ujcik.demo.quatz.triggers.quartztriggers.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class ProductExpirationService {

    private final ProductService productService;

    public ProductExpirationService(
            ProductService productService
    ) {
        this.productService = productService;
    }

    @Transactional
    public void deactivateExpiredProducts()  {
        int batchSize = 5000;
        OffsetDateTime now = OffsetDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, batchSize, Sort.by("id"));
        Page<Product> productsWithExpiration = productService.findProductsWithExpiration(now, pageRequest);
        log.info("==========================================================");
        log.info("Found {} products for expiration", productsWithExpiration.getTotalElements());
        log.info("==========================================================");
        deactivateBatchOfProducts(productsWithExpiration.getContent());
        while (productsWithExpiration.hasNext()) {
            Pageable pageable = productsWithExpiration.nextPageable();
            log.info("Getting next page for: {}", pageable);
            productsWithExpiration = productService.findProductsWithExpiration(now, pageable);
            deactivateBatchOfProducts(productsWithExpiration.getContent());
        }
        log.info("==========================================================");
        log.info("Deactivated {} products after expiration", productsWithExpiration.getTotalElements());
        log.info("==========================================================");
    }

    private void deactivateBatchOfProducts(List<Product> products) {
        products.forEach(product -> productService.deactivateProduct(product.getId()));
    }


}
