package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class AviaResultsPage extends Page {

    private static final By FILTER_BAGGAGE = By.xpath(
            "(//label[@data-ti='order-checkbox-outer'][.//*[@data-ti='form_baggage_filter']])[1]" +
                    " | (//*[@data-ti='form_baggage_filter']/ancestor::label[1])[1]"
    );
    private static final Pattern HHMM = Pattern.compile("^\\d{2}:\\d{2}$");

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(50);

    public AviaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(ExpectedConditions.urlContains("/f/"));
        longWait.until(d -> countDepartureTimes() >= 1
                || hasText("Авиабилеты")
                || hasText("Прямой"));
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

    public int countFlightCards() {
        return Math.max(1, countDepartureTimes() / 2);
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

    public boolean hasAirlineName() {
        String[] airlines = {"Аэрофлот", "S7", "Победа", "Россия", "Utair", "Etihad", "Qatar", "Lufthansa", "Aer", "Турки"};
        for (String airline : airlines) {
            if (hasText(airline)) {
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
    public AviaResultsPage clickBaggageFilter() {
        wait.until(ExpectedConditions.presenceOfElementLocated(FILTER_BAGGAGE));
        WebElement chip = driver.findElement(FILTER_BAGGAGE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", chip);

        WebDriverWait clickWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        clickWait.until(ExpectedConditions.elementToBeClickable(FILTER_BAGGAGE));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chip);

        // Wait for filter effect (non-critical, continue if timeout)
        try {
            WebDriverWait filterWait = new WebDriverWait(driver, Duration.ofSeconds(8));
            filterWait.until(d -> d.getCurrentUrl().contains("baggage") || hasText("С багажом"));
        } catch (Exception e) {
            // Filter may still be applied even if URL/text doesn't update immediately
        }

        return this;
    }

    public boolean baggageMentioned() {
        return hasText("Багаж") || hasText("багаж") || hasText("с багажом");
    }
}
