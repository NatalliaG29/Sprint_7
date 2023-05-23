package ru.yandex_praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.Before;
import org.junit.Test;
import ru.yandex_praktikum.clients.CourierClient;

import static org.hamcrest.Matchers.greaterThan;

public class ListOrdersTest {
    private CourierClient courierClient = new CourierClient();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверки: 1. В тело ответа возвращается список заказов.")
    public void createCourier() {

        //получение списка заказов
        courierClient.listOrder()
                .statusCode(200)
                .body("orders.size()", greaterThan(0));
    }
}
