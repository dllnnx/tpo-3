package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.FaqPage;

public class FaqTest extends BaseTest {

    /**
     * UC-11: поиск в справочной
     */
    @Test(description = "UC-11: поиск в справочной по запросу \"возврат билета\"")
    public void uc11_searchInFaq() {
        FaqPage faq = new FaqPage(driver, wait).open()
                .search("возврат билета");

        String urlAfterSearch = faq.currentUrl();
        boolean urlChanged = !urlAfterSearch.equals("https://www.tutu.ru/2read/");
        boolean hasRelevantResults = faq.countResultsContainingKeyword("озврат") >= 1
                && faq.countResultsContainingKeyword("ернуть") >= 1;

        Assert.assertTrue(urlChanged && hasRelevantResults,
                "URL должен измениться после поиска или должны появиться результаты по запросу. " +
                        "URL: " + urlAfterSearch);
        Assert.assertTrue(faq.hasResultsHeading(),
                "На странице результатов должны быть видны заголовки (h1/h2)");
    }
}
