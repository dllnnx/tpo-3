package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class ElectrichkaResultsPage extends Page {

    private static final Pattern HHMM = Pattern.compile("^\\d{2}:\\d{2}$");

    public ElectrichkaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public ElectrichkaResultsPage waitForSchedule() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(45), Duration.ofMillis(500));
        longWait.until(d -> !d.getCurrentUrl().contains("/prigorod/"));
        longWait.until(d -> countDepartureTimes() >= 1
                || hasText("Расписание")
                || hasText("электричк")
                || hasText("Москва")
                || hasText("отправ"));
        return this;
    }

    public int countDepartureTimes() {
        List<WebElement> candidates = driver.findElements(By.xpath(
                "//a[contains(text(), ':') and string-length(normalize-space())=5]"
        ));
        return (int) candidates.stream()
                .filter(this::isDisplayedSafe)
                .map(e -> {
                    try { return e.getText().trim(); } catch (Exception x) { return ""; }
                })
                .filter(t -> HHMM.matcher(t).matches())
                .count();
    }

    public boolean hasText(String keyword) {
        return driver.findElements(By.xpath("//*[contains(., \"" + keyword + "\")]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
