package sk.ujcik.demo.quatz.triggers.quartztriggers.repository;

import org.springframework.data.repository.CrudRepository;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.ProductState;

import java.time.OffsetDateTime;
import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByExpirationDateLessThanAndState(OffsetDateTime expirationDate, ProductState state);
}
