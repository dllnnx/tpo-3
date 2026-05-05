package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ElectrichkaPage;
import pages.ElectrichkaResultsPage;

public class ElectrichkaTest extends BaseTest {

    @Test(description = "UC-08: расписание электричек по дефолтному маршруту")
    public void uc08_electrichkaSchedule() {
        ElectrichkaPage page = new ElectrichkaPage(driver, wait).open();

        Assert.assertTrue(page.isFormPrefilledWithDefaults() || !page.findFromInput().isEmpty(),
                "Форма электричек должна быть видна (с дефолтами или пустая)");

        page.fillFromIfEmpty("Москва");
        page.fillToIfEmpty("Мытищи");

        ElectrichkaResultsPage results = page.submit().waitForSchedule();

        Assert.assertTrue(results.countDepartureTimes() >= 1
                        && results.hasText("Расписание электричек"),
                "Должно быть >= 1 времен в расписании и упоминание расписания. " +
                        "Time count: " + results.countDepartureTimes());
    }
}
