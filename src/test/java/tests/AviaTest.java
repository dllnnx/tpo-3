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

    @Test(description = "UC-04: поиск авиабилетов Москва -> СПб")
    public void uc04_searchFlightsMoscowSpb() {
        AviaPage avia = new HomePage(driver, wait).open()
                .clickAviaTab()
                .fillFrom("Москва")
                .fillTo("Санкт-Петербург")
                .pickDateInDays(14);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@data-ti='switch']")
        )).click();

        Assert.assertEquals(avia.getFromValue(), "Москва");
        Assert.assertEquals(avia.getToValue(), "Санкт-Петербург");

        AviaResultsPage results = avia.submit();
        results.waitForResults();

        Assert.assertTrue(results.hasAirlineName());
    }

    @Test(description = "UC-12: negative: одинаковые города в авиа форме")
    public void uc12_sameDepartureArrivalNegative() {
        AviaPage avia = new HomePage(driver, wait).open()
                .clickAviaTab()
                .fillFrom("Москва")
                .fillTo("Москва");

        boolean noNavigation = avia.submitAndExpectNoNavigation();
        boolean validationHint = avia.hasValidationHint();

        Assert.assertTrue(noNavigation || validationHint, "ожидаем либо отсутствие навигации, либо подсказку валидации " +
                "noNav=" + noNavigation + ", hint=" + validationHint + ", url=" + driver.getCurrentUrl());
    }
}
