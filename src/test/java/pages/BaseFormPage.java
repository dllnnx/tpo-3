package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DateUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public abstract class BaseFormPage extends Page {

    protected static final By SUGGEST_CONTAINER = By.xpath("//div[@data-ti='dropdown-suggest-container']");
    protected static final By SUGGEST_ITEM = By.xpath("//div[@data-ti='dropdown-suggest-container']//div[@data-ti='dropdown-item']");
    protected static final By SUBMIT = By.xpath("//button[@data-ti='submit-button']");
    protected static final By CALENDAR = By.xpath("//*[@data-ti='calendar']");

    private static final By CALENDAR_NEXT_BUTTON = By.xpath("//button[@data-ti='calendar-month-header-next-button']");

    private static final By CALENDAR_MONTH_TITLE = By.xpath("//*[@data-ti='calendar-month-header-title']");

    private static final By CALENDAR_APPLY_BUTTON = By.xpath("//button[@data-ti='calendar-popper-footer-select-button']");

    private static final int MAX_CALENDAR_NAVIGATIONS = 24;

    protected BaseFormPage(WebDriver driver, WebDriverWait wait, String url) {
        super(driver, wait, url);
    }

    protected WebElement fillField(By input, String city) {
        WebElement inp = wait.until(ExpectedConditions.elementToBeClickable(input));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", inp);

        inp.click();
        inp.clear();
        inp.sendKeys(city);

        // Wait until suggestions appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGEST_CONTAINER));

        // Wait until at least one suggestion matches text
        WebElement matchedSuggestion = wait.until(driver -> {
            List<WebElement> items = driver.findElements(SUGGEST_ITEM);
            for (WebElement el : items) {
                try {
                    String text = el.getText();
                    if (el.isDisplayed() &&
                            text != null &&
                            text.toLowerCase().contains(city.toLowerCase())) {
                        return el;
                    }
                } catch (Exception ignored) {}
            }
            return null;
        });

        wait.until(ExpectedConditions.elementToBeClickable(matchedSuggestion));
        matchedSuggestion.click();

        // Wait until input value stabilizes and contains expected city
        wait.until(d -> {
            String value = d.findElement(input).getDomAttribute("value");
            return value != null &&
                    value.toLowerCase().contains(city.toLowerCase());
        });

        return inp;
    }

    protected void navigateCalendarToMonth(LocalDate target) {
        String targetMonthRu = DateUtils.monthRu(target);
        String targetYear = String.valueOf(target.getYear());

        for (int i = 0; i < MAX_CALENDAR_NAVIGATIONS; i++) {
            String currentMonth = readCalendarMonthTitle();
            if (currentMonth != null
                    && currentMonth.toLowerCase().contains(targetMonthRu.toLowerCase())
                    && currentMonth.contains(targetYear)) {
                return;
            }

            WebElement nextButton = driver.findElement(CALENDAR_NEXT_BUTTON);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);

            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                shortWait.until(d -> {
                    String newMonth = d.findElement(CALENDAR_MONTH_TITLE).getText();
                    return !newMonth.equals(currentMonth);
                });
            } catch (Exception e) {
                // if calendar doesn't update, try next iteration
            }
        }
    }

    protected String readCalendarMonthTitle() {
        try {
            return driver.findElement(CALENDAR_MONTH_TITLE).getText();
        } catch (Exception e) {
            return "";
        }
    }

    protected void clickDayInVisibleMonth(LocalDate target) {
        long millis = target.atStartOfDay(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli();
        By byEpoch = By.xpath(
                "//*[@data-ti='calendar-day-cell' and @data-empty='false'" +
                        " and @data-disabled='false' and @data-date='" + millis + "']"
        );
        driver.findElements(byEpoch).get(0).click();
    }

    protected void clickCalendarApply() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(CALENDAR_APPLY_BUTTON));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

            try {
                shortWait.until(ExpectedConditions.invisibilityOfElementLocated(CALENDAR));
            } catch (Exception e) {
                // Continue even if calendar doesn't close immediately
            }
        } catch (Exception e) {
            // If apply button not found, day selection may be auto-applied
        }
    }

    protected void clickSubmit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn);

        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }
}
