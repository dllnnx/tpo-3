package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DateUtils;

import java.time.LocalDate;

public class HotelPage extends BaseFormPage {

    private static final By DEST_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and contains(normalize-space(),'Город')]]//input[@data-ti='input']"
    );
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='trip-dates']");

    public HotelPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public HotelPage fillDestination(String city) {
        fillField(DEST_INPUT, city);
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

    public HotelResultsPage submit() {
        clickSubmit();
        return new HotelResultsPage(driver, wait);
    }
}
