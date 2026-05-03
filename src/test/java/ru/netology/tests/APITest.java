package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.API;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQL;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("API тесты оплаты тура")
public class APITest {

    private static List<SQL.PaymentEntity> payments;
    private static List<SQL.CreditRequestEntity> credits;
    private static List<SQL.OrderEntity> orders;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure",
                new AllureSelenide().screenshots(true).savePageSource(true));
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

        String status = API.sendPayRequest(API.getApiApprovedCard());

        refreshDB();

        assertAll(
                () -> assertEquals("APPROVED", status),
                () -> assertEquals(1, payments.size()),
                () -> assertEquals(1, orders.size()),
                () -> assertEquals(0, credits.size()),
                () -> assertEquals("APPROVED", payments.get(0).getStatus())
        );
    }

    @Test
    @DisplayName("02. Отклонённая оплата (DECLINED)")
    void shouldDeclinePayment() {

        String status = API.sendPayRequest(API.getApiDeclinedCard());

        refreshDB();

        assertAll(
                () -> assertEquals("DECLINED", status),
                () -> assertEquals(1, payments.size()),
                () -> assertEquals(1, orders.size()),
                () -> assertEquals(0, credits.size()),
                () -> assertEquals("DECLINED", payments.get(0).getStatus())
        );
    }

    // -- ВАЛИДАЦИЯ ПОЛЕЙ (ПУСТЫЕ ЗНАЧЕНИЯ) --

    @Test
    @DisplayName("03. Пустой номер карты")
    void shouldErrorEmptyCardNumber() {

        API.sendPayRequestBadRequest(API.getApiEmptyNumberCard());

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("04. Пустой месяц")
    void shouldErrorEmptyMonth() {

        API.sendPayRequestBadRequest(API.getApiEmptyMonthCard());

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("05. Пустой год")
    void shouldErrorEmptyYear() {

        API.sendPayRequestBadRequest(API.getApiEmptyYearCard());

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("06. Пустое поле владельца")
    void shouldErrorEmptyOwner() {

        API.sendPayRequestBadRequest(API.getApiEmptyOwnerCard());

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("07. Пустой CVC")
    void shouldErrorEmptyCvc() {

        API.sendPayRequestBadRequest(API.getApiEmptyCVCCard());

        refreshDB();
        assertEquals(0, payments.size());
    }

    // -- НЕВАЛИДНЫЕ ДАННЫЕ --

    @Test
    @DisplayName("08. Неверный формат номера карты")
    void shouldErrorInvalidCardNumber() {

        API.sendPayRequestBadRequest(new DataGenerator.CardData(
                "1234 5678 0000 0000",
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("09. Неверный месяц (13)")
    void shouldErrorInvalidMonth() {

        API.sendPayRequestBadRequest(new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                "13",
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("10. Просроченный год")
    void shouldErrorExpiredYear() {

        API.sendPayRequestBadRequest(new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                DataGenerator.expiredYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));

        refreshDB();
        assertEquals(0, payments.size());
    }

    @Test
    @DisplayName("11. Короткий CVC")
    void shouldErrorShortCvc() {

        API.sendPayRequestBadRequest(new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                "12"
        ));

        refreshDB();
        assertEquals(0, payments.size());
    }
}