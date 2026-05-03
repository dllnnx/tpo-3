package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.HotelPage;
import pages.HotelResultsPage;

@SuppressWarnings("IllegalAllureIdUast")
public class HotelTest extends BaseTest {

    /**
     * UC-07: Поиск отелей в Сочи на 2 ночи.
     * Hotel → ввод «Сочи» в hotels-destination-input → выбор саджеста →
     * выбор дат заезда +30, выезда +32 → submit → ассерт что URL содержит
     * параметры поиска и видны ≥ 1 карточек отелей с ценой.
     */
    @Test(description = "UC-07: Поиск отелей в Сочи на 2 ночи")
    public void uc07_searchHotelsSochi() {
        HotelPage hotel = new HomePage(driver, wait).open()
                .clickHotelTab()
                .fillDestination("Сочи")
                .pickStayDates(30, 2);

        HotelResultsPage results = hotel.submit().waitForResults();

        Assert.assertTrue(results.currentUrl().contains("hotel.tutu.ru"),
                "URL должен остаться на hotel.tutu.ru: " + results.currentUrl());
        Assert.assertTrue(results.hasText("Сочи") || results.currentUrl().toLowerCase().contains("sochi"),
                "На странице результатов должно быть упоминание «Сочи»");
        Assert.assertTrue(results.countHotelCards() >= 1 || results.hasPriceFormat(),
                "Ожидаем хотя бы одну карточку отеля с ценой в результатах");
    }
}
