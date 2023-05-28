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
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.COURIER_ENDPOINT)
                .then();
    }

    public ValidatableResponse login(LoginCourierRequest loginCourierRequest) {
        return given()
                .spec(getSpec())
                .body(loginCourierRequest)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.COURIER_LOGIN_ENDPOINT)
                .then();
    }

    public ValidatableResponse delete(int id) {
        return given()
                .spec(getSpec())
                .pathParam("id", id)
                .when()
                .delete(EndpointConstants.API_BASE_URI + EndpointConstants.COURIER_ENDPOINT + "/{id}")
                .then();
    }

    public ValidatableResponse order(CreateOrderRequest createOrderRequest) {
        return given()
                .spec(getSpec())
                .body(createOrderRequest)
                .when()
                .post(EndpointConstants.API_BASE_URI + EndpointConstants.ORDERS_ENDPOINT)
                .then();
    }

    public ValidatableResponse cancel(int track) {
        return given()
                .spec(getSpec())
                .when()
                .queryParam("track", track)
                .put(EndpointConstants.API_BASE_URI + EndpointConstants.ORDERS_CANCEL_ENDPOINT)
                .then();
    }

    public ValidatableResponse listOrder() {
        return given()
                .spec(getSpec())
                .when()
                .get(EndpointConstants.API_BASE_URI + EndpointConstants.ORDERS_ENDPOINT)
                .then();
    }
}
