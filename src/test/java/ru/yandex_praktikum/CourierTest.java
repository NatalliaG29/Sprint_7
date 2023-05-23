package ru.yandex_praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex_praktikum.clients.CourierClient;
import ru.yandex_praktikum.dataprovider.CourierProvider;
import ru.yandex_praktikum.pojo.CreateCourierRequest;
import ru.yandex_praktikum.pojo.LoginCourierRequest;

public class CourierTest {
    private CourierClient courierClient = new CourierClient();
    private Integer id;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Проверки: 1. Курьера можно создать; 2. Запрос возвращает правильный код ответа; 3. Успешный запрос возвращает ok: true; 4. Чтобы создать курьера, нужно передать в ручку все обязательные поля.")
    public void createCourier() {
        CreateCourierRequest createCourierRequest = CourierProvider.getRandomCourierRequest();

        //создание курьера
        courierClient.create(createCourierRequest)
            .statusCode(201)
            .body("ok", Matchers.equalTo(true));

        //логин для проверки что точно создали курьера
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.login(loginCourierRequest)
            .statusCode(200)
            .extract().jsonPath().get("id");
    }

    @Test
    @DisplayName("Проверка на создание дупликата курьера")
    @Description("Проверки: 1. Нельзя создать двух одинаковых курьеров; 2. Запрос возвращает правильный код ответа; 3. Если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void checkCreateDuplicateCourier() {
        CreateCourierRequest createCourierRequest = CourierProvider.getRandomCourierRequest();

        //создание курьера
        courierClient.create(createCourierRequest)
                .statusCode(201)
                .body("ok", Matchers.equalTo(true));

        //логин для проверки что точно создали курьера
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.login(loginCourierRequest)
                .statusCode(200)
                .extract().jsonPath().get("id");

        //повторное создание курьера с такими же данными
        courierClient.create(createCourierRequest)
                .statusCode(409)
                .body("message", Matchers.equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Проверка на создание курьера без пароля")
    @Description("Проверки: 1. Если одного из полей нет, запрос возвращает ошибку; 2. Запрос возвращает правильный код ответа.")
    public void checkCreateCourierWithoutLogin() {
        CreateCourierRequest request = new CreateCourierRequest();
        request.setLogin("");
        request.setPassword("1234");
        request.setFirstName("Xi");

        //создание курьера без пароля
        courierClient.create(request)
                .statusCode(400)
                .body("message", Matchers.equalTo("Недостаточно данных для создания учетной записи"));
    }

    @After
    //удалиение курьера
    public void tearDown(){
        if (id != null){
            courierClient.delete(id)
                    .statusCode(200);
        }
    }
}
