package sk.ujcik.demo.quatz.triggers.quartztriggers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.ProductState;
import sk.ujcik.demo.quatz.triggers.quartztriggers.repository.ProductRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private static final Random random = new Random();

    public void generateProducts(int numberOfProducts, int maxExpirationTimeInSeconds) {

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < numberOfProducts; i++) {
            Product product = new Product();
            product.setState(ProductState.ACTIVE);
            product.setName("Product_" + i);
            product.setExpirationDate(java.time.OffsetDateTime.now().plusSeconds(random.nextInt(maxExpirationTimeInSeconds)));
            products.add(product);
        }
        productRepository.saveAll(products);
    }

    public List<Product> findProductsWithExpiration(int expirationInSeconds) {
        return productRepository.findByExpirationDateLessThanAndState(OffsetDateTime.now().plusSeconds(expirationInSeconds), ProductState.ACTIVE);
    }

    public void deactivateProduct(long productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.setState(ProductState.INACTIVE);
            productRepository.save(product);
        });
    }
}
