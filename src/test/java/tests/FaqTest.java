package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.FaqPage;

public class FaqTest extends BaseTest {

    @Test(description = "UC-11: поиск в справочной по запросу \"возврат билета\"")
    public void uc11_searchInFaq() {
        FaqPage faq = new FaqPage(driver, wait).open()
                .search("возврат билета");

        String urlAfterSearch = faq.currentUrl();
        boolean urlChanged = !urlAfterSearch.equals("https://www.tutu.ru/2read/");
        boolean hasRelevantResults = faq.countResultsContainingKeyword("озврат") >= 1
                && faq.countResultsContainingKeyword("ернуть") >= 1;

        Assert.assertTrue(urlChanged && hasRelevantResults, "Неверный URL: " + urlAfterSearch);
        Assert.assertTrue(faq.hasResultsHeading(), "На странице результатов должны быть видны заголовки");
    }
}
