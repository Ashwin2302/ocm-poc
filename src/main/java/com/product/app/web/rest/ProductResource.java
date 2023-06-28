package com.product.app.web.rest;

import static javax.ws.rs.core.UriBuilder.fromPath;

import com.product.app.domain.Product;
import com.product.app.security.AuthoritiesConstants;
import com.product.app.service.ProductService;
import com.product.app.web.rest.errors.BadRequestAlertException;
import com.product.app.web.util.HeaderUtil;
import com.product.app.web.util.ResponseUtil;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing {@link com.product.app.domain.Product}.
 */
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Inject
    ProductService productService;

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param product the product to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new product, or with status {@code 400 (Bad Request)} if the product has already an ID.
     */
    @POST
    @RolesAllowed(AuthoritiesConstants.USER)
    public Response createProduct(Product product, @Context UriInfo uriInfo) {
        log.debug("REST request to save Product : {}", product);
        if (product.id != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }
        var result = productService.persistOrUpdate(product);
        var response = Response.created(fromPath(uriInfo.getPath()).path(result.id.toString()).build()).entity(result);
        HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code PUT  /products} : Updates an existing product.
     *
     * @param product the product to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed(AuthoritiesConstants.USER)
    public Response updateProduct(Product product, @PathParam("id") Long id) {
        log.debug("REST request to update Product : {}", product);
        if (product.id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = productService.persistOrUpdate(product);
        var response = Response.ok().entity(result);
        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, product.id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the product to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed(AuthoritiesConstants.USER)
    public Response deleteProduct(@PathParam("id") Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        var response = Response.noContent();
        HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()).forEach(response::header);
        return response.build();
    }

    /**
     * {@code GET  /products} : get all the products.
     *     * @return the {@link Response} with status {@code 200 (OK)} and the list of products in body.
     */
    @GET
    @RolesAllowed(AuthoritiesConstants.USER)
    public List<Product> getAllProducts() {
        log.debug("REST request to get all Products");
        return productService.findAll();
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the product to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the product, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    @RolesAllowed(AuthoritiesConstants.USER)
    public Response getProduct(@PathParam("id") Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<Product> product = productService.findOne(id);
        return ResponseUtil.wrapOrNotFound(product);
    }
}
