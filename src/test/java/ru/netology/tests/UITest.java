package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.pages.CreditPage;
import ru.netology.pages.MainPage;
import ru.netology.pages.PaymentPage;
import ru.netology.data.SQL;

import static com.codeborne.selenide.Selenide.open;

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

    @Step("Открыть форму оплаты картой")
    void openPayment() {
        var mainPage = new MainPage();
        paymentPage = mainPage.clickButtonPay();
        paymentPage.verifyPageVisible();
    }

    @Step("Открыть форму оплаты кредитом")
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
    }

    @Test
    @Story("Позитивные сценарии")
    @DisplayName("2. Успешный кредит APPROVED картой")
    void shouldPayCreditWithApprovedCard() {
        var data = DataGenerator.approvedCard();
        fillAndSubmitCredit(data);
        creditPage.shouldSeeSuccess();
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
    }

    @Test
    @DisplayName("4. Отказ в кредите DECLINED картой")
    void shouldDeclineCreditWithDeclinedCard() {
        var data = DataGenerator.declinedCard();
        fillAndSubmitCredit(data);
        creditPage.shouldSeeError();
    }

    // ----------------------
    // НЕВЕРНЫЕ НОМЕРА КАРТ
    // ----------------------

    @Test
    @DisplayName("5. Карта с невалидным номером отклоняется банком")
    void shouldErrorInvalidCardNumber() {
        var data = new DataGenerator.CardData("1234 5678 9012 3450",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeError();
    }

    @Test
    @DisplayName("6. Номер карты с пробелами")
    void shouldAcceptCardNumberWithSpaces() {
        var data = new DataGenerator.CardData(" 1111 2222 3333 4444 ",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeSuccess();
    }

    @Test
    @DisplayName("7. Спецсимволы в номере карты")
    void shouldErrorSpecialSymbolsCardNumber() {
        var data = new DataGenerator.CardData("#@# $%^ &*()",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCardField(), "Неверный формат");
    }

    @Test
    @DisplayName("8. Короткий номер карты")
    void shouldErrorShortCardNumber() {
        var data = new DataGenerator.CardData("1234 5678 9012",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCardField(), "Номер карты должен содержать 16 цифр");
    }

    @Test
    @DisplayName("9. Длинный номер карты")
    void shouldErrorLongCardNumber() {
        var data = new DataGenerator.CardData("1234 5678 9012 3456 7890",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCardField(), "Номер карты должен содержать 16 цифр");
    }

    @Test
    @DisplayName("10. Пустой номер карты")
    void shouldErrorEmptyCardNumber() {
        var data = new DataGenerator.CardData("",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCardField(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("11. Ввод в поле номер карты значение placeholder")
    void shouldShowErrorWhenCardNumberIsEmpty() {
        var data = new DataGenerator.CardData("0000 0000 0000 0000",
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCardField(), "Неверный формат");
    }

    // ----------------------
    // МЕСЯЦ И ГОД
    // ----------------------

    @Test
    @DisplayName("12. Некорректный месяц (13)")
    void shouldErrorInvalidMonth() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD, "13",
                DataGenerator.validYear(), DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getMonthField(), "Месяц должен быть от 01 до 12");
    }

    @Test
    @DisplayName("13. Буквы в месяце")
    void shouldErrorLettersMonth() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD, "ab",
                DataGenerator.validYear(), DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getMonthField(), "Неверный формат");
    }

    @Test
    @DisplayName("14. Пустой месяц")
    void shouldErrorEmptyMonth() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD, "",
                DataGenerator.validYear(), DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getMonthField(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("15. Просроченный год")
    void shouldErrorExpiredYear() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.expiredYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getYearField(), "Истёк срок действия карты");
    }

    @Test
    @DisplayName("16. Слишком большой год")
    void shouldErrorTooFutureYear() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.futureYear(),
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getYearField(), "Неверно указан срок");
    }

    @Test
    @DisplayName("17. Буквы в годе")
    void shouldErrorLettersYear() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), "cd",
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getYearField(), "Неверный формат");
    }

    @Test
    @DisplayName("18. Пустой год")
    void shouldErrorEmptyYear() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), "",
                DataGenerator.owner(), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getYearField(), "Поле обязательно для заполнения");
    }

    // ----------------------
    // CVC
    // ----------------------

    @Test
    @DisplayName("19. Короткий CVC")
    void shouldErrorShortCVC() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), "12");
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCvcField(), "CVC должен содержать 3 цифры");
    }

    @Test
    @DisplayName("20. Буквы в CVC")
    void shouldErrorLettersCVC() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), "abc");
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCvcField(), "CVC должен содержать 3 цифры");
    }

    @Test
    @DisplayName("21. Пустой CVC")
    void shouldErrorEmptyCVC() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                DataGenerator.owner(), "");
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getCvcField(), "Поле обязательно для заполнения");
    }

    // ----------------------
    // ВЛАДЕЛЕЦ КАРТЫ
    // ----------------------

    @Test
    @DisplayName("22. Пустой владелец карты")
    void shouldErrorEmptyOwner() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                "", DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getOwnerField(), "Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("23. Буквы кириллицей в имени")
    void shouldErrorCyrillicOwner() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                "ИВАН ПЕТРОВ", DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getOwnerField(), "Имя может содержать только латинские буквы");
    }

    @Test
    @DisplayName("24. Спецсимволы в имени")
    void shouldErrorSpecialSymbolsOwner() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                "IVAN123!@", DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getOwnerField(), "Имя может содержать только буквы");
    }

    @Test
    @DisplayName("25. Очень короткое имя владельца")
    void shouldErrorTooShortOwner() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                "A", DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getOwnerField(), "Имя слишком короткое");
    }

    @Test
    @DisplayName("26. Очень длинное имя владельца")
    void shouldErrorTooLongOwner() {
        var data = new DataGenerator.CardData(DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(), DataGenerator.validYear(),
                "A".repeat(51), DataGenerator.validCvc());
        fillAndSubmitPayment(data);
        paymentPage.shouldSeeFieldError(paymentPage.getOwnerField(), "Имя слишком длинное");
    }

    // ----------------------
    // Состояние клавиши "Продолжить" после нажатия
    // ----------------------

    @Test
    @DisplayName("27. Кнопка блокируется и показывает спиннер при отправке")
    void shouldShowLoadingStateWhileSubmitting() {
        var data = DataGenerator.approvedCard();
        openPayment();
        paymentPage.fillCardForm(data);
        paymentPage.submit();
        paymentPage.shouldShowLoadingState();
    }
}