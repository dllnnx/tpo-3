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
    private static final By SUBMIT_BUTTON = By.xpath("//button[contains(@class,'j-submit_button')]");

    private TrainPage trainForm;

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

    /**
     * Кликает по вкладке «Ж/д билеты» в форме на главной.
     *
     * На реальном сайте такой клик НЕ навигирует на /poezda/ — страница
     * остаётся главной, но форма меняет содержимое: текст submit-кнопки
     * становится «Найти поезда». Ждём именно этот признак вместо
     * ожидания смены URL.
     */
    public HomePage clickTrainTab() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(TAB_TRAIN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        wait.until(d -> {
            try {
                String txt = d.findElement(SUBMIT_BUTTON).getText();
                return txt != null && txt.contains("Найти поезда");
            } catch (Exception e) {
                return false;
            }
        });
        trainForm = new TrainPage(driver, wait);
        return this;
    }

    public HomePage fillFrom(String prefix, String exactCity) {
        ensureTrainForm().fillFrom(prefix, exactCity);
        return this;
    }

    public HomePage fillTo(String prefix, String exactCity) {
        ensureTrainForm().fillTo(prefix, exactCity);
        return this;
    }

    public HomePage pickDateInDays(int daysFromToday) {
        ensureTrainForm().pickDateInDays(daysFromToday);
        return this;
    }

    public TrainResultsPage submitSearch() {
        return ensureTrainForm().submitSearch();
    }

    private TrainPage ensureTrainForm() {
        if (trainForm == null) {
            throw new IllegalStateException(
                    "Сначала нужно вызвать clickTrainTab() — форма «Ж/д билеты» ещё не активирована");
        }
        return trainForm;
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
