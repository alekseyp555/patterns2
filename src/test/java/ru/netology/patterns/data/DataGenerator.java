package ru.netology.patterns.data;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import java.util.Locale;

import static io.restassured.RestAssured.given;

// Класс для генерации случайных данных и отправки HTTP-запросов
public class DataGenerator {

    // Спецификация запроса, используемая для настройки базовых свойств запросов
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")                      // Базовый URL
            .setPort(9999)                                      // Порт, на котором запущен сервис
            .setAccept(ContentType.JSON)                        // Тип контента, который будем получать
            .setContentType(ContentType.JSON)                   // Формат тела запроса
            .log(LogDetail.ALL)                                 // Логирование всех деталей запроса
            .build();                                           // Сборка спецификации

    // Экземпляр библиотеки Faker для генерации случайных данных
    private static final Faker faker = new Faker(new Locale("ru")); // Настроено на русский язык

    // Приватный конструктор предотвращает создание экземпляров класса DataGenerator
    private DataGenerator() {
    }

    // Метод для отправки POST-запроса на регистрацию пользователя
    private static void sendRequest(RegistrationDto user) {
        given() // Дано
                .spec(requestSpec) // Используем нашу настроенную спецификацию запроса
                .body(new Gson().toJson(user)) // Преобразование объекта в JSON и передача в теле запроса
                .when() // Когда
                .post("/api/system/users") // Отправляем запрос на указанный endpoint
                .then() // Тогда
                .statusCode(200); // Ожидаем, что запрос завершится успешно (код 200)
    }

    // Метод для генерации случайного логина пользователя
    public static String getRandomLogin() {
        // Использование библиотеки Faker для генерации случайного логина
        return faker.name().username();
    }

    // Метод для генерации случайного пароля пользователя
    public static String getRandomPassword() {
        // Использование библиотеки Faker для генерации случайного пароля
        return faker.internet().password();
    }

    // Класс для регистрации пользователя
    public static class Registration {
        private Registration() {
        } // Закрыт конструктор для предотвращения создания экземпляров

        // Метод для генерации случайного пользователя с заданным статусом
        public static RegistrationDto getUser(String status) {
            // Создание объекта пользователя с помощью методов getRandomLogin() и getRandomPassword(),
            // статус передается параметром
            return new RegistrationDto(getRandomLogin(), getRandomPassword(), status);
        }

        // Метод для регистрации пользователя и возврата зарегистрированных данных
        public static RegistrationDto getRegisteredUser(String status) {
            // Генерация пользователя с заданным статусом
            RegistrationDto registeredUser = getUser(status);
            // Отправка запроса на регистрацию пользователя
            sendRequest(registeredUser);
            return registeredUser;
        }
    }

    // Класc данных для представления пользователя (логин, пароль, статус)
    @Value
    public static class RegistrationDto {
        String login;     // Логин пользователя
        String password;  // Пароль пользователя
        String status;    // Статус пользователя (например, 'active', 'blocked')
    }
}