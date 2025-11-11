package com.pavel.store.controller;

import com.pavel.store.config.TestSecurityConfig;
import com.pavel.store.service.IdempotencyService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ✅ ДОБАВЬТЕ ЭТОТ ИМПОРТ
import org.springframework.web.client.RestTemplate;

// ✅ ПРАВИЛЬНЫЕ ИМПОРТЫ MOCKITO
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

// ✅ ИМПОРТЫ REST ASSURED
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(TestSecurityConfig.class)
public class OrderControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private IdempotencyService idempotencyServiceMock;
    @MockitoBean
    private RestTemplate restTemplateMock;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost";
        RestAssured.port = port;
        enableLoggingOfRequestAndResponseIfValidationFails();

        when(idempotencyServiceMock.hasExistKey(anyString())).thenReturn(false);
        when(idempotencyServiceMock.saveKeyWithOrderId(anyString(), anyLong())).thenReturn(true);
    }

    // ========== TESTS FOR GET ALL ORDERS ==========

    @Test
    void getAllOrders_WithDefaultPagination_ShouldReturn200AndPageOfOrders() {
        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content", hasSize(5))
                .body("totalElements", equalTo(5))
                .body("currentPage", equalTo(0))
                .body("pageSize", equalTo(20));
    }

    @Test
    void getAllOrders_WithCustomPagination_ShouldReturn200AndCorrectPage() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("content", hasSize(2))
                .body("totalElements", equalTo(5))
                .body("currentPage", equalTo(0))
                .body("pageSize", equalTo(2));
    }

    // ========== TESTS FOR GET ORDER BY ID/USER ID ==========

    @Test
    void getOrder_WithExistingOrderId_ShouldReturn200AndOrder() {
        given()
                .queryParam("orderId", 1)
                .when()
                .get("/api/v1/orders/order")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("totalAmount", equalTo(3399.98f))
                .body("address", equalTo("123 Main St, New York, NY"))
                .body("orderStatus", equalTo("CREATED"));
    }

    @Test
    void getOrder_WithExistingUserId_ShouldReturn200AndOrder() {
        given()
                .queryParam("userId", 2)
                .when()
                .get("/api/v1/orders/order")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("username", equalTo("john_doe")) // ← ИЗМЕНИТЕ "userId" на "username"
                .body("orderStatus", equalTo("CREATED"))
                .body("totalAmount", equalTo(3399.98f));
    }

    @Test
    void getOrder_WithNonExistingOrderId_ShouldReturn404() {
        given()
                .queryParam("orderId", 9999)
                .when()
                .get("/api/v1/orders/order")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getOrder_WithoutParameters_ShouldReturn400() {
        given()
                .when()
                .get("/api/v1/orders/order")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    // ========== TESTS FOR CREATE ORDER ==========

    @Test
    void createOrder_WithValidData_ShouldReturn201AndOrder() {
        Map<String, Object> createDto = new HashMap<>();
        createDto.put("userId", 2L);
        createDto.put("shippingAddress", "New Test Address");

        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1L);
        item1.put("quantity", 1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 2L);
        item2.put("quantity", 1);

        createDto.put("items", List.of(item1, item2));

        String idempotencyKey = "test-key-" + System.currentTimeMillis();

        given()
                .header("Idempotency-Key", idempotencyKey)
                .contentType(ContentType.JSON)
                .body(createDto)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("username", equalTo("john_doe"))
                .body("address", equalTo("New Test Address"))
                .body("orderStatus", equalTo("CREATED"));
    }

    @Test
    void createOrder_WithNonExistingUser_ShouldReturn404() {
        Map<String, Object> createDto = new HashMap<>();
        createDto.put("userId", 9999L);
        createDto.put("shippingAddress", "Test Address");
        createDto.put("items", List.of());

        given()
                .header("Idempotency-Key", "test-key-non-existing-user")
                .contentType(ContentType.JSON)
                .body(createDto)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createOrder_WithNonExistingProduct_ShouldReturn404() {
        Map<String, Object> createDto = new HashMap<>();
        createDto.put("userId", 2L);
        createDto.put("shippingAddress", "Test Address");

        Map<String, Object> item = new HashMap<>();
        item.put("productId", 9999L);
        item.put("quantity", 1);

        createDto.put("items", List.of(item));

        given()
                .header("Idempotency-Key", "test-key-non-existing-product")
                .contentType(ContentType.JSON)
                .body(createDto)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createOrder_WithoutIdempotencyKey_ShouldReturn400() {
        Map<String, Object> createDto = new HashMap<>();
        createDto.put("userId", 2L);
        createDto.put("shippingAddress", "Test Address");
        createDto.put("items", List.of());

        given()
                .contentType(ContentType.JSON)
                .body(createDto)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    // ========== TESTS FOR GET ORDERS BY PRODUCT ID ==========

    @Test
    void findOrdersByProductId_WithExistingProduct_ShouldReturn200AndOrderList() {
        given()
                .pathParam("productId", 1)
                .when()
                .get("/api/v1/orders/{productId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
    }

    @Test
    void findOrdersByProductId_WithNonExistingProduct_ShouldReturn404() {
        given()
                .pathParam("productId", 9999)
                .when()
                .get("/api/v1/orders/{productId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    // ========== TESTS FOR UPDATE ORDER ==========

    @Test
    void updateOrder_WithValidData_ShouldReturn200AndUpdatedOrder() {
        Map<String, Object> updateDto = new HashMap<>();
        updateDto.put("shippingAddress", "Updated Test Address");
        updateDto.put("status", "COMPLETED");

        given()
                .contentType(ContentType.JSON)
                .body(updateDto)
                .pathParam("id", 1)
                .when()
                .put("/api/v1/orders/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("address", equalTo("Updated Test Address"))
                .body("orderStatus", equalTo("COMPLETED"));
    }

    @Test
    void updateOrder_WithNonExistingOrder_ShouldReturn404() {
        Map<String, Object> updateDto = new HashMap<>();
        updateDto.put("shippingAddress", "Test Address");
        updateDto.put("status", "COMPLETED");

        given()
                .contentType(ContentType.JSON)
                .body(updateDto)
                .pathParam("id", 9999)
                .when()
                .put("/api/v1/orders/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    // ========== TESTS FOR ADD ITEMS TO ORDER ==========

    @Test
    void addItemsToOrder_WithValidData_ShouldReturn200AndUpdatedOrder() {
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("productId", 3L);
        newItem.put("quantity", 2);

        given()
                .contentType(ContentType.JSON)
                .body(newItem)
                .queryParam("orderId", 1)
                .when()
                .put("/api/v1/orders")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1));
    }

    // ========== TESTS FOR DELETE ORDER ==========

    @Test
    void deleteOrder_WithExistingOrder_ShouldReturn204() {
        given()
                .pathParam("id", 5)
                .when()
                .delete("/api/v1/orders/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteOrder_WithNonExistingOrder_ShouldReturn404() {
        given()
                .pathParam("id", 9999)
                .when()
                .delete("/api/v1/orders/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    // ========== TESTS FOR GROUPING PRODUCTS BY ORDER ==========

    @Test
    void getProductsGroupingOrders_ShouldReturn200AndGroupedData() {
        given()
                .when()
                .get("/api/v1/orders/groupOfProducts")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].product", notNullValue())
                .body("[0].orders", notNullValue());
    }
}