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
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(SUBMIT));
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
        clickFirstSuggest(city);
        return this;
    }

    public ElectrichkaPage fillToIfEmpty(String city) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(TO_INPUT));
        String existing = input.getDomAttribute("value");
        if (existing != null && !existing.isEmpty()) {
            return this;
        }
        input.click();
        input.sendKeys(city);
        clickFirstSuggest(city);
        return this;
    }

    private void clickFirstSuggest(String city) {
        By suggest = By.xpath(
                "//*[@data-ti='dropdown-item' or contains(@class,'suggest')]" +
                        "//*[contains(text(),'" + city + "')]/ancestor-or-self::*[" +
                        "@data-ti='dropdown-item' or contains(@class,'item')][1]"
        );
        try {
            WebElement first = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(suggest));
            driver.findElements(suggest).get(0).click();
        } catch (Exception ignored) {
        }
    }

    public ElectrichkaResultsPage submit() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(SUBMIT));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.elementToBeClickable(SUBMIT));
        } catch (Exception ignored) {
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        return new ElectrichkaResultsPage(driver, wait);
    }

    public java.util.List<WebElement> findFromInput() {
        return driver.findElements(FROM_INPUT);
    }
}
