package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class BusResultsPage extends Page {

    private static final Pattern HHMM = Pattern.compile("^\\d{2}:\\d{2}$");

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(45);

    public BusResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public BusResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(d -> d.getCurrentUrl().contains("bus.tutu.ru") || d.getCurrentUrl().contains("/avtobus"));
        longWait.until(d -> countDepartureTimes() >= 1 || hasText("автобус"));
        return this;
    }

    public int countDepartureTimes() {
        List<WebElement> candidates = driver.findElements(By.xpath(
                "//span[contains(text(),':') and string-length(normalize-space())=5]"
        ));

        int count = 0;
        for (WebElement el : candidates) {
            if (isDisplayedSafe(el)) {
                try {
                    String text = el.getText().trim();
                    if (HHMM.matcher(text).matches()) {
                        count++;
                    }
                } catch (Exception e) {
                    // Skip elements that can't be read
                }
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
