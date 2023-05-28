package ru.yandex_praktikum.dataprovider;

import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex_praktikum.pojo.CreateCourierRequest;

public class CourierProvider {
    public static CreateCourierRequest getRandomCourierRequest(){
        CreateCourierRequest createCourierRequest = new CreateCourierRequest();
        createCourierRequest.setLogin(RandomStringUtils.randomAlphabetic(4));
        createCourierRequest.setPassword(RandomStringUtils.randomAlphabetic(3));
        createCourierRequest.setFirstName(RandomStringUtils.randomAlphabetic(5));
        return createCourierRequest;
    }
}
