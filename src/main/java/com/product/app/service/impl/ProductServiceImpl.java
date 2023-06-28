package com.product.app.service.impl;

import com.product.app.domain.Product;
import com.product.app.service.ProductService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
    public Product persistOrUpdate(Product product) {
        log.debug("Request to save Product : {}", product);
        return Product.persistOrUpdate(product);
    }

    /**
     * Delete the Product by id.
     *
     * @param id the id of the entity.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        Product
            .findByIdOptional(id)
            .ifPresent(product -> {
                product.delete();
            });
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    public Optional<Product> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return Product.findByIdOptional(id);
    }

    /**
     * Get all the products.
     * @return the list of entities.
     */
    @Override
    public List<Product> findAll() {
        log.debug("Request to get all Products");
        return Product.findAll().list();
    }
}
