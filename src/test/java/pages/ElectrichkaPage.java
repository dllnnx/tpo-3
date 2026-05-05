package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;

import java.time.Duration;

public class ElectrichkaPage extends Page {

    private static final By FROM_INPUT = By.xpath("//label[.//span[text()='Откуда']]//input");
    private static final By TO_INPUT = By.xpath("//label[.//span[text()='Куда']]//input");
    private static final By SUBMIT = By.xpath("//button[@data-ti='submit-button']");

    private static final By SUGGEST_ITEM = By.xpath("//*[@data-ti='dropdown-item']");

    public ElectrichkaPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.ELECTRICHKA_URL);
    }

    @Override
    public ElectrichkaPage open() {
        super.open();
        return this;
    }

    public boolean isFormPrefilledWithDefaults() {
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            longWait.until(ExpectedConditions.presenceOfElementLocated(SUBMIT));

            String fromVal = driver.findElement(FROM_INPUT).getDomAttribute("value");
            String toVal = driver.findElement(TO_INPUT).getDomAttribute("value");

            return fromVal != null && !fromVal.isEmpty()
                    && toVal != null && !toVal.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public ElectrichkaPage fillFromIfEmpty(String city) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(FROM_INPUT));
        String existing = input.getDomAttribute("value");

        if (existing != null && !existing.isEmpty()) {
            return this;
        }

        input.click();
        input.sendKeys(city);
        selectSuggestion(FROM_INPUT, city);
        return this;
    }

    public ElectrichkaPage fillToIfEmpty(String city) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(TO_INPUT));

        String existing = input.getDomAttribute("value");
        if (existing != null && !existing.isEmpty()) {
            return this;
        }

        input.click();
        input.clear();
        input.sendKeys(city);

        selectSuggestion(TO_INPUT, city);

        return this;
    }

    private void selectSuggestion(By inputLocator, String city) {

        WebElement matched = wait.until(driver -> {
            var items = driver.findElements(SUGGEST_ITEM);
            for (WebElement el : items) {
                try {
                    if (el.isDisplayed() &&
                            el.getText() != null &&
                            el.getText().toLowerCase().contains(city.toLowerCase())) {
                        return el;
                    }
                } catch (Exception ignored) {}
            }
            return null;
        });

        matched.click();

        wait.until(d -> {
            String value = d.findElement(inputLocator).getDomAttribute("value");
            return value != null &&
                    value.toLowerCase().contains(city.toLowerCase());
        });
    }

    public ElectrichkaResultsPage submit() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(SUBMIT));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn);

        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        } catch (Exception ignored) {
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        return new ElectrichkaResultsPage(driver, wait);
    }

    public java.util.List<WebElement> findFromInput() {
        return driver.findElements(FROM_INPUT);
    }
}
