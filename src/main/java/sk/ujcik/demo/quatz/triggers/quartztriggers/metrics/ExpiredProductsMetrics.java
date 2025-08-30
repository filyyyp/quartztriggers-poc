package sk.ujcik.demo.quatz.triggers.quartztriggers.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.ProductState;
import sk.ujcik.demo.quatz.triggers.quartztriggers.repository.ProductRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class ExpiredProductsMetrics {

    private final ProductRepository productRepository;

    public ExpiredProductsMetrics(
            MeterRegistry meterRegistry,
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
        Gauge.builder("app.product.afterexpiration.count", this::getCountOfProductsAfterExpiration)
                .description("Count of products after expiration")
                .register(meterRegistry);
        Gauge.builder("app.product.afterexpiration.average.time.miliseconds", () -> calculateAverageTimeAfterExpiration().toMillis())
                .description("Average time of Quartz misfired triggers for product expiration job")
                .register(meterRegistry);
    }

    private Duration calculateAverageTimeAfterExpiration() {
        OffsetDateTime now = OffsetDateTime.now();
        Iterable<Product> byExpirationDateLessThanAndState = productRepository.findByExpirationDateLessThanAndState(now, ProductState.ACTIVE);
        Stream<Product> expiredProducts = StreamSupport.stream(byExpirationDateLessThanAndState.spliterator(), false);
        Duration duration = expiredProducts.map(product -> Duration.between(product.getExpirationDate(), now))
                .reduce(Duration::plus).orElse(Duration.ZERO);
        if (duration.isZero()) {
            return Duration.ZERO;
        } else {

            return duration.dividedBy(StreamSupport.stream(byExpirationDateLessThanAndState.spliterator(), false).count());
        }
    }

    private long getCountOfProductsAfterExpiration() {
        return productRepository.countByExpirationDateLessThanAndState(OffsetDateTime.now(), ProductState.ACTIVE);
    }

}
