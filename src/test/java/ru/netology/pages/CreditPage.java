package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class CreditPage {

    private final SelenideElement form = $("fieldset");

    // --- Проверка страницы ---
    public void verifyPageVisible() {
        $$("h3.heading.heading_size_m")
                .findBy(Condition.exactText("Кредит по данным карты"))
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
                .$("[type='text']")
                .shouldBe(visible, Duration.ofSeconds(10));
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
    public CreditPage fillCardForm(DataGenerator.CardData data) {
        cardNumberField().setValue(data.number);
        monthField().setValue(data.month);
        yearField().setValue(data.year);
        ownerField().setValue(data.owner);
        cvcField().setValue(data.cvc);
        return this;
    }

    public CreditPage submit() {
        continueButton().click();
        return this;
    }

    // --- Проверки ---
    public void shouldSeeSuccess() {
        SelenideElement success = successNotification();

        success.$(".notification__title")
                .shouldHave(Condition.exactText("Успешно"));
        success.$(".notification__content")
                .shouldHave(Condition.text("Операция одобрена Банком."));
    }

    public void shouldSeeError() {
        SelenideElement error = errorNotification();

        error.$(".notification__title")
                .shouldHave(Condition.exactText("Ошибка"));
        error.$(".notification__content")
                .shouldHave(Condition.text("Ошибка! Банк отказал в проведении операции."));
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