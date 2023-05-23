package ru.yandex_praktikum.clients;

import io.restassured.response.ValidatableResponse;
import ru.yandex_praktikum.pojo.CreateCourierRequest;
import ru.yandex_praktikum.pojo.CreateOrderRequest;
import ru.yandex_praktikum.pojo.LoginCourierRequest;

import static io.restassured.RestAssured.given;

public class CourierClient extends BaseClient {
    public ValidatableResponse create(CreateCourierRequest createCourierRequest) {
        return given()
                .spec(getSpec())
                .body(createCourierRequest)
                .when()
                .post("/api/v1/courier")
                .then();
    }

    public ValidatableResponse login(LoginCourierRequest loginCourierRequest) {
        return given()
                .spec(getSpec())
                .body(loginCourierRequest)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }

    public ValidatableResponse delete(int id) {
        return given()
                .spec(getSpec())
                .pathParam("id", id)
                .when()
                .delete("/api/v1/courier/{id}")
                .then();
    }

    public ValidatableResponse order(CreateOrderRequest createOrderRequest) {
        return given()
                .spec(getSpec())
                .body(createOrderRequest)
                .when()
                .post("/api/v1/orders")
                .then();
    }

    public ValidatableResponse cancel(int track) {
        return given()
                .spec(getSpec())
                .when()
                .queryParam("track", track)
                .put("/api/v1/orders/cancel")
                .then();
    }

    public ValidatableResponse listOrder() {
        return given()
                .spec(getSpec())
                .when()
                .get("/api/v1/orders")
                .then();
    }
}
