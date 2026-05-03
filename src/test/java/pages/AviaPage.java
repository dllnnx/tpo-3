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
import java.time.ZoneId;
import java.util.List;

public class AviaPage extends Page {

    private static final By FROM_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Откуда']]//input[@data-ti='input']"
    );
    private static final By TO_INPUT = By.xpath(
            "//div[@data-ti='input-root'][.//span[@data-ti='input-label' and normalize-space()='Куда']]//input[@data-ti='input']"
    );
    private static final By DATE_INPUT = By.xpath("//input[@data-ti='trip-dates']");
    private static final By SUGGEST_CONTAINER = By.xpath("//div[@data-ti='dropdown-suggest-container']");
    private static final By SUGGEST_ITEM = By.xpath("//div[@data-ti='dropdown-suggest-container']//div[@data-ti='dropdown-item']");
    private static final By SUBMIT = By.xpath("//button[@data-ti='submit-button']");

    public AviaPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaPage fillFrom(String city) {
        return fillField(FROM_INPUT, city);
    }

    public AviaPage fillTo(String city) {
        return fillField(TO_INPUT, city);
    }

    private AviaPage fillField(By input, String city) {
        WebElement inp = wait.until(ExpectedConditions.presenceOfElementLocated(input));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", inp);
        inp.click();
        inp.sendKeys(city);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGEST_CONTAINER));
        wait.until(d -> {
            List<WebElement> items = d.findElements(SUGGEST_ITEM);
            return !items.isEmpty() && items.get(0).isDisplayed();
        });
        WebElement first = driver.findElements(SUGGEST_ITEM).get(0);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
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

    public AviaPage pickDateInDays(int daysFromToday) {
        LocalDate target = DateUtils.plusDays(daysFromToday);
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateInput);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@data-ti='suggest-container']")));

        navigateCalendarToMonth(target);
        clickDayInVisibleMonth(target);
        clickCalendarApply();
        return this;
    }

    private void navigateCalendarToMonth(LocalDate target) {
        String targetMonthRu = DateUtils.monthRu(target);
        String targetYear = String.valueOf(target.getYear());
        for (int i = 0; i < 24; i++) {
            String currentMonth = readCalendarMonthTitle();
            if (currentMonth.toLowerCase().contains(targetMonthRu.toLowerCase())
                    && currentMonth.contains(targetYear)) {
                return;
            }
            try {
                WebElement next = driver.findElement(
                        By.xpath("//button[@data-ti='calendar-month-header-next-button']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", next);
                Thread.yield();
            } catch (Exception ignored) {
                return;
            }
        }
    }

    private String readCalendarMonthTitle() {
        try {
            return driver.findElement(
                    By.xpath("//*[@data-ti='calendar-month-header-title']")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private void clickDayInVisibleMonth(LocalDate target) {
        long millis = target.atStartOfDay(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli();
        By byEpoch = By.xpath(
                "//*[@data-ti='calendar-day-cell' and @data-empty='false'" +
                        " and @data-disabled='false' and @data-date='" + millis + "']"
        );
        driver.findElements(byEpoch).get(0).click();
    }

    private void clickCalendarApply() {
        try {
            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[@data-ti='calendar-popper-footer-select-button']")
                    ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception ignored) {
        }
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath("//*[@data-ti='calendar']")));
        } catch (Exception ignored) {
        }
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
        java.util.Set<String> oldHandles = new java.util.HashSet<>(driver.getWindowHandles());
        try {
            WebElement btn = driver.findElement(SUBMIT);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (Exception ignored) {
        }
        switchToNewWindowIfAny(oldHandles, Duration.ofSeconds(3));
        try {
            new WebDriverWait(driver, Duration.ofSeconds(7))
                    .until(d -> d.getCurrentUrl().contains("/f/"));
            return false;
        } catch (Exception e) {
            return !driver.getCurrentUrl().contains("/f/");
        }
    }

    private void clickSubmitWithFallbacks() {
        java.util.Set<String> oldHandles = new java.util.HashSet<>(driver.getWindowHandles());
        String oldUrl = safeUrl();
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(SUBMIT));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('iframe.tutu-chat-widget-iframe, " +
                            "[class*=\"tutuSmart\"], [data-ti=\"disclaimer_wrapper\"], " +
                            "[class*=\"chat-widget\"]').forEach(e => e.style.display = 'none');");
        } catch (Exception ignored) {
        }
        try {
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
        if (!waitForUrlChangeOrNewWindow(oldHandles, oldUrl, Duration.ofSeconds(5))) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            } catch (Exception ignored) {
            }
            waitForUrlChangeOrNewWindow(oldHandles, oldUrl, Duration.ofSeconds(8));
        }
        switchToNewWindowIfAny(oldHandles, Duration.ofSeconds(2));
    }

    private boolean waitForUrlChangeOrNewWindow(
            java.util.Set<String> oldHandles, String oldUrl, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(d -> {
                        for (String h : d.getWindowHandles()) {
                            if (!oldHandles.contains(h)) return true;
                        }
                        String cur = safeUrl();
                        return cur != null && !cur.equals(oldUrl);
                    });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void switchToNewWindowIfAny(java.util.Set<String> oldHandles, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(d -> {
                        for (String h : d.getWindowHandles()) {
                            if (!oldHandles.contains(h)) return true;
                        }
                        return false;
                    });
            for (String h : driver.getWindowHandles()) {
                if (!oldHandles.contains(h)) {
                    driver.switchTo().window(h);
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private String safeUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasValidationHint() {
        List<WebElement> hints = driver.findElements(By.xpath(
                "//*[contains(text(),'совпада') or contains(text(),'одинак')" +
                        " or contains(text(),'Откуда и куда') or contains(text(),'разные')" +
                        " or contains(text(),'Выберите')]"
        ));
        return hints.stream().anyMatch(this::isDisplayedSafe);
    }

    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
