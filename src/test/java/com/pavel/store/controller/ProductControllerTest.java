package com.pavel.store.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProductControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {

        baseURI = "http://localhost";
        RestAssured.port = port;
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void getAllProductsWithDefaultParametersShouldReturn200() {

        given()
                .when()
                .get("/api/v1/products")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", hasSize(13))
                .body("totalElements", equalTo(13))
                .body("pageSize", equalTo(20));

    }

    @Test
    void getProductById_WithExistingId_ShouldReturn200AndProduct() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(1))
                .body("name", equalTo("MacBook Pro 16\""))
                .body("article", equalTo("MBP16-001"))
                .body("description", equalTo("Apple MacBook Pro 16 inch with M2 Pro chip"))
                .body("price", equalTo(2499.99f))
                .body("amount", equalTo(15))
                .body("categoryName", equalTo("Electronics"))
                .body("image", equalTo("/images/macbook-pro.jpg"));
    }

    @Test
    void getProductById_WithAnotherExistingId_ShouldReturn200AndProduct() {
        given()
                .pathParam("id", 2)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("id", equalTo(2))
                .body("name", equalTo("iPhone 15 Pro"))
                .body("article", equalTo("IP15P-002"))
                .body("price", equalTo(999.99f))
                .body("amount", equalTo(50));
    }

    @Test
    void getProductById_WithNonExistingId_ShouldReturn404() {
        given()
                .pathParam("id", 9999)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", containsString("not found")); // или проверьте структуру вашего ErrorResponse
    }

    @Test
    void getProductById_WithInvalidIdFormat_ShouldReturn500() {
        given()
                .pathParam("id", "invalid-id")
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(500);
    }

    @Test
    void getProductById_WithZeroId_ShouldReturn404() {
        given()
                .pathParam("id", 0)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getProductById_WithNegativeId_ShouldReturn404() {
        given()
                .pathParam("id", -1)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(404);
    }

    // ========== TESTS FOR findImage ==========


    @Test
    void findImage_WithExistingProductButNoImage_ShouldReturn500() {
        // Предполагая, что у продукта с ID=5 нет изображения (проверьте ваш data.sql)
        given()
                .pathParam("id", 5)
                .when()
                .get("/api/v1/products/{id}/image")
                .then()
                .statusCode(500);
        // или проверьте структуру ErrorResponse
    }

    @Test
    void findImage_WithNonExistingProduct_ShouldReturn500() {
        given()
                .pathParam("id", 9999)
                .when()
                .get("/api/v1/products/{id}/image")
                .then()
                .statusCode(500);
    }

    @Test
    void findImage_WithInvalidId_ShouldReturn500() {
        given()
                .pathParam("id", "invalid")
                .when()
                .get("/api/v1/products/{id}/image")
                .then()
                .statusCode(500);
    }

    // ========== COMPARISON TESTS ==========


    @Test
    void getProductAndImage_ForNonExistingProduct_ShouldBothReturn500() {
        Long nonExistingId = 9999L;

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

        given()
                .pathParam("id", nonExistingId)
                .when()
                .get("/api/v1/products/{id}/image")
                .then()
                .statusCode(500);
    }


    @Test
    void getProductById_ShouldReturnCorrectContentType() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .contentType(ContentType.JSON)
                .header("Content-Type", containsString("application/json"));
    }

    @Test
    void findImage_ShouldHaveValidResponseHeaders() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/api/v1/products/{id}/image")
                .then()
                .header("Content-Length", notNullValue()) // если изображение существует
                .header("Content-Type", notNullValue());
    }

    @Test
    void sequentialCallsToSameProduct_ShouldReturnConsistentResults() {
        Long productId = 3L;

        // Первый вызов
        given().pathParam("id", productId).get("/api/v1/products/{id}").then().statusCode(200);

        // Второй вызов - должен вернуть тот же результат
        given().pathParam("id", productId).get("/api/v1/products/{id}").then().statusCode(200);

        // Третий вызов - изображение
        given().pathParam("id", productId).get("/api/v1/products/{id}/image").then().statusCode(500); // или 404
    }

    @Test
    void getFilterProductShouldReturn200() {
        String bodyJson = """
                {
                  "amount": 50,
                  "price": 1000,
                  "is_Available": true
                }
                """;
        given().body(bodyJson)
                .when()
                .get("/api/v1/products/search")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void getFilterProductShouldReturn200StatusAnd2ElementsIfPrice40() {

        given().param("price", 40)
                .when()
                .get("/api/v1/products/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2));

    }
    @Test
    void getFilterProductShouldReturn200StatusAnd2ElementsIfAmount100() {

        given().param("amount", 100)
                .when()
                .get("/api/v1/products/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2));

    }
}


