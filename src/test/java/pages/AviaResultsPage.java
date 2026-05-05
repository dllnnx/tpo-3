package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class AviaResultsPage extends Page {

    private static final By AIRLINE_NAME = By.xpath("//div[@data-ti='card-badges']");
    private static final Pattern HHMM = Pattern.compile("^\\d{2}:\\d{2}$");

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(50);

    public AviaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
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
                String text = el.getText().trim();
                if (HHMM.matcher(text).matches()) {
                    count++;
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

    public boolean hasAirlineName() {
        Set<String> airlines = Set.of("Аэрофлот", "S7", "Победа", "Россия", "Utair", "Etihad", "Qatar", "Lufthansa");
        return driver.findElements(AIRLINE_NAME).stream()
                .map(WebElement::getText)
                .anyMatch(airlines::contains);
    }

}
