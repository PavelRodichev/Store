package com.pavel.store.controller;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.annotation.DirtiesContext;


import java.util.concurrent.CompletableFuture;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any; // ✅ ДОБАВЬТЕ ЭТОТ ИМПОРТ
import static org.mockito.ArgumentMatchers.anyString; // ✅ ДОБАВЬТЕ ЭТОТ ИМПОРТ
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/sql/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplateMock;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost";
        RestAssured.port = port;
        enableLoggingOfRequestAndResponseIfValidationFails();

        // ✅ Правильный стабинг для KafkaTemplate
        when(kafkaTemplateMock.send(anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void CreateUserWithValidDataShouldReturnCreatedUser200() {
        String jsonBody = """
                {
                  "username": "Test1Username",
                  "email": "Test1@mail.com",
                  "password": "Test1234",
                  "firstName": "Test1First",
                  "lastName": "Test1Last"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/api/v1/users/register")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("username", equalTo("Test1Username"))
                .body("email", equalTo("Test1@mail.com"))
                .body("firstName", equalTo("Test1First"))
                .body("lastName", equalTo("Test1Last"))
                .body("role", equalTo("USER"));

    }

    @Test
    void CreateUserWithNotValidEmailShouldReturn400status() {
        String jsonBody = """
                {
                  "username": "TestUsername",
                  "email": "Testmail.com",
                  "password": "Test123",
                  "firstName": "TestFirst",
                  "lastName": "TestLast"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/api/v1/users/register") // Добавлен слеш в начале
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("exception", equalTo("MethodArgumentNotValidException"))
                .body("message", containsString("email")) // Более гибкая проверка
                .header("content-type", "application/json");
    }

    @Test
    void getAllUsersWithDefaultParamsShouldReturn200() {
        given()
                .when()
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", hasSize(6))
                .body("totalElements", equalTo(6))
                .body("content[0].username", notNullValue()); // Дополнительная проверка
    }

    @Test
    void deleteUser_ShouldReturn204() {
        Long userId = 1L;

        // Удаление пользователя
        given()
                .pathParam("id", userId)
                .when()
                .delete("/api/v1/users/{id}")
                .then()
                .statusCode(204);

        // Проверка, что пользователь удалён
        given()
                .pathParam("id", userId)
                .when()
                .get("/api/v1/users/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    void getUserById_ShouldReturn200() {
        Long userId = 2L;

        given()
                .pathParam("id", userId)
                .when()
                .get("/api/v1/users/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userId.intValue()))
                .body("username", notNullValue())
                .body("email", notNullValue());
    }

    @Test
    void updateUser_ShouldReturn200() {
        Long userId = 3L;
        String jsonBody = """
                {                          
                  "firstName": "UpdatedFirst",
                  "lastName": "UpdatedLast",
                   "email": "updated@mail.com"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .pathParam("id", userId)
                .when()
                .put("/api/v1/users/{id}")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("UpdatedFirst"))
                .body("email", equalTo("updated@mail.com"));
    }

    @Test
    void createUserWithNullUsernameShouldReturn400() {
        String jsonBody = """
                {
                  "username": null,
                  "email": "Test@mail.com",
                  "password": "Test123",
                  "firstName": "TestFirst",
                  "lastName": "TestLast"
                }
                """;
        given().contentType(ContentType.JSON)
                .body(jsonBody).
                when().post("api/v1/users/register")
                .then().statusCode(400).body("exception", equalTo("MethodArgumentNotValidException"))
                .body("message", equalTo("username: Username is required"));


    }

    @Test
    void createUser_WithoutPassword_ShouldReturn400() {
        String jsonWithoutPassword = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonWithoutPassword)
                .when()
                .post("/api/v1/users/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("password"));
    }

    @Test
    void createUser_WithoutEmail_ShouldReturn400() {
        String jsonWithoutEmail = """
                {
                    "username": "testuser",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonWithoutEmail)
                .when()
                .post("/api/v1/users/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("email")); // Проверяем что ошибка связана с отсутствующим email
    }

    @Test
    void createUser_WithBlankEmail_ShouldReturn400() {
        String jsonWithBlankEmail = """
                {
                    "username": "testuser",
                    "email": "   ",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonWithBlankEmail)
                .when()
                .post("/api/v1/users/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message",
                        anyOf(
                                containsString("email"),
                                containsString("blank"),
                                containsString("empty"),
                                containsString("required"),
                                containsString("valid")
                        ));
    }

    @Test
    void createUser_WithBlankUsername_ShouldReturn400() {
        String jsonWithBlankUsername = """
                {
                    "username": "   ",
                    "email": "test@example.com",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(jsonWithBlankUsername)
                .when()
                .post("/api/v1/users/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("username")); // Проверяем что ошибка связана с username
    }
}