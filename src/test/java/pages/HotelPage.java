package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;
import utils.DateUtils;

import java.time.Duration;
import java.time.LocalDate;

public class HotelPage extends Page {

    private static final By DEST_INPUT = By.xpath("//input[@data-ti='hotels-destination-input']");
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='date-input']");
    private static final By SUBMIT = By.xpath("//button[normalize-space()='Найти']");

    public HotelPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.HOTEL_URL);
    }

    @Override
    public HotelPage open() {
        super.open();
        return this;
    }

    public HotelPage fillDestination(String city) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(DEST_INPUT));
        input.click();
        input.clear();
        input.sendKeys(city);
        By suggest = By.xpath(
                "//*[@data-ti='dropdown-item' or contains(@class,'suggest') or contains(@class,'dropdown')]" +
                        "//*[contains(text(),'" + city + "')]/ancestor::*[" +
                        "@data-ti='dropdown-item' or contains(@class,'item')][1]"
        );
        try {
            WebElement first = new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.elementToBeClickable(suggest));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
        } catch (Exception ignored) {
            input.sendKeys(Keys.ENTER);
        }
        return this;
    }

    public HotelPage pickStayDates(int checkInDaysFromToday, int nights) {
        LocalDate checkIn = DateUtils.plusDays(checkInDaysFromToday);
        LocalDate checkOut = checkIn.plusDays(nights);

        WebElement dateField = wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT));
        dateField.click();

        clickCalendarDay(checkIn);
        clickCalendarDay(checkOut);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(d -> {
                        String v = d.findElement(DATE_INPUT).getDomAttribute("value");
                        return v != null && !v.isEmpty();
                    });
        } catch (Exception ignored) {
        }
        return this;
    }

    private void clickCalendarDay(LocalDate date) {
        String iso = DateUtils.toDashFormat(date);
        By cell = By.xpath(
                "//*[@data-ti='calendar-cell-day-" + iso + "']" +
                        " | //*[@data-day='" + iso + "']" +
                        " | //div[@role='gridcell' and not(contains(@aria-disabled,'true'))]" +
                        "[.//span[normalize-space()='" + date.getDayOfMonth() + "']]"
        );
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.elementToBeClickable(cell));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (Exception ignored) {
        }
    }

    public HotelResultsPage submit() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        return new HotelResultsPage(driver, wait);
    }

}
