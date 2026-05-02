package ru.netology.data;

public class API {

    public static DataGenerator.CardData getApiApprovedCard() {
        return DataGenerator.approvedCard();
    }

    public static DataGenerator.CardData getApiDeclinedCard() {
        return DataGenerator.declinedCard();
    }

    public static DataGenerator.CardData getApiEmptyNumberCard() {
        return new DataGenerator.CardData(
                "",
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        );
    }

    public static DataGenerator.CardData getApiEmptyMonthCard() {
        return new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                "",
                DataGenerator.validYear(),
                DataGenerator.owner(),
                DataGenerator.validCvc()
        );
    }

    public static DataGenerator.CardData getApiEmptyYearCard() {
        return new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                "",
                DataGenerator.owner(),
                DataGenerator.validCvc()
        );
    }

    public static DataGenerator.CardData getApiEmptyOwnerCard() {
        return new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                "",
                DataGenerator.validCvc()
        );
    }

    public static DataGenerator.CardData getApiEmptyCVCCard() {
        return new DataGenerator.CardData(
                DataGenerator.APPROVED_CARD,
                DataGenerator.validMonth(),
                DataGenerator.validYear(),
                DataGenerator.owner(),
                ""
        );
    }
}