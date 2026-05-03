package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;

import java.time.Duration;
import java.util.List;

public class FaqPage extends Page {

    private static final By SEARCH_INPUT = By.xpath("(//input[@type='text'])[1]");

    public FaqPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.FAQ_URL);
    }

    @Override
    public FaqPage open() {
        super.open();
        return this;
    }

    public FaqPage search(String query) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        input.click();
        input.clear();
        input.sendKeys(query);
        input.sendKeys(Keys.ENTER);
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> d.getCurrentUrl().contains("search="));
        return this;
    }

    public boolean hasResultsHeading() {
        return driver.findElements(By.xpath("//h1 | //h2"))
                .stream().anyMatch(e -> {
                    try {
                        return e.isDisplayed() && !e.getText().isEmpty();
                    } catch (Exception ex) {
                        return false;
                    }
                });
    }

    public int countResultsContainingKeyword(String keyword) {
        List<WebElement> matches = driver.findElements(By.xpath(
                "//a[contains(., \"" + keyword + "\")] | //h2[contains(., \"" + keyword + "\")] | //h3[contains(., \"" + keyword + "\")]"
        ));
        return (int) matches.stream().filter(e -> {
            try {
                return e.isDisplayed();
            } catch (Exception x) {
                return false;
            }
        }).count();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
