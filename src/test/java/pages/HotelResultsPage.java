package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HotelResultsPage extends Page {

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(60);

    public HotelResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public HotelResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(ExpectedConditions.urlContains("hotel.tutu.ru"));
        longWait.until(d -> countHotelCards() >= 1 || hasText("отел") || hasText("Отел"));
        return this;
    }

    public int countHotelCards() {
        List<WebElement> elements = driver.findElements(By.xpath(
                "//*[contains(text(),'отзыв') or contains(text(),'Отел') or contains(text(),'хостел')]"
        ));

        int count = 0;
        for (WebElement el : elements) {
            if (isDisplayedSafe(el)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasText(String keyword) {
        List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(),\"" + keyword + "\")]"));
        for (WebElement el : elements) {
            if (isDisplayedSafe(el)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPriceFormat() {
        List<WebElement> priceElements = driver.findElements(By.xpath("//*[contains(text(),'₽')]"));
        for (WebElement el : priceElements) {
            if (isDisplayedSafe(el)) {
                return true;
            }
        }
        return false;
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
