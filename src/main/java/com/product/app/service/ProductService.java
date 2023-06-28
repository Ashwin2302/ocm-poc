package com.product.app.service;

import com.product.app.domain.Product;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.product.app.domain.Product}.
 */
public interface ProductService {
    /**
     * Save a product.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    Product persistOrUpdate(Product product);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all the products.
     * @return the list of entities.
     */
    public List<Product> findAll();

    /**
     * Get the "id" product.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Product> findOne(Long id);
}
