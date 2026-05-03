package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;

import java.time.Duration;
import java.util.List;

public class BusPage extends Page {

    private static final By FROM_INPUT = By.xpath("//input[@placeholder='Откуда']");
    private static final By TO_INPUT = By.xpath("//input[@placeholder='Куда']");
    private static final By DATE_INPUT = By.xpath("//input[@placeholder='Дата']");
    private static final By SUBMIT = By.xpath("//button[normalize-space()='Найти билеты']");
    private static final By START_CITY_HIDDEN = By.xpath("//input[@name='startCity']");
    private static final By END_CITY_HIDDEN = By.xpath("//input[@name='endCity']");

    public BusPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.BUS_URL);
    }

    @Override
    public BusPage open() {
        super.open();
        return this;
    }

    public BusPage fillFrom(String city) {
        return fillCity(FROM_INPUT, city, START_CITY_HIDDEN);
    }

    public BusPage fillTo(String city) {
        return fillCity(TO_INPUT, city, END_CITY_HIDDEN);
    }

    private BusPage fillCity(By input, String city, By hiddenIdInput) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(input));
        el.click();
        el.sendKeys(city);
        By suggest = By.xpath("//div[contains(@class,'styles__dropdown_item') and contains(., '" + city + "')]");
        WebElement first = wait.until(d -> {
            List<WebElement> items = d.findElements(suggest);
            for (WebElement i : items) {
                try {
                    if (i.isDisplayed()) return i;
                } catch (Exception ignored) {
                }
            }
            return null;
        });
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> {
                        try {
                            String v = d.findElement(hiddenIdInput).getDomAttribute("value");
                            return v != null && !v.isEmpty();
                        } catch (Exception e) {
                            return false;
                        }
                    });
        } catch (Exception ignored) {
        }
        return this;
    }

    public BusPage pickTomorrow() {
        WebElement dateField = wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT));
        dateField.click();
        By tomorrowChip = By.xpath(
                "//button[normalize-space()='Завтра']" +
                        " | //*[normalize-space(text())='Завтра']/ancestor::button[1]"
        );
        try {
            WebElement tomorrow = wait.until(ExpectedConditions.elementToBeClickable(tomorrowChip));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tomorrow);
        } catch (Exception ignored) {
            By anyEnabledDay = By.xpath("//td[not(contains(@class,'disabled'))]/a[string-length(normalize-space()) <= 2][2]");
            try {
                WebElement day = driver.findElement(anyEnabledDay);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", day);
            } catch (Exception ignored2) {
            }
        }
        return this;
    }

    public BusResultsPage submit() {
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT)).click();
        return new BusResultsPage(driver, wait);
    }
}
