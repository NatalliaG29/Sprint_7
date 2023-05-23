package ru.yandex_praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
    private Integer track;
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

        //создание заказа
        track = courierClient.order(request)
                .statusCode(201)
                .body("track", notNullValue())
                .extract().jsonPath().get("track");
    }
    @After
    //отмена заказа
    public void tearDown(){
        if (track != null){
            courierClient.cancel(track)
                    .statusCode(200);
        }
    }
}
