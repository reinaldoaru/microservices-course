package com.reinaldoaru.products_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reinaldoaru.products_service.model.dtos.ProductRequest;
import com.reinaldoaru.products_service.model.dtos.ProductResponse;
import com.reinaldoaru.products_service.model.entities.Product;
import com.reinaldoaru.products_service.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(ProductRequest productRequest) {
        var product = Product.builder()
            .sku(productRequest.getSku())
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(productRequest.getPrice())
            .status(productRequest.getStatus())
            .build();

        productRepository.save(product);
        log.info("Product added: {}", product);
    }

    public List<ProductResponse> getAllProducts() {
        var products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId())
            .sku(product.getSku())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .status(product.getStatus())
            .build();
    }
}
