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

        driver.findElements(inputLocator).get(0).click();
        input.click();
        input.clear();
        input.sendKeys(prefix);

        WebElement matched = wait.until(driver -> {
            List<WebElement> items = driver.findElements(SUGGEST_ITEM);

            for (WebElement el : items) {
                if (!el.isDisplayed()) continue;

                String text = el.getText();
                if (text != null && text.toLowerCase().contains(exactCity.toLowerCase())) {
                    return el;
                }
            }
            return null;
        });

        matched.click();

        wait.until(d -> {
            String value = d.findElement(inputLocator).getDomAttribute("value");
            return value != null &&
                    value.toLowerCase().contains(exactCity.toLowerCase());
        });
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
