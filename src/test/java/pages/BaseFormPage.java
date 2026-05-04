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

    protected BaseFormPage(WebDriver driver, WebDriverWait wait, String url) {
        super(driver, wait, url);
    }

    protected WebElement fillField(By input, String city) {
        WebElement inp = wait.until(ExpectedConditions.presenceOfElementLocated(input));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", inp);
        inp.click();
        inp.sendKeys(city);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGEST_CONTAINER));
        wait.until(d -> {
            List<WebElement> items = d.findElements(SUGGEST_ITEM);
            return !items.isEmpty() && items.get(0).isDisplayed();
        });
        WebElement first = driver.findElements(SUGGEST_ITEM).get(0);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(SUGGEST_CONTAINER));
        } catch (Exception ignored) {
        }
        wait.until(d -> {
            String v = d.findElement(input).getDomAttribute("value");
            return v != null && !v.isEmpty();
        });
        return inp;
    }

    protected void navigateCalendarToMonth(LocalDate target) {
        String targetMonthRu = DateUtils.monthRu(target);
        String targetYear = String.valueOf(target.getYear());
        for (int i = 0; i < 24; i++) {
            String currentMonth = readCalendarMonthTitle();
            if (currentMonth.toLowerCase().contains(targetMonthRu.toLowerCase())
                    && currentMonth.contains(targetYear)) {
                return;
            }
            try {
                WebElement next = driver.findElement(
                        By.xpath("//button[@data-ti='calendar-month-header-next-button']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", next);
                Thread.yield();
            } catch (Exception ignored) {
                return;
            }
        }
    }

    protected String readCalendarMonthTitle() {
        try {
            return driver.findElement(
                    By.xpath("//*[@data-ti='calendar-month-header-title']")).getText();
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
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[@data-ti='calendar-popper-footer-select-button']")
                    ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception ignored) {
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(CALENDAR));
        } catch (Exception ignored) {
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
