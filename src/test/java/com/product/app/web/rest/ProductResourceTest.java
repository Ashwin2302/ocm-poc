//package com.product.app.web.rest;
//
//import static io.restassured.RestAssured.given;
//import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
//import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
//import static javax.ws.rs.core.Response.Status.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.*;
//
//import com.product.app.TestUtil;
//import com.product.app.domain.Product;
//import io.quarkus.liquibase.LiquibaseFactory;
//import io.quarkus.test.junit.QuarkusTest;
//import io.restassured.RestAssured;
//import io.restassured.common.mapper.TypeRef;
//import java.util.List;
//import javax.inject.Inject;
//import liquibase.Liquibase;
//import org.junit.jupiter.api.*;
//
//@QuarkusTest
//public class ProductResourceTest {
//
//    private static final TypeRef<Product> ENTITY_TYPE = new TypeRef<>() {};
//
//    private static final TypeRef<List<Product>> LIST_OF_ENTITY_TYPE = new TypeRef<>() {};
//
//    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
//    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";
//
//    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
//    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";
//
//    private static final Double DEFAULT_PRICE = 1D;
//    private static final Double UPDATED_PRICE = 2D;
//
//    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
//    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";
//
//    String adminToken;
//
//    Product product;
//
//    @Inject
//    LiquibaseFactory liquibaseFactory;
//
//    @BeforeAll
//    static void jsonMapper() {
//        RestAssured.config =
//            RestAssured.config().objectMapperConfig(objectMapperConfig().defaultObjectMapper(TestUtil.jsonbObjectMapper()));
//    }
//
//    @BeforeEach
//    public void authenticateAdmin() {
//        this.adminToken = TestUtil.getAdminToken();
//    }
//
//    @BeforeEach
//    public void databaseFixture() {
//        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
//            liquibase.dropAll();
//            liquibase.validate();
//            liquibase.update(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Create an entity for this test.
//     * <p>
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Product createEntity() {
//        var product = new Product();
//        product.productName = DEFAULT_PRODUCT_NAME;
//        product.description = DEFAULT_DESCRIPTION;
//        product.price = DEFAULT_PRICE;
//        product.imageUrl = DEFAULT_IMAGE_URL;
//        return product;
//    }
//
//    @BeforeEach
//    public void initTest() {
//        product = createEntity();
//    }
//
//    @Test
//    public void createProduct() {
//        var databaseSizeBeforeCreate = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE)
//            .size();
//
//        // Create the Product
//        product =
//            given()
//                .auth()
//                .preemptive()
//                .oauth2(adminToken)
//                .contentType(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .body(product)
//                .when()
//                .post("/api/products")
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract()
//                .as(ENTITY_TYPE);
//
//        // Validate the Product in the database
//        var productList = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE);
//
//        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
//        var testProduct = productList.stream().filter(it -> product.id.equals(it.id)).findFirst().get();
//        assertThat(testProduct.productName).isEqualTo(DEFAULT_PRODUCT_NAME);
//        assertThat(testProduct.description).isEqualTo(DEFAULT_DESCRIPTION);
//        assertThat(testProduct.price).isEqualTo(DEFAULT_PRICE);
//        assertThat(testProduct.imageUrl).isEqualTo(DEFAULT_IMAGE_URL);
//    }
//
//    @Test
//    public void createProductWithExistingId() {
//        var databaseSizeBeforeCreate = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE)
//            .size();
//
//        // Create the Product with an existing ID
//        product.id = 1L;
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .contentType(APPLICATION_JSON)
//            .accept(APPLICATION_JSON)
//            .body(product)
//            .when()
//            .post("/api/products")
//            .then()
//            .statusCode(BAD_REQUEST.getStatusCode());
//
//        // Validate the Product in the database
//        var productList = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE);
//
//        assertThat(productList).hasSize(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    public void updateProduct() {
//        // Initialize the database
//        product =
//            given()
//                .auth()
//                .preemptive()
//                .oauth2(adminToken)
//                .contentType(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .body(product)
//                .when()
//                .post("/api/products")
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract()
//                .as(ENTITY_TYPE);
//
//        var databaseSizeBeforeUpdate = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE)
//            .size();
//
//        // Get the product
//        var updatedProduct = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products/{id}", product.id)
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .body()
//            .as(ENTITY_TYPE);
//
//        // Update the product
//        updatedProduct.productName = UPDATED_PRODUCT_NAME;
//        updatedProduct.description = UPDATED_DESCRIPTION;
//        updatedProduct.price = UPDATED_PRICE;
//        updatedProduct.imageUrl = UPDATED_IMAGE_URL;
//
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .contentType(APPLICATION_JSON)
//            .accept(APPLICATION_JSON)
//            .body(updatedProduct)
//            .when()
//            .put("/api/products/" + product.id)
//            .then()
//            .statusCode(OK.getStatusCode());
//
//        // Validate the Product in the database
//        var productList = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE);
//
//        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
//        var testProduct = productList.stream().filter(it -> updatedProduct.id.equals(it.id)).findFirst().get();
//        assertThat(testProduct.productName).isEqualTo(UPDATED_PRODUCT_NAME);
//        assertThat(testProduct.description).isEqualTo(UPDATED_DESCRIPTION);
//        assertThat(testProduct.price).isEqualTo(UPDATED_PRICE);
//        assertThat(testProduct.imageUrl).isEqualTo(UPDATED_IMAGE_URL);
//    }
//
//    @Test
//    public void updateNonExistingProduct() {
//        var databaseSizeBeforeUpdate = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE)
//            .size();
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .contentType(APPLICATION_JSON)
//            .accept(APPLICATION_JSON)
//            .body(product)
//            .when()
//            .put("/api/products/" + Long.MAX_VALUE)
//            .then()
//            .statusCode(BAD_REQUEST.getStatusCode());
//
//        // Validate the Product in the database
//        var productList = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE);
//
//        assertThat(productList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    public void deleteProduct() {
//        // Initialize the database
//        product =
//            given()
//                .auth()
//                .preemptive()
//                .oauth2(adminToken)
//                .contentType(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .body(product)
//                .when()
//                .post("/api/products")
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract()
//                .as(ENTITY_TYPE);
//
//        var databaseSizeBeforeDelete = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE)
//            .size();
//
//        // Delete the product
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .delete("/api/products/{id}", product.id)
//            .then()
//            .statusCode(NO_CONTENT.getStatusCode());
//
//        // Validate the database contains one less item
//        var productList = given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(LIST_OF_ENTITY_TYPE);
//
//        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//
//    @Test
//    public void getAllProducts() {
//        // Initialize the database
//        product =
//            given()
//                .auth()
//                .preemptive()
//                .oauth2(adminToken)
//                .contentType(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .body(product)
//                .when()
//                .post("/api/products")
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract()
//                .as(ENTITY_TYPE);
//
//        // Get all the productList
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products?sort=id,desc")
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .body("id", hasItem(product.id.intValue()))
//            .body("productName", hasItem(DEFAULT_PRODUCT_NAME))
//            .body("description", hasItem(DEFAULT_DESCRIPTION))
//            .body("price", hasItem(DEFAULT_PRICE.doubleValue()))
//            .body("imageUrl", hasItem(DEFAULT_IMAGE_URL));
//    }
//
//    @Test
//    public void getProduct() {
//        // Initialize the database
//        product =
//            given()
//                .auth()
//                .preemptive()
//                .oauth2(adminToken)
//                .contentType(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .body(product)
//                .when()
//                .post("/api/products")
//                .then()
//                .statusCode(CREATED.getStatusCode())
//                .extract()
//                .as(ENTITY_TYPE);
//
//        var response = given() // Get the product
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products/{id}", product.id)
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .extract()
//            .as(ENTITY_TYPE);
//
//        // Get the product
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products/{id}", product.id)
//            .then()
//            .statusCode(OK.getStatusCode())
//            .contentType(APPLICATION_JSON)
//            .body("id", is(product.id.intValue()))
//            .body("productName", is(DEFAULT_PRODUCT_NAME))
//            .body("description", is(DEFAULT_DESCRIPTION))
//            .body("price", is(DEFAULT_PRICE.doubleValue()))
//            .body("imageUrl", is(DEFAULT_IMAGE_URL));
//    }
//
//    @Test
//    public void getNonExistingProduct() {
//        // Get the product
//        given()
//            .auth()
//            .preemptive()
//            .oauth2(adminToken)
//            .accept(APPLICATION_JSON)
//            .when()
//            .get("/api/products/{id}", Long.MAX_VALUE)
//            .then()
//            .statusCode(NOT_FOUND.getStatusCode());
//    }
//}
