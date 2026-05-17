package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class AviaResultsPage extends Page {

    private static final By AIRLINE_NAME = By.xpath("//motion.div[@data-ti='card-badges'] | //div[@data-ti='card-badges']");
    private static final By FLIGHT_CARD = By.xpath("//div[@data-ti='card-badges'] | //motion.div[@data-ti='card-badges']");
    private static final By FILTERS_PANEL = By.xpath("//*[@data-ti='filterGroups']");
    private static final By FILTERS_OPEN_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Фильтры')]"
    );

    private static final Map<String, String> FILTER_DATA_TI_SUFFIX = Map.of(
            "С багажом", "with_baggage",
            "Без багажа", "without_baggage",
            "Прямой", "transfers_0"
    );

    private static final Pattern HHMM = Pattern.compile("^\\d{2}:\\d{2}$");

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(50);
    private static final Duration FILTER_TIMEOUT = Duration.ofSeconds(15);

    public AviaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(d -> countVisibleFlightCards() >= 1
                || countDepartureTimes() >= 1
                || hasText("Авиабилеты")
                || hasText("Прямой"));
        return this;
    }

    public AviaResultsPage openFiltersPanel() {
        hideOverlayWidgets();
        WebDriverWait filterWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(500));

        List<WebElement> openButtons = driver.findElements(FILTERS_OPEN_BUTTON);
        for (WebElement btn : openButtons) {
            if (isDisplayedSafe(btn)) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
                break;
            }
        }

        WebElement panel = filterWait.until(ExpectedConditions.presenceOfElementLocated(FILTERS_PANEL));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'start'});", panel);
        filterWait.until(d -> isDisplayedSafe(d.findElement(FILTERS_PANEL)));
        return this;
    }

    public AviaResultsPage clickFilter(String label) {
        String suffix = FILTER_DATA_TI_SUFFIX.get(label);
        if (suffix == null) {
            throw new IllegalArgumentException("Unknown filter label: " + label);
        }

        By chipLocator = By.xpath(
                "//*[@data-ti='filterGroups']//*[contains(@data-ti,'shortcut') and contains(@data-ti,'"
                        + suffix + "')]"
        );

        WebDriverWait filterWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(500));
        WebElement chip = filterWait.until(d -> {
            for (WebElement candidate : d.findElements(chipLocator)) {
                if (isDisplayedSafe(candidate)) {
                    return candidate;
                }
            }
            return null;
        });

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", chip);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chip);

        try {
            filterWait.until(d -> isFilterChipSelected(d.findElement(chipLocator)));
        } catch (Exception ignored) {
        }

        return this;
    }

    public boolean isFilterActive(String label) {
        String suffix = FILTER_DATA_TI_SUFFIX.get(label);
        if (suffix == null) {
            return false;
        }

        By chipLocator = By.xpath(
                "//*[@data-ti='filterGroups']//*[contains(@data-ti,'shortcut') and contains(@data-ti,'"
                        + suffix + "')]"
        );

        for (WebElement chip : driver.findElements(chipLocator)) {
            if (isDisplayedSafe(chip) && isFilterChipSelected(chip)) {
                return true;
            }
        }
        return false;
    }

    public int countVisibleFlightCards() {
        List<WebElement> cards = driver.findElements(FLIGHT_CARD);
        int count = 0;
        for (WebElement card : cards) {
            if (isDisplayedSafe(card)) {
                count++;
            }
        }
        return count;
    }

    public boolean resultsMention(String keyword) {
        return hasText(keyword);
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

    private boolean isFilterChipSelected(WebElement chip) {
        String cls = chip.getDomAttribute("class");
        if (cls != null && cls.contains("selected")) {
            return true;
        }
        String ariaChecked = chip.getDomAttribute("aria-checked");
        return "true".equals(ariaChecked);
    }

}
