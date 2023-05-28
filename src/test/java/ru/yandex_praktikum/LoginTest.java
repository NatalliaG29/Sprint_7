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

import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {

    private CourierClient courierClient = new CourierClient();
    private ValidatableResponse response;
    private CreateCourierRequest createCourierRequest = CourierProvider.getRandomCourierRequest();
    private LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Проверки: 1. Курьер может авторизоваться; 2. Успешный запрос возвращает id; 3. Для авторизации нужно передать все обязательные поля.")
    public void loginCourier() {
        response = courierClient.create(createCourierRequest);
        cod201();
        bodyOK();
        response = courierClient.login(loginCourierRequest);
        cod200();
        bodyID();
        getID();
    }

    @Test
    @DisplayName("Проверка на авторизацию без логина")
    @Description("Проверки: 1. Система вернёт ошибку, если неправильно указать логин или пароль; 2. Если какого-то поля нет, запрос возвращает ошибку.")
    public void checkLoginCourierWithInvalidLogin() {
        response = courierClient.create(createCourierRequest);
        cod201();
        bodyOK();
        LoginCourierRequest request = new LoginCourierRequest("",createCourierRequest.getPassword());
        response = courierClient.login(request);
        cod400();
        textInsufficientData();
        response = courierClient.login(loginCourierRequest);
        cod200();
        bodyID();
        getID();
    }

    @Test
    @DisplayName("Проверка на авторизацию с несуществующей парой логин-пароль")
    @Description("Проверки: 1. Eсли авторизоваться под несуществующим пользователем, запрос возвращает ошибку.")
    public void checkLoginCourierWithInvalidLoginPassword() {
        LoginCourierRequest request = new LoginCourierRequest("1111", "1234");
        response = courierClient.login(request);
        cod404();
        textNotFound();
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

    @Step("Проверяем что id приходит не пустое")
    public void bodyID(){
        response.body("id", notNullValue());
    }

    @Step("Получаем значение id")
    public void getID(){
        response.extract().jsonPath().get("id");
    }

    @Step("Проверяем что код ответа 400")
    public void cod400(){
        response.statusCode(400);
    }

    @Step("Проверяем что ответ возвращает [Недостаточно данных для входа]")
    public void textInsufficientData(){
                response.body("message", Matchers.equalTo("Недостаточно данных для входа"));
    }

    @Step("Проверяем что код ответа 404")
    public void cod404(){
        response.statusCode(404)
                .body("message", Matchers.equalTo("Учетная запись не найдена"));
    }

    @Step("Проверяем что текст ошибки [Учетная запись не найдена]")
    public void textNotFound(){
        response.body("message", Matchers.equalTo("Учетная запись не найдена"));
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
