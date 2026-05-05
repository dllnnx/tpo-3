package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DateUtils;

import java.time.LocalDate;
import java.util.List;

public class TrainPage extends BaseFormPage {

    private static final By FROM_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Откуда']]//input[@data-ti='input']"
    );
    private static final By TO_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Куда']]//input[@data-ti='input']"
    );
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='trip-dates']");

    public TrainPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public TrainPage fillFrom(String prefix, String exactCity) {
        fillFieldWithExactText(FROM_INPUT, prefix, exactCity);
        return this;
    }

    public TrainPage fillTo(String prefix, String exactCity) {
        fillFieldWithExactText(TO_INPUT, prefix, exactCity);
        return this;
    }

    private void fillFieldWithExactText(By inputLocator, String prefix, String exactCity) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input);

        String currentValue = input.getDomAttribute("value");
        if (currentValue != null && currentValue.equalsIgnoreCase(exactCity)) {
            return;
        }

        input.click();
        input.clear();
        input.sendKeys(prefix);

        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGEST_CONTAINER));

        By exactSuggestion = By.xpath("//div[@data-ti='dropdown-item' and contains(., '" + exactCity + "')]");

        WebElement suggestion = wait.until(ExpectedConditions.elementToBeClickable(exactSuggestion));

        suggestion.click();

        wait.until(d -> {
            String value = input.getDomAttribute("value");
            return value != null && value.equalsIgnoreCase(exactCity);
        });

        wait.until(ExpectedConditions.invisibilityOfElementLocated(SUGGEST_CONTAINER));
    }

    public TrainPage pickDateInDays(int daysFromToday) {
        LocalDate target = DateUtils.plusDays(daysFromToday);
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateInput);

        wait.until(ExpectedConditions.visibilityOfElementLocated(CALENDAR));

        navigateCalendarToMonth(target);
        clickDayInVisibleMonth(target);
        clickCalendarApply();
        return this;
    }

    public TrainResultsPage submitSearch() {
        clickSubmit();
        return new TrainResultsPage(driver, wait);
    }
}
