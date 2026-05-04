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
        return fillField(FROM_INPUT, prefix, exactCity);
    }

    public TrainPage fillTo(String prefix, String exactCity) {
        return fillField(TO_INPUT, prefix, exactCity);
    }

    private TrainPage fillField(By input, String prefix, String exactCity) {
        WebElement inp = wait.until(ExpectedConditions.presenceOfElementLocated(input));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", inp);
        inp.click();
        inp.sendKeys(prefix);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGEST_CONTAINER));

        By exactItem = By.xpath(
                "//div[@data-ti='dropdown-suggest-container']//div[@data-ti='dropdown-item']" +
                        "[.//*[normalize-space()='" + exactCity + "']]"
        );
        WebElement target = null;
        try {
            target = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> {
                        List<WebElement> exact = d.findElements(exactItem);
                        for (WebElement e : exact) {
                            if (isDisplayedSafe(e)) return e;
                        }
                        return null;
                    });
        } catch (Exception ignored) {
        }
        if (target == null) {
            wait.until(d -> {
                List<WebElement> items = d.findElements(SUGGEST_ITEM);
                return !items.isEmpty() && items.get(0).isDisplayed();
            });
            target = driver.findElements(SUGGEST_ITEM).get(0);
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(SUGGEST_CONTAINER));
        } catch (Exception ignored) {
        }
        wait.until(d -> {
            String v = d.findElement(input).getDomAttribute("value");
            return v != null && !v.isEmpty();
        });
        return this;
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
