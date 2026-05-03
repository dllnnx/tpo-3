package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class LoginModalPage extends Page {

    private static final By EMAIL_INPUT = By.xpath(
            "//input[@name='emailOrPhone' or @placeholder='Ваш телефон или почта']" +
                    " | //input[@name='userEmail']" +
                    " | //input[@type='email']"
    );
    private static final By NEXT_BUTTON = By.xpath(
            "//button[normalize-space()='Продолжить' or normalize-space()='Получить код'" +
                    " or normalize-space()='Далее' or normalize-space()='Войти']"
    );

    public LoginModalPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public LoginModalPage waitForVisible() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> getVisibleEmailInput() != null);
        return this;
    }

    public boolean isEmailFieldVisible() {
        return getVisibleEmailInput() != null;
    }

    private WebElement getVisibleEmailInput() {
        for (WebElement el : driver.findElements(EMAIL_INPUT)) {
            try {
                if (el.isDisplayed() && el.isEnabled()) {
                    return el;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public LoginModalPage fillEmail(String email) {
        WebElement input = wait.until(d -> getVisibleEmailInput());
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", input);
        input.click();
        try { input.clear(); } catch (Exception ignored) {}
        input.sendKeys(email);
        return this;
    }

    public String getEmailValue() {
        WebElement input = getVisibleEmailInput();
        return input == null ? "" : input.getDomAttribute("value");
    }

    public LoginModalPage clickContinue() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception ignored) {
        }
        return this;
    }

    public boolean codeStepReached() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                    !d.findElements(By.xpath(
                            "//*[contains(text(),'код') or contains(text(),'Код') or contains(text(),'Введите')]"
                    )).isEmpty()
            );
            List<WebElement> indicators = driver.findElements(By.xpath(
                    "//*[contains(text(),'код') or contains(text(),'Код') or contains(text(),'Введите')]"
            ));
            return indicators.stream().anyMatch(e -> {
                try {
                    return e.isDisplayed();
                } catch (Exception x) {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }
}
