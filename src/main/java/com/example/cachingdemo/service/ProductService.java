package com.example.cachingdemo.service;

import com.example.cachingdemo.entity.Product;
import com.example.cachingdemo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductRepository repository;

   // #result refers to the return value of the method (available in unless, not in key).
    //unless means: "don't cache if this condition is true"
   @Cacheable(value = "products", unless = "#result == null or #result.price < 100000")
   public Product getProduct(Long id) {
        log.info("the value is retrieve from the database");
        return repository.findById(id).orElse(null);
    }

    @Cacheable(
            value = "products",
            key = "#product.name",
            condition = "#product.price > 100000"
    )
    public Product createProduct(Product product) {
        return repository.save(product);
    }

    @CachePut(value = "products", key = "#product.id")
    public Product updateProduct(Product product) {
        return repository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}