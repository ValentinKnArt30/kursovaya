package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {

    private static final Faker faker = new Faker(new Locale("en"));

    public static final String APPROVED_CARD = "1111 2222 3333 4444";
    public static final String DECLINED_CARD = "5555 6666 7777 8888";

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yy");

    public static String validMonth() {
        return LocalDate.now().format(MONTH_FORMAT);
    }

    public static String validYear() {
        return LocalDate.now().plusYears(1).format(YEAR_FORMAT);
    }

    public static String expiredYear() {
        return LocalDate.now().minusYears(1).format(YEAR_FORMAT);
    }

    public static String futureYear() {
        return LocalDate.now().plusYears(7).format(YEAR_FORMAT);
    }

    public static String validCvc() {
        return String.format("%03d", faker.number().numberBetween(100, 999));
    }

    public static String owner() {
        return faker.name().firstName().toUpperCase(Locale.ROOT)
                + " "
                + faker.name().lastName().toUpperCase(Locale.ROOT);
    }

    @Value
    public static class CardData {
        String number;
        String month;
        String year;
        String owner;
        String cvc;
    }

    // Позитивные сценарии

    public static CardData approvedCard() {
        return new CardData(
                APPROVED_CARD,
                validMonth(),
                validYear(),
                owner(),
                validCvc()
        );
    }

    public static CardData declinedCard() {
        return new CardData(
                DECLINED_CARD,
                validMonth(),
                validYear(),
                owner(),
                validCvc()
        );
    }

    // Негативные сценарии

    public static String invalidCardNumber() {
        return faker.number().digits(16);
    }

    public static String shortCardNumber() {
        return faker.number().digits(12);
    }

    public static String longCardNumber() {
        return faker.number().digits(20);
    }

    public static String empty() {
        return "";
    }

    public static String spacesCardNumber() {
        return " 1111 2222 3333 4444 ";
    }

    public static String specialSymbolsCardNumber() {
        return "#@# $%^ &*()";
    }

    public static String cyrillicOwner() {
        return "ИВАН ПЕТРОВ";
    }

    public static String specialOwner() {
        return "IVAN123!@";
    }

    public static String shortOwner() {
        return "A";
    }

    public static String longOwner() {
        return "A".repeat(51);
    }

    public static String emptyOwner() {
        return "";
    }

    public static CardData cardWithMonth(String month) {
        return new CardData(
                APPROVED_CARD,
                month,
                validYear(),
                owner(),
                validCvc()
        );
    }

    public static CardData cardWithYear(String year) {
        return new CardData(
                APPROVED_CARD,
                validMonth(),
                year,
                owner(),
                validCvc()
        );
    }

    public static CardData cardWithCvc(String cvc) {
        return new CardData(
                APPROVED_CARD,
                validMonth(),
                validYear(),
                owner(),
                cvc
        );
    }

    public static CardData cardWithOwner(String owner) {
        return new CardData(
                APPROVED_CARD,
                validMonth(),
                validYear(),
                owner,
                validCvc()
        );
    }

    public static CardData cardWithSpaces() {
        return new CardData(
                " 1111 2222 3333 4444 ",
                validMonth(),
                validYear(),
                owner(),
                validCvc()
        );
    }

    public static CardData cardWithSymbols() {
        return new CardData(
                "#@# $%^ &*()",
                validMonth(),
                validYear(),
                owner(),
                validCvc()
        );
    }

    public static CardData placeholderCard() {
        return new CardData(
                "0000 0000 0000 0000",
                validMonth(),
                validYear(),
                owner(),
                validCvc()
        );
    }
}