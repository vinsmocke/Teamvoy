package com.teamvoy.task.service.impl;

import com.teamvoy.task.exception.EntityNotFoundException;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.Product;
import com.teamvoy.task.repository.ProductRepository;
import com.teamvoy.task.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        if (product != null) {
            if (productRepository.existsByName(product.getName())) {
                Product exitingProduct = productRepository.findByNameIgnoreCase(product.getName()).orElseThrow(
                        () -> new EntityNotFoundException("Product with name " + product.getName() + " not found!"));

                if (exitingProduct.getName().trim().equalsIgnoreCase(product.getName().trim())) {
                    exitingProduct.setAmount(exitingProduct.getAmount() + product.getAmount());
                    return productRepository.save(exitingProduct);
                }
            }
            return productRepository.save(product);
        } else
            throw new NullEntityReferenceException("Product cannot be 'null'");
    }

    @Override
    public Product readById(long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found!"));
    }

    @Override
    public Product update(Product product) {
        if (product != null) {
            readById(product.getId());
            return productRepository.save(product);
        }
        throw new NullEntityReferenceException("Product cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        productRepository.delete(readById(id));
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
