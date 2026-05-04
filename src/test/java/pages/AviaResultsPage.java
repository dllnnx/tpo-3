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

    public AviaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(50), Duration.ofMillis(500));
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
        return (int) candidates.stream()
                .filter(this::isDisplayedSafe)
                .map(e -> {
                    try {
                        return e.getText().trim();
                    } catch (Exception x) {
                        return "";
                    }
                })
                .filter(t -> HHMM.matcher(t).matches())
                .count();
    }

    public int countFlightCards() {
        return Math.max(1, countDepartureTimes() / 2);
    }

    public boolean hasText(String keyword) {
        return driver.findElements(By.xpath("//*[contains(text(),\"" + keyword + "\")]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    public boolean hasAirlineName() {
        String[] airlines = {"Аэрофлот", "S7", "Победа", "Россия", "Utair", "Etihad", "Qatar", "Lufthansa", "Aer", "Турки"};
        for (String a : airlines) {
            if (hasText(a)) return true;
        }
        return false;
    }

    public boolean hasPriceFormat() {
        return driver.findElements(By.xpath("//*[contains(text(),'₽')]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    public AviaResultsPage clickBaggageFilter() {
        wait.until(ExpectedConditions.presenceOfElementLocated(FILTER_BAGGAGE));
        WebElement chip = driver.findElement(FILTER_BAGGAGE);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", chip);
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(FILTER_BAGGAGE));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chip);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(d -> d.getCurrentUrl().contains("baggage")
                            || hasText("С багажом"));
        } catch (Exception ignored) {
        }
        return this;
    }

    public boolean baggageMentioned() {
        return hasText("Багаж") || hasText("багаж") || hasText("с багажом");
    }
}
