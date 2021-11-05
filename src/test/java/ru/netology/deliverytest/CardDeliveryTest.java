package ru.netology.deliverytest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.userinfo.DataGenerator;


import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999");
    }


    @Test
    public void happyPath() {
        var validUser = DataGenerator.Registration.generateUser();
        var meetingDate = DataGenerator.generateDate(3);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").doubleClick().sendKeys(meetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + meetingDate));
    }

    @Test
    public void changingDeliveryDate() {
        var validUser = DataGenerator.Registration.generateUser();
        var firstMeetingDate = DataGenerator.generateDate(5);
        var secondMeetingDate = DataGenerator.generateDate(10);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").doubleClick().sendKeys(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $(withText("Запланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id='date'] input").doubleClick().sendKeys(secondMeetingDate);
        $(withText("Запланировать")).click();
        $("[data-test-id='replan-notification']").should(appear);
        $("[data-test-id='replan-notification'] .notification__content")
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $(withText("Перепланировать")).click();
        $("[data-test-id='success-notification'] [class='notification__content']").should(appear)
                .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
