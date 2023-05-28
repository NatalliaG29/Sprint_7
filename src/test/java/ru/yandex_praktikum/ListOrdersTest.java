package ru.yandex_praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex_praktikum.clients.CourierClient;

import static org.hamcrest.Matchers.greaterThan;

public class ListOrdersTest {
    private ValidatableResponse response;
    private CourierClient courierClient = new CourierClient();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверки: 1. В тело ответа возвращается список заказов.")
    public void createList() {
        response = courierClient.listOrder();
        cod201();
        getListOrders();
    }

    @Step("Проверяем что код ответа 200")
    public void cod201(){
        response.statusCode(200);
    }
    @Step("Проверяем что количество заказов больше 0")
    public void getListOrders(){
        response.body("orders.size()", greaterThan(0));
    }
}
