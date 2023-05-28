package ru.yandex_praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
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
    private ValidatableResponse response;
    private Integer id;
    private CreateCourierRequest createCourierRequest = CourierProvider.getRandomCourierRequest();
    private LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
    private CreateCourierRequest request = new CreateCourierRequest();

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Создание нового курьера")
    @Description("Проверки: 1. Курьера можно создать; 2. Запрос возвращает правильный код ответа; 3. Успешный запрос возвращает ok: true; 4. Чтобы создать курьера, нужно передать в ручку все обязательные поля.")
    public void createCourier() {
        response = courierClient.create(createCourierRequest);
        cod201();
        bodyOK();
        response = courierClient.login(loginCourierRequest);
        cod200();
        getID();
    }

    @Test
    @DisplayName("Проверка на создание дупликата курьера")
    @Description("Проверки: 1. Нельзя создать двух одинаковых курьеров; 2. Запрос возвращает правильный код ответа; 3. Если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void checkCreateDuplicateCourier() {
        response = courierClient.create(createCourierRequest);
        cod201();
        bodyOK();
        response = courierClient.login(loginCourierRequest);
        cod200();
        getID();
        response = courierClient.create(createCourierRequest);
        cod409();
        textDuplicateLogin();
    }

    @Test
    @DisplayName("Проверка на создание курьера без логина")
    @Description("Проверки: 1. Если одного из полей нет, запрос возвращает ошибку; 2. Запрос возвращает правильный код ответа.")
    public void checkCreateCourierWithoutLogin() {
        request.setLogin("");
        request.setPassword("1234");
        request.setFirstName("Xi");

        response = courierClient.create(request);
        cod400();
        textInsufficientDataLogin();
    }

    @After
    public void tearDown(){
        deleteCourier();
    }


    @Step("Проверяем что код ответа 201")
    public void cod201(){
        response.statusCode(201);
    }

    @Step("Проверяем что ответ возвращает [ok: true]")
    public void bodyOK() {
        response.body("ok", Matchers.equalTo(true));
    }

    @Step("Проверяем что код ответа 200")
    public void cod200(){
        response.statusCode(200);
    }

    @Step("Получаем значение id")
    public void getID(){
        response.extract().jsonPath().get("id");
    }

    @Step("Проверяем что код ответа 409")
    public void cod409(){
        response.statusCode(409);
    }

    @Step("Проверяем что ответ возвращает [Этот логин уже используется. Попробуйте другой.]")
    public void textDuplicateLogin(){
        response.body("message", Matchers.equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Проверяем что код ответа 409")
    public void cod400(){
        response.statusCode(400);
    }

    @Step("Проверяем что ответ возвращает [Недостаточно данных для создания учетной записи]")
    public void textInsufficientDataLogin(){
        response.body("message", Matchers.equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Удаление курьера")
    public void deleteCourier(){
        Integer idValue = response.extract().path("id");
        if (idValue != null){
            courierClient.delete(idValue)
                    .statusCode(200);
        }
    }
}
