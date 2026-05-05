package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HomePage extends Page {

    private static final By HEADER = By.xpath("//*[@data-ti='header']");
    private static final By LOGIN_BUTTON = By.xpath("//button[@data-ti='login-button']");
    private static final By TAB_AVIA_BTN = By.xpath("//button[@data-ti='tab-unified-avia']");
    private static final By TAB_TRAIN_BTN = By.xpath("//button[@data-ti='tab-unified-train']");
    private static final By TAB_BUS_BTN = By.xpath("//button[@data-ti='tab-unified-bus']");
    private static final By TAB_HOTEL_BTN = By.xpath("//button[@data-ti='tab-unified-hotel']");
    private static final By SUBMIT_BUTTON = By.xpath("//button[@data-ti='submit-button']");

    public HomePage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.BASE_URL);
    }

    @Override
    public HomePage open() {
        super.open();
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER));
        return this;
    }

    public boolean isHeaderNavigationVisible() {
        wait.until(ExpectedConditions.presenceOfElementLocated(HEADER));
        wait.until(ExpectedConditions.presenceOfElementLocated(LOGIN_BUTTON));
        wait.until(ExpectedConditions.presenceOfElementLocated(TAB_AVIA_BTN));
        wait.until(ExpectedConditions.presenceOfElementLocated(TAB_TRAIN_BTN));
        return true;
    }

    public AviaPage clickAviaTab() {
        clickTab(TAB_AVIA_BTN, "Найти авиабилеты");
        return new AviaPage(driver, wait);
    }

    public TrainPage clickTrainTab() {
        clickTab(TAB_TRAIN_BTN, "Найти поезда");
        return new TrainPage(driver, wait);
    }

    public BusPage clickBusTab() {
        clickTab(TAB_BUS_BTN, "Найти автобусы");
        return new BusPage(driver, wait);
    }

    public HotelPage clickHotelTab() {
        clickTab(TAB_HOTEL_BTN, "Найти отели", "Найти жильё");
        return new HotelPage(driver, wait);
    }

    private void clickTab(By tabLocator, String... expectedSubmitTexts) {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(tabLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        String expectedTextsPattern = Arrays.stream(expectedSubmitTexts)
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));
        wait.until(ExpectedConditions.textMatches(SUBMIT_BUTTON, Pattern.compile(expectedTextsPattern)));
        hideOverlayWidgets();
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
