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

public class HotelPage extends Page {

    private static final By DEST_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and contains(normalize-space(),'Город')]]//input[@data-ti='input']"
    );
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='trip-dates']");
    private static final By SUGGEST_CONTAINER = By.xpath("//div[@data-ti='dropdown-suggest-container']");
    private static final By SUGGEST_ITEM = By.xpath("//div[@data-ti='dropdown-suggest-container']//div[@data-ti='dropdown-item']");
    private static final By SUBMIT = By.xpath("//button[@data-ti='submit-button']");
    private static final By CALENDAR = By.xpath("//*[@data-ti='calendar']");

    public HotelPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public HotelPage fillDestination(String city) {
        WebElement inp = wait.until(ExpectedConditions.presenceOfElementLocated(DEST_INPUT));
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
            String v = d.findElement(DEST_INPUT).getDomAttribute("value");
            return v != null && !v.isEmpty();
        });
        return this;
    }

    public HotelPage pickStayDates(int checkInDaysFromToday, int nights) {
        LocalDate checkIn = DateUtils.plusDays(checkInDaysFromToday);
        LocalDate checkOut = checkIn.plusDays(nights);

        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateInput);
        wait.until(ExpectedConditions.visibilityOfElementLocated(CALENDAR));

        navigateCalendarToMonth(checkIn);
        clickDayInVisibleMonth(checkIn);
        navigateCalendarToMonth(checkOut);
        clickDayInVisibleMonth(checkOut);
        clickCalendarApply();
        return this;
    }

    private void navigateCalendarToMonth(LocalDate target) {
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

    private String readCalendarMonthTitle() {
        try {
            return driver.findElement(
                    By.xpath("//*[@data-ti='calendar-month-header-title']")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private void clickDayInVisibleMonth(LocalDate target) {
        long millis = target.atStartOfDay(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli();
        By byEpoch = By.xpath(
                "//*[@data-ti='calendar-day-cell' and @data-empty='false'" +
                        " and @data-disabled='false' and @data-date='" + millis + "']"
        );
        driver.findElements(byEpoch).get(0).click();
    }

    private void clickCalendarApply() {
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

    public HotelResultsPage submit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        return new HotelResultsPage(driver, wait);
    }

    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
