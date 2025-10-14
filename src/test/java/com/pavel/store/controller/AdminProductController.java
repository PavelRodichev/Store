package com.pavel.store.controller;

import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminProductController {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void createProduct_ShouldReturn201() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("LaptoPProduct");
        dto.setPrice(BigDecimal.valueOf(999.99));
        dto.setAmount(12);
        dto.setCategoryName("Books");
        dto.setArticle("artcikae");
        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/admin/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("LaptoPProduct"))
                .body("price", equalTo(999.99F));
    }

    @Test
    void updateProduct_ShouldReturn200() {
        String bodyDto = """
                {
                  "name": "Test",
                  "price": 23.2,
                  "description": "string",
                  "categoryName": "Books",
                  "article": "asdawd2",
                  "amount": 20
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .body(bodyDto)
                .when()
                .put("/api/admin/products/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Test"))
                .body("price", equalTo(23.2F));
    }

    @Test
    void deleteProductById_ShouldReturn204() {
        Long productId = 1L;

        given()
                .pathParam("id", productId)
                .when()
                .delete("/api/admin/products/{id}")
                .then()
                .statusCode(204);

        // Проверка, что продукт больше не существует
        given()
                .pathParam("id", productId)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(404);
    }
}
