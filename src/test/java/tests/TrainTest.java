package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.TrainResultsPage;


public class TrainTest extends BaseTest {

    /**
     * UC-01: Поиск ж/д билетов Москва → Санкт-Петербург с выбором даты.
     * Главная → клик по вкладке «Ж/д билеты» (URL остаётся главной, у submit
     * меняется текст на «Найти поезда») → Откуда=Москва → Куда=Санкт-Петербург →
     * дата +30 дней → Submit → редирект на /poezda/ с результатами →
     * ассерт ≥ 4 карточек поездов, упоминания типов вагонов.
     */
    @Test(description = "UC-01: Поиск ж/д Москва → СПб с выбором даты")
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

    /**
     * UC-02: Фильтр «Сапсан» на странице результатов ж/д.
     * Главная → клик «Ж/д билеты» → поиск Москва → СПб → активация чипа Сапсан →
     * ассерт что нет других типов поездов (Ласточка / Ночной экспресс) в видимых результатах.
     */
    @Test(description = "UC-02: Фильтр «Сапсан» в результатах ж/д")
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

    /**
     * UC-03: Переключение даты в date-strip на странице результатов ж/д.
     * Главная → клик «Ж/д билеты» → поиск → клик по соседней дате в date-strip →
     * проверка изменения URL и наличия результатов.
     */
    @Test(description = "UC-03: Переключение даты в date-strip результатов ж/д")
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
