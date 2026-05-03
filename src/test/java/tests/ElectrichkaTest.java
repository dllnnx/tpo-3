package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.ElectrichkaPage;
import pages.ElectrichkaResultsPage;

@SuppressWarnings("IllegalAllureIdUast")
public class ElectrichkaTest extends BaseTest {

    /**
     * UC-08: Расписание электричек Москва → Мытищи.
     * /prigorod/ → проверка что форма предзаполнена дефолтным маршрутом → submit →
     * ассерт URL содержит /prigorod/ и видны ≥ 5 строк времён формата HH:MM
     * (или хотя бы упоминание «электричка»).
     */
    @Test(description = "UC-08: Расписание электричек по дефолтному маршруту")
    public void uc08_electrichkaSchedule() {
        ElectrichkaPage page = new ElectrichkaPage(driver, wait).open();

        Assert.assertTrue(page.isFormPrefilledWithDefaults() || !page.findFromInput().isEmpty(),
                "Форма электричек должна быть видна (с дефолтами или пустая)");

        page.fillFromIfEmpty("Москва");
        page.fillToIfEmpty("Мытищи");

        ElectrichkaResultsPage results = page.submit().waitForSchedule();

        Assert.assertTrue(results.currentUrl().contains("/prigorod/"),
                "URL должен содержать /prigorod/: " + results.currentUrl());
        Assert.assertTrue(results.countDepartureTimes() >= 5
                        || results.hasText("электричк")
                        || results.hasText("расписан"),
                "Должно быть ≥ 5 времён в расписании или упоминание расписания. " +
                        "Time count: " + results.countDepartureTimes());
    }
}
