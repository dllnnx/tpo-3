package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BusResultsPage extends Page {

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(45);

    public BusResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public BusResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(d -> d.getCurrentUrl().contains("bus.tutu.ru") || d.getCurrentUrl().contains("/avtobus"));
        longWait.until(d -> hasText("автобус"));
        return this;
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
        List<WebElement> priceElements = driver.findElements(By.xpath("//*[contains(text(),'₽') or contains(text(),'руб')]"));
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
