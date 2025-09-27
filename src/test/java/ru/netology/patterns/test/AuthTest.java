package ru.netology.patterns.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static ru.netology.patterns.data.DataGenerator.Registration.getRegisteredUser;
import static ru.netology.patterns.data.DataGenerator.Registration.getUser;


// Класс тестов аутентификации (авторизации)
class AuthTest {

    // Перед началом всех тестов настройка интеграционного инструмента Selenide с Allure Selenide.
    // Эта конфигурация позволит записывать скриншоты и сохранять HTML страницы, что удобно для анализа ошибок.
    @BeforeAll
    static void setupAllureReports() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()); // Включаем отчетность в Allure

        // Можно настроить дополнительные опции (например, отключение скриншотов и включение сохранения HTML-страницы)
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(false) // Отключаем скриншоты
                .savePageSource(true) // Сохраняем HTML страницы
        );
    }

    // После завершения всех тестов удаляем слушатель Selenide Logger
    @AfterAll
    static void tearDownAllureReports() {
        SelenideLogger.removeListener("AllureSelenide");
    }

    // Выполняется перед каждым тестом, открывается страница авторизации
    @BeforeEach
    void setup() {
        open("http://localhost:9999"); // Переходим на нужную страницу
    }

    // Первый тест: Вход в систему активным зарегистрированным пользователем
    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        // Зарегистрируем пользователя со статусом "active"
        var registeredUser = getRegisteredUser("active");

        // Заполняем поля формы авторизации данными зарегистрированного пользователя
        $("[data-test-id='login'] .input__box .input__control").val(registeredUser.getLogin());
        $("[data-test-id='password'] .input__box .input__control").val(registeredUser.getPassword());

        // Кликаем на кнопку "Войти"
        $("[data-test-id='action-login']").click();

        // Проверяем, что после входа видим надпись "Личный кабинет"
        $("h2").shouldHave(exactText(" Личный кабинет"));

        // TO DO: Далее добавьте логику, которая обеспечит дальнейшую обработку после удачного входа.
    }

    // Второй тест: Попытка войти незарегистрированным пользователем должна вызвать ошибку
    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        // Создаем пользователя с произвольными данными, но не регистрируем его
        var notRegisteredUser = getUser("active");

        // Заполняем поля формы авторизации данными несуществующего пользователя
        $("[data-test-id='login'] .input__box .input__control").val(notRegisteredUser.getLogin());
        $("[data-test-id='password'] .input__box .input__control").val(notRegisteredUser.getPassword());

        // Нажимаем кнопку "Войти"
        $("[data-test-id='action-login']").click();

        // Проверяем, что появляется ошибка "Неверно указан логин или пароль"
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(exactText("Ошибка! Неверно указан логин или пароль"));

        // TO DO: Дополните тест необходимой дополнительной обработкой после неудачной попытки входа.
    }

    // Третий тест: Попытка войти заблокированным пользователем должна вызвать ошибку
    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        // Регистрируем пользователя со статусом "blocked"
        var blockedUser = getRegisteredUser("blocked");

        // Заполняем поля формы авторизации данными заблокированного пользователя
        $("[data-test-id='login'] .input__box .input__control").val(blockedUser.getLogin());
        $("[data-test-id='password'] .input__box .input__control").val(blockedUser.getPassword());

        // Нажимаем кнопку "Войти"
        $("[data-test-id='action-login']").click();

        // Проверяем, что появляется ошибка "Пользователь заблокирован"
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(exactText("Ошибка! Пользователь заблокирован"));

        // TO DO: Если нужно, дополните тест дополнительными действиями после неудачи входа.
    }
}