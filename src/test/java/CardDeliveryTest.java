import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {
    private Faker faker = new Faker(new Locale("ru"));

    public String cityGenerate() {
        String[] cities = {"Казань", "Нижний Новгород", "Москва", "Санкт-Петербург", "Новосибирск", "Иваново"};
        Random random = new Random();
        int index = random.nextInt(cities.length);
        String randomCity = cities[index];
        return randomCity;
    }

    public String deliveryDate(int daysToDelivery) {
        return LocalDate.now().plusDays(daysToDelivery)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String name() {
        return faker.name().fullName().replace("ё", "е");
    }

    public String phone() {
        return faker.phoneNumber().phoneNumber();
    }


    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
    }


    @Test
    public void happyPath() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(3));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue(phone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + deliveryDate(3)));
    }

    @Test
    public void changingDeliveryDate() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(3));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue(phone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + deliveryDate(3)));
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(4));
        $(withText("Запланировать")).click();
        $("[data-test-id='replan-notification']").should(appear);
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(withText("Перепланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + deliveryDate(4)));
    }

    @Test
    public void allFieldsAreEmpty() {
        $("[data-test-id='city'] input").setValue("");
        $("[data-test-id='date'] input").doubleClick().sendKeys(" ");
        $(withText("Мы гарантируем безопасность ваших данных")).click(); //для того чтобы скрыть календарь
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("");
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Поле обязательно для заполнения")).should(appear);
    }

    @Test
    public void allValuesAreWrong() {
        $("[data-test-id='city'] input").setValue("New York");
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(2));
        $("[data-test-id='name'] input").setValue("Ivanov Ivan");
        $("[data-test-id='phone'] input").setValue("");
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Доставка в выбранный город недоступна")).should(appear);
    }

    @Test
    public void emptyCityField() {
        $("[data-test-id='city'] input").setValue("");
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue(phone());
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Поле обязательно для заполнения")).should(appear);
    }

    @Test
    public void notAllowedCity() {
        $("[data-test-id='city'] input").setValue("Реутов");
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue(phone());
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Доставка в выбранный город недоступна")).should(appear);
    }

    @Test
    public void emptyNameField() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue(phone());
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Поле обязательно для заполнения")).should(appear);
    }

    @Test
    public void enteringNameInEnglish() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue("Ivanov Ivan");
        $("[data-test-id='phone'] input").setValue(phone());
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."))
                .should(appear);
    }

    @Test
    public void enteringNameUsingNumbers() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue("123456");
        $("[data-test-id='phone'] input").setValue(phone());
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."))
                .should(appear);
    }

    @Test
    public void enteringNameUsingSpecialChars() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue("%#$@%");
        $("[data-test-id='phone'] input").setValue(phone());
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."))
                .should(appear);
    }

    @Test
    public void emptyTelephoneField() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue("");
        $(".checkbox__box").click();
        $(byText("Запланировать")).click();
        $(withText("Поле обязательно для заполнения")).should(appear);
    }

    @Test
    public void orderWithNoCheckbox() {
        $("[data-test-id='city'] input").setValue(cityGenerate());
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDate(5));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue(phone());
        $(byText("Запланировать")).click();
        $("[class='checkbox checkbox_size_m checkbox_theme_alfa-on-white input_invalid']").shouldBe(visible);
    }
}
