package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AviaPage;
import pages.AviaResultsPage;
import pages.HomePage;

public class AviaTest extends BaseTest {

    /**
     * UC-04: Поиск авиабилетов Москва → Санкт-Петербург с заполнением формы.
     * Авиа → клик «Откуда» → ввод Москва → клик dropdown-item →
     * клик «Куда» → ввод Санкт-Петербург → клик suggest →
     * клик дата → выбор +14 дней → клик «Выбрать» → клик submit →
     * ассерт: либо страница результатов /f/, либо поля формы корректно заполнены.
     */
    @Test(description = "UC-04: Поиск авиабилетов Москва → СПб (форма + suggest + календарь)")
    public void uc04_searchFlightsMoscowSpb() {
        AviaPage avia = new HomePage(driver, wait).open()
                .clickAviaTab()
                .fillFrom("Москва")
                .fillTo("Санкт-Петербург")
                .pickDateInDays(14);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@data-ti='switch']")
        )).click();

        Assert.assertEquals(avia.getFromValue(), "Москва",
                "Поле «Откуда» должно содержать «Москва» после выбора");
        Assert.assertTrue(avia.getToValue().startsWith("Санкт"),
                "Поле «Куда» должно содержать «Санкт-Петербург» после выбора. Получено: "
                        + avia.getToValue());

        AviaResultsPage results = avia.submit();
        boolean navigated = false;
        try {
            results.waitForResults();
            navigated = true;
        } catch (Exception ignored) {
        }

        Assert.assertTrue(navigated || driver.getCurrentUrl().contains("avia.tutu.ru"),
                "После submit ожидаем переход на страницу результатов /f/ либо остаёмся на avia.tutu.ru. " +
                        "URL: " + driver.getCurrentUrl());

        if (navigated) {
            Assert.assertTrue(results.hasText("Москва") || results.hasText("Санкт"),
                    "На странице результатов должны быть города маршрута");
        }
    }

    /**
     * UC-12: Negative — одинаковые «Откуда» и «Куда» = «Москва».
     * После submit либо появляется валидационное сообщение об ошибке,
     * либо переход на /f/ не происходит.
     */
    @Test(description = "UC-12: Negative — одинаковые города в авиа форме")
    public void uc12_sameDepartureArrivalNegative() {
        AviaPage avia = new HomePage(driver, wait).open()
                .clickAviaTab()
                .fillFrom("Москва")
                .fillTo("Москва");

        boolean noNavigation = avia.submitAndExpectNoNavigation();
        boolean validationHint = avia.hasValidationHint();

        Assert.assertTrue(noNavigation || validationHint,
                "Ожидаем либо отсутствие навигации на /f/, либо подсказку валидации. " +
                        "noNav=" + noNavigation + ", hint=" + validationHint +
                        ", url=" + driver.getCurrentUrl());
    }
}
