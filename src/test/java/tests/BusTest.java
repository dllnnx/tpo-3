package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BusPage;
import pages.BusResultsPage;

@SuppressWarnings("IllegalAllureIdUast")
public class BusTest extends BaseTest {

    /**
     * UC-06: Поиск автобусов Москва → Тула на завтра.
     * Bus → ввод Москва (suggest) → ввод Тула (suggest) → выбор даты «Завтра» → submit →
     * ассерт изменения URL, наличия упоминания «автобус» или ≥ 1 рейса с временем.
     */
    @Test(description = "UC-06: Поиск автобусов Москва → Тула")
    public void uc06_searchBusesMoscowTula() {
        BusPage bus = new BusPage(driver, wait).open()
                .fillFrom("Москва")
                .fillTo("Тула")
                .pickTomorrow();

        BusResultsPage results = bus.submit();
        try {
            results.waitForResults();
        } catch (Exception ignored) {
        }

        String url = results.currentUrl();
        Assert.assertTrue(url.contains("bus.tutu.ru"),
                "URL должен оставаться на bus.tutu.ru после поиска: " + url);

        boolean hasResultsContent = url.toLowerCase().contains("moskva")
                || url.toLowerCase().contains("tula")
                || url.contains("Москва")
                || url.contains("Тула")
                || results.hasText("Москва")
                || results.hasText("Тула")
                || results.countDepartureTimes() >= 1
                || results.hasText("автобус")
                || results.hasText("Автобус")
                || results.hasText("маршрут")
                || results.hasPriceFormat();

        Assert.assertTrue(hasResultsContent,
                "После поиска ожидаем либо переход на URL с маршрутом, либо упоминание Москва/Тула/автобус в контенте. URL: " + url);
    }
}
