package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class PaymentPage {

    private final SelenideElement form = $("form");

    // --- Проверка страницы ---
    public void verifyPageVisible() {
        $$("h3.heading.heading_size_m")
                .findBy(Condition.exactText("Оплата по карте"))
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    // --- Поля ---
    public SelenideElement cardNumberField() {
        return form.$("[placeholder='0000 0000 0000 0000']")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    public SelenideElement monthField() {
        return form.$("[placeholder='08']")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    public SelenideElement yearField() {
        return form.$("[placeholder='22']")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    public SelenideElement ownerField() {
        return form.$$("span.input__top")
                .findBy(Condition.text("Владелец"))
                .closest(".input")
                .$("[type='text']");
    }

    public SelenideElement cvcField() {
        return form.$("[placeholder='999']")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    // --- Кнопка ---
    private SelenideElement continueButton() {
        return $$("button")
                .findBy(Condition.text("Продолжить"))
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    // --- Состояние кнопки ---
    private SelenideElement loadingButton() {
        return $$("button.button")
                .findBy(Condition.text("Отправляем запрос в Банк..."))
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    // --- Уведомления ---
    private SelenideElement successNotification() {
        return $(".notification_status_ok")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    private SelenideElement errorNotification() {
        return $(".notification_status_error")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    // --- Действия ---
    public PaymentPage fillCardForm(DataGenerator.CardData data) {
        cardNumberField().setValue(data.number);
        monthField().setValue(data.month);
        yearField().setValue(data.year);
        ownerField().setValue(data.owner);
        cvcField().setValue(data.cvc);
        return this;
    }

    public PaymentPage submit() {
        continueButton().click();
        return this;
    }

    // --- Проверки ---
    public void shouldSeeSuccess() {
        SelenideElement success = $(".notification_status_ok")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        successNotification().$(".notification__title")
                .shouldHave(Condition.exactText("Успешно"));
        successNotification().$(".notification__content")
                .shouldHave(Condition.text("Операция одобрена Банком."));
    }

    public void shouldSeeError() {
        SelenideElement error = $(".notification_status_error")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
        errorNotification().$(".notification__title")
                .shouldHave(Condition.exactText("Ошибка"));
        errorNotification().$(".notification__content")
                .shouldHave(Condition.text("Ошибка! Банк отказал в проведении операции."));
    }

    public void shouldSeeFieldError(SelenideElement field, String message) {
        field.closest(".input")
                .$(".input__sub")
                .shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(Condition.text(message));
    }

    public void shouldShowLoadingState() {
        loadingButton().shouldBe(Condition.disabled);
        loadingButton().$(".spin").shouldBe(visible);
    }

    // --- Геттеры для полей ---
    public SelenideElement getCardField() {
        return cardNumberField();
    }

    public SelenideElement getMonthField() {
        return monthField();
    }

    public SelenideElement getYearField() {
        return yearField();
    }

    public SelenideElement getOwnerField() {
        return ownerField();
    }

    public SelenideElement getCvcField() {
        return cvcField();
    }
}