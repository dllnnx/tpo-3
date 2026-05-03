package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;

public class HomePage extends Page {

    private static final By HEADER = By.xpath("//*[@data-ti='header']");
    private static final By LOGIN_BUTTON = By.xpath("//button[@data-ti='login-button']");
    private static final By TAB_AVIA = By.xpath("//button[normalize-space()='Авиабилеты']");
    private static final By TAB_TRAIN = By.xpath("//button[normalize-space()='Ж/д билеты']");
    private static final By TAB_BUS = By.xpath("(//button[normalize-space()='Автобусы'])[1]");
    private static final By TAB_HOTEL = By.xpath("(//button[normalize-space()='Отели'])[1]");

    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.BASE_URL);
    }

    @Override
    public HomePage open() {
        super.open();
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER));
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_BUTTON));
        return this;
    }

    public boolean isHeaderNavigationVisible() {
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER));
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_BUTTON));
        wait.until(ExpectedConditions.presenceOfElementLocated(TAB_AVIA));
        wait.until(ExpectedConditions.presenceOfElementLocated(TAB_TRAIN));
        return true;
    }

    public TrainPage clickTrainNavLink() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(TAB_TRAIN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        TrainPage trainPage = new TrainPage(driver, wait);
        trainPage.open();
        wait.until(ExpectedConditions.urlContains("/poezda"));
        return trainPage;
    }

    public LoginModalPage openLoginModal() {
        wait.until(d -> !d.findElements(LOGIN_BUTTON).isEmpty());
        WebElement visibleBtn = wait.until(d -> {
            for (WebElement b : d.findElements(LOGIN_BUTTON)) {
                try {
                    if (b.isDisplayed() && b.isEnabled()) return b;
                } catch (Exception ignored) {
                }
            }
            return null;
        });
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", visibleBtn);
        hideOverlayWidgets();
        return new LoginModalPage(driver, wait);
    }
}
