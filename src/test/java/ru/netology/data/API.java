package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class API {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .build();

    public static String sendPayRequest(DataGenerator.CardData data) {
        return given()
                .spec(requestSpec)
                .body(data)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .extract()
                .path("status");
    }

    public static void sendPayRequestBadRequest(DataGenerator.CardData data) {
        given()
                .spec(requestSpec)
                .body(data)
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);
    }

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