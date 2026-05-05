package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BusPage;
import pages.BusResultsPage;
import pages.HomePage;

public class BusTest extends BaseTest {

    @Test(description = "UC-06: поиск автобусов Москва - Тула")
    public void uc06_searchBusesMoscowTula() {
        BusPage bus = new HomePage(driver, wait).open()
                .clickBusTab()
                .fillFrom("Москва")
                .fillTo("Тула")
                .pickDateInDays(1);

        BusResultsPage results = bus.submit();
        results.waitForResults();

        String url = results.currentUrl();
        Assert.assertTrue(url.contains("bus.tutu.ru"),
                "URL должен оставаться на bus.tutu.ru после поиска: " + url);

        Assert.assertTrue(results.hasPriceFormat());
    }
}
