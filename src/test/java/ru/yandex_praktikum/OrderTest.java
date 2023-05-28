package ru.yandex_praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex_praktikum.clients.CourierClient;
import ru.yandex_praktikum.pojo.CreateOrderRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderTest {
    private CourierClient courierClient = new CourierClient();
    private ValidatableResponse response;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Parameterized.Parameter(0)
    public List<String> colors;

    @Parameterized.Parameters
    public static Object[] getColorForOrder() {
        return new Object[]{
                Collections.singletonList("BLACK"),
                Collections.singletonList("GREY"),
                Arrays.asList("BLACK", "GREY"),
                Collections.emptyList()
        };
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Проверки: 1. Можно указать один из цветов — BLACK или GREY; 2. Можно указать оба цвета; 3. Можно совсем не указывать цвет;.")
    public void createOrder() {
        CreateOrderRequest request = new CreateOrderRequest("Mimi", "Uno", "Lywe, 13 apt.", 2, "+7 800 355 35 35", 3, "2023-06-01", "Spasibo", colors);
        response = courierClient.order(request);
        cod201();
        bodyTrack();
        getTrack();
    }

    @After
    public void tearDown() {
        cancelOrder();
    }

    @Step("Проверяем что код ответа 201")
    public void cod201(){
        response.statusCode(201);
    }

    @Step("Проверяем что track приходит не пустое")
    public void bodyTrack(){
        response.body("track", notNullValue());
    }

    @Step("Получаем значение track")
    public void getTrack() {
        response.extract().jsonPath().get("track");
    }

    @Step("Удаление курьера")
    public void cancelOrder(){
        Integer idValue = response.extract().path("track");
        if (idValue != null){
            courierClient.cancel(idValue)
                    .statusCode(200);
        }
    }
    }

