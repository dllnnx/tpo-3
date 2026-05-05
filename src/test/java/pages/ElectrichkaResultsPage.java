package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Расписание электричек')]")
        ));

        wait.until(d -> countDepartureTimes() > 0 || hasText("отправ") || hasText("электрич"));

        return this;
    }

    public int countDepartureTimes() {
        List<WebElement> candidates = driver.findElements(By.xpath(
                "//a[contains(text(), ':') and string-length(normalize-space())=5]"
        ));

        int count = 0;
        for (WebElement el : candidates) {
            if (isDisplayedSafe(el)) {
                try {
                    String text = el.getText().trim();
                    if (HHMM.matcher(text).matches()) {
                        count++;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return count;
    }

    public boolean hasText(String keyword) {
        List<WebElement> elements = driver.findElements(By.xpath("//*[contains(., \"" + keyword + "\")]"));
        for (WebElement el : elements) {
            if (isDisplayedSafe(el)) {
                return true;
            }
        }
        return false;
    }
}
