package sk.ujcik.demo.quatz.triggers.quartztriggers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.ujcik.demo.quatz.triggers.quartztriggers.model.Product;
import sk.ujcik.demo.quatz.triggers.quartztriggers.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    public void generateProducts(int numberOfProducts) {

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < numberOfProducts; i++) {
            Product product = new Product();
            product.setName("Product_" + i);
            product.setExpirationDate(java.time.OffsetDateTime.now().plusMinutes(1));
            products.add(product);
        }
        productRepository.saveAll(products);
    }
}
