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

import static org.hamcrest.Matchers.notNullValue;

public class LoginTest {

    private CourierClient courierClient = new CourierClient();
    private Integer id;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @DisplayName("Авторизация курьера")
    @Description("Проверки: 1. Курьер может авторизоваться; 2. Успешный запрос возвращает id; 3. Для авторизации нужно передать все обязательные поля.")
    public void loginCourier() {
        CreateCourierRequest createCourierRequest = CourierProvider.getRandomCourierRequest();

        //создание курьера
        courierClient.create(createCourierRequest)
                .statusCode(201)
                .body("ok", Matchers.equalTo(true));

        //логин курьера
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.login(loginCourierRequest)
                .statusCode(200)
                .body("id", notNullValue())
                .extract().jsonPath().get("id");
    }

    @Test
    @DisplayName("Проверка на авторизацию без логина")
    @Description("Проверки: 1. Система вернёт ошибку, если неправильно указать логин или пароль; 2. Если какого-то поля нет, запрос возвращает ошибку.")
    public void checkLoginCourierWithInvalidLogin() {
        CreateCourierRequest createCourierRequest = CourierProvider.getRandomCourierRequest();

        //создание курьера
        courierClient.create(createCourierRequest)
                .statusCode(201)
                .body("ok", Matchers.equalTo(true));

        //логин курьера
        LoginCourierRequest request = new LoginCourierRequest("",createCourierRequest.getPassword());
        courierClient.login(request)
                .statusCode(400)
                .body("message", Matchers.equalTo("Недостаточно данных для входа"));

        //логин курьера что бы узнать id и удалить в конце
        LoginCourierRequest loginCourierRequest = LoginCourierRequest.from(createCourierRequest);
        id = courierClient.login(loginCourierRequest)
                .statusCode(200)
                .body("id", notNullValue())
                .extract().jsonPath().get("id");
    }

    @Test
    @DisplayName("Проверка на авторизацию с несуществующей парой логин-пароль")
    @Description("Проверки: 1. Eсли авторизоваться под несуществующим пользователем, запрос возвращает ошибку.")
    public void checkLoginCourierWithInvalidLoginPassword() {

        //логин курьера под несуществующей парой логин-пароль
        LoginCourierRequest request = new LoginCourierRequest("1111", "1234");
        courierClient.login(request)
                .statusCode(404)
                .body("message", Matchers.equalTo("Учетная запись не найдена"));
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
