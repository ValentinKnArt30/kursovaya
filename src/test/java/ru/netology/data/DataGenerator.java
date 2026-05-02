package ru.netology.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Locale;

public class DataGenerator {

    private static final Faker faker = new Faker(new Locale("en"));

    public static final String APPROVED_CARD = "1111 2222 3333 4444";
    public static final String DECLINED_CARD = "5555 6666 7777 8888";

    public static String validMonth() {
        return String.format("%02d", LocalDate.now().getMonthValue());
    }

    public static String validYear() {
        return String.format("%02d", LocalDate.now().plusYears(1).getYear() % 100);
    }

    public static String expiredYear() {
        return String.format("%02d", LocalDate.now().minusYears(1).getYear() % 100);
    }

    public static String futureYear() {
        return String.format("%02d", LocalDate.now().plusYears(7).getYear() % 100);
    }

    public static String validCvc() {
        return String.format("%03d", faker.number().numberBetween(100, 999));
    }

    public static String owner() {
        return faker.name().firstName().toUpperCase(Locale.ROOT)
                + " "
                + faker.name().lastName().toUpperCase(Locale.ROOT);
    }

    public static class CardData {
        public final String number;
        public final String month;
        public final String year;
        public final String owner;
        public final String cvc;

        public CardData(String number, String month, String year, String owner, String cvc) {
            this.number = number;
            this.month = month;
            this.year = year;
            this.owner = owner;
            this.cvc = cvc;
        }

        @Override
        public String toString() {
            return "CardData{" +
                    "number='" + number + '\'' +
                    ", month='" + month + '\'' +
                    ", year='" + year + '\'' +
                    ", owner='" + owner + '\'' +
                    ", cvc='" + cvc + '\'' +
                    '}';
        }
    }

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
}