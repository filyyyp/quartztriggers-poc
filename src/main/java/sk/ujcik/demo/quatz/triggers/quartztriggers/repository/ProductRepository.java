package sk.ujcik.demo.quatz.triggers.quartztriggers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.ProductState;

import java.time.OffsetDateTime;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Page<Product> findByExpirationDateLessThanAndState(OffsetDateTime expirationDate, ProductState state, Pageable pageable);

    Iterable<Product> findByExpirationDateLessThanAndState(OffsetDateTime expirationDate, ProductState state);

    long countByExpirationDateLessThanAndState(OffsetDateTime expirationDate, ProductState state);

}
