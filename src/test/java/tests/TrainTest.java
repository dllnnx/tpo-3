package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TrainResultsPage;


public class TrainTest extends BaseTest {

    @Test(description = "UC-01: поиск ж/д Москва -> СПб с выбором даты")
    public void uc01_searchTrainsMoscowSpbWithDate() {
        TrainResultsPage results = new HomePage(driver, wait).open()
                .clickTrainTab()
                .fillFrom("Москв", "Москва")
                .fillTo("Санкт-Пете", "Санкт-Петербург")
                .pickDateInDays(30)
                .submitSearch()
                .waitForResults();

        Assert.assertTrue(results.countTrainCards() >= 4,
                "Ожидаем минимум 4 карточки поездов в результатах, нашли: " + results.countTrainCards());
    }

    @Test(description = "UC-02: фильтр Сапсан в результатах ж/д")
    public void uc02_applySapsanFilter() {
        TrainResultsPage results = new HomePage(driver, wait).open()
                .clickTrainTab()
                .fillFrom("Москв", "Москва")
                .fillTo("Санкт-Пете", "Санкт-Петербург")
                .pickDateInDays(20)
                .submitSearch()
                .waitForResults();

        int trainsBefore = results.countTrainCards();
        results.clickSapsanFilter();

        Assert.assertTrue(results.isSapsanFilterActive(),
                "Чип «Сапсан» должен получить класс selected после клика");
        Assert.assertTrue(results.allVisibleTrainsAreSapsan(),
                "После фильтра все видимые поезда должны быть «Сапсан». " +
                        "Найдены: " + results.visibleTrainNames());
        Assert.assertTrue(results.countTrainCards() <= trainsBefore,
                "Количество карточек после применения фильтра не должно вырасти");
    }

    @Test(description = "UC-03: переключение даты в date-strip результатов ж/д")
    public void uc03_changeDateInDateStrip() {
        TrainResultsPage results = new HomePage(driver, wait).open()
                .clickTrainTab()
                .fillFrom("Москв", "Москва")
                .fillTo("Санкт-Пете", "Санкт-Петербург")
                .pickDateInDays(15)
                .submitSearch()
                .waitForResults();

        String urlBefore = driver.getCurrentUrl();
        results.clickAdjacentDateInStrip();
        String urlAfter = driver.getCurrentUrl();

        Assert.assertNotEquals(urlAfter, urlBefore,
                "URL должен измениться после клика по соседней дате в date-strip");
        Assert.assertTrue(urlAfter.contains("/poezda/"),
                "После переключения даты должны оставаться на странице результатов /poezda/");

        results.waitForResults();
        Assert.assertTrue(results.countTrainCards() >= 1,
                "На новой дате должны быть результаты (≥ 1 карточка после ожидания загрузки)");
    }
}
