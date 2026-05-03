package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class PaymentPage {

    private final SelenideElement form = $("form");

    private final SelenideElement cardNumberField =
            form.$("[placeholder='0000 0000 0000 0000']");

    private final SelenideElement monthField =
            form.$("[placeholder='08']");

    private final SelenideElement yearField =
            form.$("[placeholder='22']");

    private final SelenideElement ownerField =
            form.$$("span.input__top")
                    .findBy(Condition.text("Владелец"))
                    .closest(".input")
                    .$("[type='text']");

    private final SelenideElement cvcField =
            form.$("[placeholder='999']");

    private final SelenideElement continueButton =
            $$("button").findBy(Condition.text("Продолжить"));

    private final SelenideElement loadingButton =
            $$("button.button")
                    .findBy(Condition.text("Отправляем запрос в Банк..."));

    private final SelenideElement successNotification =
            $(".notification_status_ok");

    private final SelenideElement errorNotification =
            $(".notification_status_error");

    // --- ПРОВЕРКА СТРАНИЦЫ ---
    public void verifyPageVisible() {
        $$("h3.heading.heading_size_m")
                .findBy(Condition.exactText("Оплата по карте"))
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    // --- ДЕЙСТВИЯ ---
    public PaymentPage fillCardForm(DataGenerator.CardData data) {
        cardNumberField.setValue(data.getNumber());
        monthField.setValue(data.getMonth());
        yearField.setValue(data.getYear());
        ownerField.setValue(data.getOwner());
        cvcField.setValue(data.getCvc());
        return this;
    }

    public PaymentPage submit() {
        continueButton.click();
        return this;
    }

    // --- ПРОВЕРКИ ---
    public void shouldSeeSuccess() {
        successNotification.shouldBe(visible, Duration.ofSeconds(15));

        successNotification.$(".notification__title")
                .shouldHave(Condition.exactText("Успешно"));

        successNotification.$(".notification__content")
                .shouldHave(Condition.text("Операция одобрена Банком."));
    }

    public void shouldSeeError() {
        errorNotification.shouldBe(visible, Duration.ofSeconds(15));

        errorNotification.$(".notification__title")
                .shouldHave(Condition.exactText("Ошибка"));

        errorNotification.$(".notification__content")
                .shouldHave(Condition.text("Ошибка! Банк отказал в проведении операции."));
    }

    public void shouldSeeFieldError(SelenideElement field, String message) {
        field.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }

    public void shouldShowLoadingState() {
        loadingButton.shouldBe(Condition.disabled);
        loadingButton.$(".spin").shouldBe(visible);
    }

    public void shouldSeeCardFieldError(String message) {
        cardNumberField.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }

    public void shouldSeeMonthFieldError(String message) {
        monthField.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }

    public void shouldSeeYearFieldError(String message) {
        yearField.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }

    public void shouldSeeCvcFieldError(String message) {
        cvcField.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }

    public void shouldSeeOwnerFieldError(String message) {
        ownerField.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }
}