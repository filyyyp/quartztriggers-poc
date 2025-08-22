package sk.ujcik.demo.quatz.triggers.quartztriggers.repository;

import org.springframework.data.repository.CrudRepository;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
