package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AeroexpressPage;

public class AeroexpressTest extends BaseTest {

    /**
     * UC-09: расписание аэроэкспрессов
     */
    @Test(description = "UC-09: Расписание аэроэкспрессов")
    public void uc09_aeroexpressSchedule() {
        AeroexpressPage page = new AeroexpressPage(driver, wait).open()
                .waitForLoad()
                .clickScheduleButton()
                .waitForSchedulePageLoad();

        Assert.assertTrue(page.hasAirportName(),
                "На странице должен быть виден один из аэропортов (Шереметьево / Внуково / Домодедово...)" +
                        " или вокзалов (Павелецкий / Белорусский / Киевский)");
        Assert.assertTrue(page.hasText("Билеты на Аэроэкспресс")
                        || page.hasText("Расписание Аэроэкспресса"),
                "Должно быть упоминание расписания");
    }
}
