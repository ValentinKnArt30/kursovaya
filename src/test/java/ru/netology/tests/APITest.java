package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import ru.netology.data.API;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQL;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("API тесты оплаты тура")
public class APITest {

    private static List<SQL.PaymentEntity> payments;
    private static List<SQL.CreditRequestEntity> credits;
    private static List<SQL.OrderEntity> orders;

    private static RequestSpecification requestSpec;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure",
                new AllureSelenide().screenshots(true).savePageSource(true));

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void cleanDatabase() {
        SQL.clear();
    }

    private void refreshDB() {
        payments = SQL.getPayments();
        credits = SQL.getCreditsRequest();
        orders = SQL.getOrders();
    }

    // -- ПОЛОЖИТЕЛЬНЫЕ СЦЕНАРИИ --

    @Test
    @DisplayName("01. Успешная оплата (APPROVED)")
    void shouldApprovePayment() {

        String status = given()
                .spec(requestSpec)
                .body(API.getApiApprovedCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .extract()
                .path("status");

        refreshDB();

        assertEquals("APPROVED", status);
        assertEquals(1, payments.size());
        assertEquals(1, orders.size());
        assertEquals(0, credits.size());

        assertEquals("APPROVED", payments.get(0).getStatus());
    }

    @Test
    @DisplayName("02. Отклонённая оплата (DECLINED)")
    void shouldDeclinePayment() {

        String status = given()
                .spec(requestSpec)
                .body(API.getApiDeclinedCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .extract()
                .path("status");

        refreshDB();

        assertEquals("DECLINED", status);
        assertEquals(1, payments.size());
        assertEquals(1, orders.size());
        assertEquals(0, credits.size());

        assertEquals("DECLINED", payments.get(0).getStatus());
    }

    // -- ВАЛИДАЦИЯ ПОЛЕЙ (ПУСТЫЕ ЗНАЧЕНИЯ) --

    @Test
    @DisplayName("03. Пустой номер карты")
    void shouldErrorEmptyCardNumber() {

        given()
                .spec(requestSpec)
                .body(API.getApiEmptyNumberCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        //System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("04. Пустой месяц")
    void shouldErrorEmptyMonth() {

        given()
                .spec(requestSpec)
                .body(API.getApiEmptyMonthCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
//        System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("05. Пустой год")
    void shouldErrorEmptyYear() {

        given()
                .spec(requestSpec)
                .body(API.getApiEmptyYearCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        // System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("06. Пустое поле владельца")
    void shouldErrorEmptyOwner() {

        given()
                .spec(requestSpec)
                .body(API.getApiEmptyOwnerCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        //System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("07. Пустой CVC")
    void shouldErrorEmptyCvc() {

        given()
                .spec(requestSpec)
                .body(API.getApiEmptyCVCCard())
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        // System.out.println(SQL.getPayments());
    }

    // -- НЕВАЛИДНЫЕ ДАННЫЕ --

    @Test
    @DisplayName("08. Неверный формат номера карты")
    void shouldErrorInvalidCardNumber() {

        var data = new DataGenerator.CardData(
                "1234 5678 0000 0000",
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        );

        given()
                .spec(requestSpec)
                .body(data)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        // System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("09. Неверный месяц (13)")
    void shouldErrorInvalidMonth() {

        var data = new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                "13",
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        );

        given()
                .spec(requestSpec)
                .body(data)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        // System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("10. Просроченный год")
    void shouldErrorExpiredYear() {

        var data = new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                DataGenerator.expiredYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        );

        given()
                .spec(requestSpec)
                .body(data)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        // System.out.println(SQL.getPayments());
    }

    @Test
    @DisplayName("11. Короткий CVC")
    void shouldErrorShortCvc() {

        var data = new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                "12"
        );

        given()
                .spec(requestSpec)
                .body(data)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

        refreshDB();
        assertEquals(0, payments.size());
        // System.out.println(SQL.getPayments());
    }
}