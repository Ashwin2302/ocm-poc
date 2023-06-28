package com.product.app.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@RegisterForReflection
public class Product extends PanacheEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    public Long id;

    @Column(name = "product_name")
    public String productName;

    @Column(name = "description")
    public String description;

    @Column(name = "price")
    public Double price;

    @Column(name = "image_url")
    public String imageUrl;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "Product{" +
            "id=" +
            id +
            ", productName='" +
            productName +
            "'" +
            ", description='" +
            description +
            "'" +
            ", price=" +
            price +
            ", imageUrl='" +
            imageUrl +
            "'" +
            "}"
        );
    }

    public Product update() {
        return update(this);
    }

    public Product persistOrUpdate() {
        return persistOrUpdate(this);
    }

    public static Product update(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        var entity = Product.<Product>findById(product.id);
        if (entity != null) {
            entity.productName = product.productName;
            entity.description = product.description;
            entity.price = product.price;
            entity.imageUrl = product.imageUrl;
        }
        return entity;
    }

    public static Product persistOrUpdate(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product can't be null");
        }
        if (product.id == null) {
            persist(product);
            return product;
        } else {
            return update(product);
        }
    }
}
