import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {
    private Faker faker;

    public String deliveryDateInDays(int daysToDelivery) {
        return LocalDate.now().plusDays(daysToDelivery)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String deliveryDateInMonths(int monthsToDelivery) {
        return LocalDate.now().plusMonths(monthsToDelivery)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String deliveryDateInYears(int yearsToDelivery) {
        return LocalDate.now().plusYears(yearsToDelivery).
                format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
        faker = new Faker(new Locale("ru"));
    }

    public String name() {
        return faker.name().fullName();
    }

    public String phone() {
        return faker.phoneNumber().phoneNumber();
    }


    @Test
    public void happyPathTest() {
        $("[data-test-id='city'] input").setValue("Москва");
        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDateInDays(3));
        $("[data-test-id='name'] input").setValue(name());
        $("[data-test-id='phone'] input").setValue(phone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + deliveryDateInDays(3)));

        $("[data-test-id='date'] input").doubleClick().sendKeys(deliveryDateInDays(4));
        $(withText("Запланировать")).click();
        $("[data-test-id='replan-notification']").should(appear);
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(withText("Перепланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + deliveryDateInDays(4)));
    }


}
