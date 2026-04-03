package com.pulpapp.msproducts.service;

import com.pulpapp.msproducts.dto.ProductRequestDTO;
import com.pulpapp.msproducts.dto.ProductResponseDTO;
import com.pulpapp.msproducts.entity.Product;
import com.pulpapp.msproducts.exception.ResourceNotFoundException;
import com.pulpapp.msproducts.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public ProductResponseDTO findById(Long id) {
        return toResponseDto(findEntityById(id));
    }

    public ProductResponseDTO create(ProductRequestDTO dto) {
        validateUniqueName(dto.getName(), null);

        Product product = new Product();
        applyDtoToEntity(dto, product);

        return toResponseDto(productRepository.save(product));
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product product = findEntityById(id);
        validateUniqueName(dto.getName(), id);

        applyDtoToEntity(dto, product);

        return toResponseDto(productRepository.save(product));
    }

    public void delete(Long id) {
        Product product = findEntityById(id);
        productRepository.delete(product);
    }

    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void validateUniqueName(String name, Long excludedId) {
        boolean exists = excludedId == null
                ? productRepository.existsByNameIgnoreCase(name)
                : productRepository.existsByNameIgnoreCaseAndIdNot(name, excludedId);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A product with that name already exists");
        }
    }

    private void applyDtoToEntity(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription().trim());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setAvailable(dto.getAvailable());
        product.setImageUrl(dto.getImageUrl() == null ? null : dto.getImageUrl().trim());
    }

    private ProductResponseDTO toResponseDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setAvailable(product.getAvailable());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }
}
