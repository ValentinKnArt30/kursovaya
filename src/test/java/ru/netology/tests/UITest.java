package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.data.SQL;
import ru.netology.pages.CreditPage;
import ru.netology.pages.MainPage;
import ru.netology.pages.PaymentPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UI тесты оплаты тура")
@Feature("Оплата картой")
public class UITest {

    PaymentPage paymentPage;
    CreditPage creditPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
        SQL.clear();
    }

    // ----------------------
    // Вспомогательные методы
    // ----------------------

    @Step("Открыть оплату картой")
    void openPayment() {
        var mainPage = new MainPage();
        paymentPage = mainPage.clickButtonPay();
        paymentPage.verifyPageVisible();
    }

    @Step("Открыть кредит")
    void openCredit() {
        var mainPage = new MainPage();
        creditPage = mainPage.clickButtonCredit();
        creditPage.verifyPageVisible();
    }

    @Step("Заполнить и отправить форму оплаты картой")
    void fillAndSubmitPayment(DataGenerator.CardData data) {
        openPayment();
        paymentPage.fillCardForm(data).submit();
    }

    @Step("Заполнить и отправить форму кредита")
    void fillAndSubmitCredit(DataGenerator.CardData data) {
        openCredit();
        creditPage.fillCardForm(data).submit();
    }

    // ----------------------
    // ПОЗИТИВНЫЕ СЦЕНАРИИ
    // ----------------------

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("1. Успешная оплата APPROVED картой")
    void shouldPayWithApprovedCard() {
        var data = DataGenerator.approvedCard();

        fillAndSubmitPayment(data);

        paymentPage.shouldSeeSuccess();

        var payments = SQL.getPayments();
        var orders = SQL.getOrders();

        assertAll(
                () -> assertEquals(1, payments.size()),
                () -> assertEquals("APPROVED", payments.get(0).getStatus()),
                () -> assertEquals(1, orders.size())
        );
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("2. Успешный кредит APPROVED картой")
    void shouldPayCreditWithApprovedCard() {
        var data = DataGenerator.approvedCard();

        fillAndSubmitCredit(data);

        creditPage.shouldSeeSuccess();

        var credits = SQL.getCreditsRequest();
        var orders = SQL.getOrders();

        assertAll(
                () -> assertEquals(1, credits.size()),
                () -> assertEquals("APPROVED", credits.get(0).getStatus()),
                () -> assertEquals(1, orders.size())
        );
    }

    // ----------------------
    // ОТКАЗНЫЕ СЦЕНАРИИ
    // ----------------------

    @Test
    @DisplayName("3. Отказ в оплате DECLINED картой")
    void shouldDeclinePaymentWithDeclinedCard() {
        var data = DataGenerator.declinedCard();

        fillAndSubmitPayment(data);

        paymentPage.shouldSeeError();

        var payments = SQL.getPayments();
        var orders = SQL.getOrders();

        assertAll(
                () -> assertEquals(1, payments.size()),
                () -> assertEquals("DECLINED", payments.get(0).getStatus()),
                () -> assertEquals(1, orders.size())
        );
    }

    @Test
    @DisplayName("4. Отказ в кредите DECLINED картой")
    void shouldDeclineCreditWithDeclinedCard() {
        var data = DataGenerator.declinedCard();

        fillAndSubmitCredit(data);

        creditPage.shouldSeeError();

        var credits = SQL.getCreditsRequest();
        var orders = SQL.getOrders();

        assertAll(
                () -> assertEquals(1, credits.size()),
                () -> assertEquals("DECLINED", credits.get(0).getStatus()),
                () -> assertEquals(1, orders.size())
        );
    }

    // ----------------------
    // НЕВЕРНЫЕ НОМЕРА КАРТ
    // ----------------------

    @Test
    @DisplayName("5. Невалидный номер карты")
    void shouldErrorInvalidCardNumber() {
        fillAndSubmitPayment(new DataGenerator.CardData(
                DataGenerator.invalidCardNumber(),
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));
        paymentPage.shouldSeeCardFieldError("Неверный формат");
    }

    @Test
    @DisplayName("6. Короткий номер карты")
    void shouldErrorShortCardNumber() {
        fillAndSubmitPayment(new DataGenerator.CardData(
                DataGenerator.shortCardNumber(),
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));
        paymentPage.shouldSeeCardFieldError("Номер карты должен содержать 16 цифр");
    }

    @Test
    @DisplayName("7. Длинный номер карты")
    void shouldErrorLongCardNumber() {
        fillAndSubmitPayment(new DataGenerator.CardData(
                DataGenerator.longCardNumber(),
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));
        paymentPage.shouldSeeCardFieldError("Номер карты должен содержать 16 цифр");
    }

    @Test
    @DisplayName("8. Пустой номер карты")
    void shouldErrorEmptyCardNumber() {
        fillAndSubmitPayment(new DataGenerator.CardData(
                DataGenerator.empty(),
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        ));
        paymentPage.shouldSeeCardFieldError("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("9. Номер с пробелами")
    void shouldAcceptCardNumberWithSpaces() {
        fillAndSubmitPayment(DataGenerator.cardWithSpaces());
        paymentPage.shouldSeeSuccess();
    }

    @Test
    @DisplayName("10. Спецсимволы в номере")
    void shouldErrorSpecialSymbolsCardNumber() {
        fillAndSubmitPayment(DataGenerator.cardWithSymbols());
        paymentPage.shouldSeeCardFieldError("Неверный формат");
    }

    @Test
    @DisplayName("11. Placeholder")
    void shouldErrorPlaceholderCardNumber() {
        fillAndSubmitPayment(DataGenerator.placeholderCard());
        paymentPage.shouldSeeCardFieldError("Неверный формат");
    }

    // ----------------------
    // МЕСЯЦ И ГОД
    // ----------------------

    @Test
    @DisplayName("12. Месяц 13")
    void shouldErrorInvalidMonth() {
        fillAndSubmitPayment(DataGenerator.cardWithMonth("13"));
        paymentPage.shouldSeeMonthFieldError("Месяц должен быть от 01 до 12");
    }

    @Test
    @DisplayName("13. Буквы в месяце")
    void shouldErrorLettersMonth() {
        fillAndSubmitPayment(DataGenerator.cardWithMonth("ab"));
        paymentPage.shouldSeeMonthFieldError("Неверный формат");
    }

    @Test
    @DisplayName("14. Пустой месяц")
    void shouldErrorEmptyMonth() {
        fillAndSubmitPayment(DataGenerator.cardWithMonth(""));
        paymentPage.shouldSeeMonthFieldError("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("15. Просроченный год")
    void shouldErrorExpiredYear() {
        fillAndSubmitPayment(DataGenerator.cardWithYear(DataGenerator.expiredYear()));
        paymentPage.shouldSeeYearFieldError("Истёк срок действия карты");
    }

    @Test
    @DisplayName("16. Будущий год")
    void shouldErrorTooFutureYear() {
        fillAndSubmitPayment(DataGenerator.cardWithYear(DataGenerator.futureYear()));
        paymentPage.shouldSeeYearFieldError("Неверно указан срок");
    }

    @Test
    @DisplayName("17. Буквы в годе")
    void shouldErrorLettersYear() {
        fillAndSubmitPayment(DataGenerator.cardWithYear("cd"));
        paymentPage.shouldSeeYearFieldError("Неверный формат");
    }

    @Test
    @DisplayName("18. Пустой год")
    void shouldErrorEmptyYear() {
        fillAndSubmitPayment(DataGenerator.cardWithYear(""));
        paymentPage.shouldSeeYearFieldError("Поле обязательно для заполнения");
    }

    // ----------------------
    // CVC
    // ----------------------

    @Test
    @DisplayName("19. Короткий CVC")
    void shouldErrorShortCVC() {
        fillAndSubmitPayment(DataGenerator.cardWithCvc("12"));
        paymentPage.shouldSeeCvcFieldError("CVC должен содержать 3 цифры");
    }

    @Test
    @DisplayName("20. Буквы в CVC")
    void shouldErrorLettersCVC() {
        fillAndSubmitPayment(DataGenerator.cardWithCvc("abc"));
        paymentPage.shouldSeeCvcFieldError("CVC должен содержать 3 цифры");
    }

    @Test
    @DisplayName("21. Пустой CVC")
    void shouldErrorEmptyCVC() {
        fillAndSubmitPayment(DataGenerator.cardWithCvc(""));
        paymentPage.shouldSeeCvcFieldError("Поле обязательно для заполнения");
    }

    // ----------------------
    // ВЛАДЕЛЕЦ КАРТЫ
    // ----------------------

    @Test
    @DisplayName("22. Пустой владелец")
    void shouldErrorEmptyOwner() {
        fillAndSubmitPayment(DataGenerator.cardWithOwner(""));
        paymentPage.shouldSeeOwnerFieldError("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("23. Кириллица")
    void shouldErrorCyrillicOwner() {
        var approved = DataGenerator.approvedCard();
        fillAndSubmitPayment(new DataGenerator.CardData(
                approved.getNumber(),
                approved.getMonth(),
                approved.getYear(),
                DataGenerator.cyrillicOwner(),
                approved.getCvc()
        ));
        paymentPage.shouldSeeOwnerFieldError("Имя может содержать только латинские буквы");
    }

    @Test
    @DisplayName("24. Спецсимволы")
    void shouldErrorSpecialSymbolsOwner() {
        var approved = DataGenerator.approvedCard();
        fillAndSubmitPayment(new DataGenerator.CardData(
                approved.getNumber(),
                approved.getMonth(),
                approved.getYear(),
                DataGenerator.specialOwner(),
                approved.getCvc()
        ));
        paymentPage.shouldSeeOwnerFieldError("Имя может содержать только буквы");
    }

    @Test
    @DisplayName("25. Короткое имя")
    void shouldErrorTooShortOwner() {
        fillAndSubmitPayment(DataGenerator.cardWithOwner("A"));
        paymentPage.shouldSeeOwnerFieldError("Имя слишком короткое");
    }

    @Test
    @DisplayName("26. Длинное имя")
    void shouldErrorTooLongOwner() {
        var approved = DataGenerator.approvedCard();
        fillAndSubmitPayment(new DataGenerator.CardData(
                approved.getNumber(),
                approved.getMonth(),
                approved.getYear(),
                DataGenerator.longOwner(), // длинное имя
                approved.getCvc()
        ));
        paymentPage.shouldSeeOwnerFieldError("Имя слишком длинное");
    }

    // ----------------------
    // Состояние клавиши "Продолжить" после нажатия
    // ----------------------

    @Test
    @DisplayName("27. Кнопка блокируется и показывает спиннер при отправке")
    void shouldShowLoadingStateWhileSubmitting() {
        fillAndSubmitPayment(DataGenerator.approvedCard());
        paymentPage.shouldShowLoadingState();
    }
}