package com.pavel.store.controller;


import com.pavel.store.config.TestSecurityConfig;
import com.pavel.store.controller.rest.AuthController;
import com.pavel.store.dto.request.ProductCreateDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
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
@Import(TestSecurityConfig.class)
public class AdminProductController {


    @LocalServerPort
    private int port;



    @Mock
    private AuthController authController;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    }

    @Test
    void createProduct_ShouldReturn201() {
        ProductCreateDto dto = new ProductCreateDto();
        dto.setName("Test Laptop");
        dto.setPrice(BigDecimal.valueOf(999.99));
        dto.setAmount(10);
        dto.setCategoryName("Electronics");
        dto.setArticle("TEST-LAPTOP-001");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/admin/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Laptop"))
                .body("price", equalTo(999.99F));
    }


    @Test
    void updateProduct_ShouldReturn200() {
        // Сначала создаем продукт для обновления
        ProductCreateDto createDto = new ProductCreateDto();
        createDto.setName("Initial Product");
        createDto.setPrice(BigDecimal.valueOf(100.0));
        createDto.setAmount(5);
        createDto.setCategoryName("Electronics");
        createDto.setArticle("INITIAL-001");

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(createDto)
                .when()
                .post("/api/admin/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Теперь обновляем продукт
        String updateBody = """
                {
                  "name": "Updated Product",
                  "price": 23.2,
                  "description": "Updated description",
                  "categoryName": "Electronics",
                  "article": "UPDATED-001",
                  "amount": 20
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/api/admin/products/{id}", productId)
                .then()
                .statusCode(200)
                .body("id", equalTo(productId))
                .body("name", equalTo("Updated Product"))
                .body("price", equalTo(23.2F));
    }

    @Test
    void deleteProductById_ShouldReturn204() {
        // Сначала создаем продукт для удаления
        ProductCreateDto createDto = new ProductCreateDto();
        createDto.setName("Product To Delete");
        createDto.setPrice(BigDecimal.valueOf(50.0));
        createDto.setAmount(3);
        createDto.setCategoryName("Electronics");
        createDto.setArticle("DELETE-001");

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(createDto)
                .when()
                .post("/api/admin/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Удаляем продукт
        given()
                .pathParam("id", productId)
                .when()
                .delete("/api/admin/products/{id}")
                .then()
                .statusCode(204);

        // Проверяем, что продукт удален
        given()
                .pathParam("id", productId)
                .when()
                .get("/api/v1/products/{id}")
                .then()
                .statusCode(404);
    }

}
