package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.HotelPage;
import pages.HotelResultsPage;

public class HotelTest extends BaseTest {

    @Test(description = "UC-07: поиск отелей в Сочи на 2 ночи")
    public void uc07_searchHotelsSochi() {
        HotelPage hotel = new HomePage(driver, wait).open()
                .clickHotelTab()
                .fillDestination("Сочи")
                .pickStayDates(30, 2);

        HotelResultsPage results = hotel.submit().waitForResults();

        Assert.assertTrue(results.currentUrl().contains("hotel.tutu.ru"), "Неверный URL " + results.currentUrl());
        Assert.assertTrue(results.hasText("Сочи") || results.currentUrl().toLowerCase().contains("sochi"),
                "На странице результатов должно быть упоминание \"Сочи\"");
        Assert.assertTrue(results.countHotelCards() >= 1 || results.hasPriceFormat(),
                "Должна быть хотя бы одна карточка отеля с ценой в результатах");
    }
}
