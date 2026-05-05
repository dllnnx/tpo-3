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
import java.util.HashSet;
import java.util.Set;

public class AviaPage extends BaseFormPage {

    private static final By FROM_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Откуда']]//input[@data-ti='input']"
    );
    private static final By TO_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Куда']]//input[@data-ti='input']"
    );
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='trip-dates']");

    private static final String SAME_CITIES_ERROR = "Места отправления и назначения не должны совпадать";

    public AviaPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaPage fillFrom(String city) {
        fillField(FROM_INPUT, city);
        return this;
    }

    public AviaPage fillTo(String city) {
        fillField(TO_INPUT, city);
        return this;
    }

    public AviaPage pickDateInDays(int daysFromToday) {
        LocalDate target = DateUtils.plusDays(daysFromToday);
        wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@data-ti='suggest-container']")));
        navigateCalendarToMonth(target);
        clickDayInVisibleMonth(target);
        clickCalendarApply();
        return this;
    }

    public String getFromValue() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(FROM_INPUT)).getDomAttribute("value");
    }

    public String getToValue() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(TO_INPUT)).getDomAttribute("value");
    }

    public AviaResultsPage submit() {
        clickSubmitWithFallbacks();
        return new AviaResultsPage(driver, wait);
    }

    public boolean submitAndExpectNoNavigation() {
        Set<String> oldHandles = new HashSet<>(driver.getWindowHandles());

        try {
            WebElement btn = driver.findElement(SUBMIT);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception e) {
        }

        switchToNewWindowIfAny(oldHandles, Duration.ofSeconds(3));

        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(d -> d.getCurrentUrl().contains("/f/"));
            return false;
        } catch (Exception e) {
            return !driver.getCurrentUrl().contains("/f/");
        }
    }

    public boolean hasValidationHint() {
        return driver.findElements(By.xpath(
                "//*[contains(text(),'" + SAME_CITIES_ERROR + "')]"
        )).stream().anyMatch(this::isDisplayedSafe);
    }

    private void clickSubmitWithFallbacks() {
        Set<String> oldHandles = new HashSet<>(driver.getWindowHandles());
        String oldUrl = safeUrl();

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn);

        hideOverlayWidgets();

        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }

        if (!waitForUrlChangeOrNewWindow(oldHandles, oldUrl, Duration.ofSeconds(5))) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            waitForUrlChangeOrNewWindow(oldHandles, oldUrl, Duration.ofSeconds(8));
        }

        switchToNewWindowIfAny(oldHandles, Duration.ofSeconds(2));
    }

    private boolean waitForUrlChangeOrNewWindow(
            Set<String> oldHandles, String oldUrl, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(d -> {
                for (String h : d.getWindowHandles()) {
                    if (!oldHandles.contains(h)) {
                        return true;
                    }
                }
                String cur = safeUrl();
                return cur != null && !cur.equals(oldUrl);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void switchToNewWindowIfAny(Set<String> oldHandles, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            wait.until(d -> {
                for (String h : d.getWindowHandles()) {
                    if (!oldHandles.contains(h)) {
                        return true;
                    }
                }
                return false;
            });

            for (String h : driver.getWindowHandles()) {
                if (!oldHandles.contains(h)) {
                    driver.switchTo().window(h);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
}
