package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DateUtils;

import java.time.LocalDate;

public class BusPage extends BaseFormPage {

    private static final By FROM_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Откуда']]//input[@data-ti='input']"
    );
    private static final By TO_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Куда']]//input[@data-ti='input']"
    );
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='trip-dates']");

    public BusPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public BusPage fillFrom(String city) {
        fillField(FROM_INPUT, city);
        return this;
    }

    public BusPage fillTo(String city) {
        fillField(TO_INPUT, city);
        return this;
    }

    public BusPage pickDateInDays(int daysFromToday) {
        LocalDate target = DateUtils.plusDays(daysFromToday);
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateInput);

        wait.until(ExpectedConditions.visibilityOfElementLocated(CALENDAR));

        navigateCalendarToMonth(target);
        clickDayInVisibleMonth(target);
        clickCalendarApply();
        return this;
    }

    public BusResultsPage submit() {
        clickSubmit();
        return new BusResultsPage(driver, wait);
    }
}
