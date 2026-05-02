package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    private final SelenideElement buttonPay =
            $$("button").findBy(Condition.exactText("Купить"));

    private final SelenideElement buttonPayCredit =
            $$("button").findBy(Condition.exactText("Купить в кредит"));

    public PaymentPage clickButtonPay() {
        buttonPay.click();
        return new PaymentPage();
    }

    public CreditPage clickButtonCredit() {
        buttonPayCredit.click();
        return new CreditPage();
    }
}