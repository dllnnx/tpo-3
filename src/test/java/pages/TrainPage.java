package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;
import utils.DateUtils;

import java.time.LocalDate;

public class TrainPage extends Page {

    private static final By FROM_INPUT = By.xpath("//input[@name='schedule_station_from']");
    private static final By TO_INPUT = By.xpath("//input[@name='schedule_station_to']");
    private static final By NNST1_HIDDEN = By.xpath("//input[@name='nnst1']");
    private static final By NNST2_HIDDEN = By.xpath("//input[@name='nnst2']");
    private static final By DATE_INPUT = By.xpath("//input[contains(@class,'j-date_to')]");
    private static final By DATEPICKER = By.id("ui-datepicker-div");
    private static final By DATEPICKER_NEXT = By.xpath("//a[contains(@class,'ui-datepicker-next')]");
    private static final By DATEPICKER_MONTH_LAST = By.xpath("//div[contains(@class,'ui-datepicker-group-last')]//span[@class='ui-datepicker-month']");
    private static final By DATEPICKER_MONTH_FIRST = By.xpath("//div[contains(@class,'ui-datepicker-group-first')]//span[@class='ui-datepicker-month']");
    private static final By SUBMIT = By.xpath("//button[contains(@class,'j-submit_button')]");

    public TrainPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.TRAIN_URL);
    }

    @Override
    public TrainPage open() {
        super.open();
        return this;
    }

    public TrainPage fillFrom(String prefix, String exactCity) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(FROM_INPUT));
        input.click();
        input.clear();
        input.sendKeys(prefix);
        clickSuggest(true, exactCity);
        wait.until(d -> !d.findElement(NNST1_HIDDEN).getDomAttribute("value").isEmpty());
        return this;
    }

    public TrainPage fillTo(String prefix, String exactCity) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(TO_INPUT));
        input.click();
        input.clear();
        input.sendKeys(prefix);
        clickSuggest(false, exactCity);
        wait.until(d -> !d.findElement(NNST2_HIDDEN).getDomAttribute("value").isEmpty());
        return this;
    }

    private void clickSuggest(boolean fromBlock, String city) {
        String container = fromBlock ? "j-station_from" : "j-station_to";
        By suggestItem = By.xpath(
                "//div[contains(@class,'" + container + "')]//ul[contains(@class,'_level_1')]" +
                        "//li[normalize-space()='" + city + "']/div"
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(suggestItem));
        WebElement el = driver.findElement(suggestItem);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    public TrainPage pickDateInDays(int daysFromToday) {
        LocalDate target = DateUtils.plusDays(daysFromToday);
        wait.until(ExpectedConditions.elementToBeClickable(DATE_INPUT)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(DATEPICKER));
        navigateMonth(target);
        clickDay(target.getDayOfMonth(), target);
        return this;
    }

    private void navigateMonth(LocalDate target) {
        String targetMonthRu = capitalize(DateUtils.monthRu(target));
        for (int i = 0; i < 24; i++) {
            String first = readMonth(DATEPICKER_MONTH_FIRST);
            String last = readMonth(DATEPICKER_MONTH_LAST);
            if (targetMonthRu.equalsIgnoreCase(first) || targetMonthRu.equalsIgnoreCase(last)) {
                return;
            }
            wait.until(ExpectedConditions.elementToBeClickable(DATEPICKER_NEXT)).click();
        }
    }

    private String readMonth(By by) {
        try {
            return driver.findElement(by).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void clickDay(int day, LocalDate target) {
        String targetMonthRu = capitalize(DateUtils.monthRu(target));
        String last = readMonth(DATEPICKER_MONTH_LAST);
        boolean inLastGroup = targetMonthRu.equalsIgnoreCase(last);
        String groupClass = inLastGroup ? "ui-datepicker-group-last" : "ui-datepicker-group-first";
        By dayCell = By.xpath(
                "//div[contains(@class,'" + groupClass + "')]" +
                        "//td[not(contains(@class,'ui-state-disabled'))]" +
                        "/a[normalize-space()='" + day + "']"
        );
        wait.until(ExpectedConditions.elementToBeClickable(dayCell)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(DATEPICKER));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public TrainResultsPage submitSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(SUBMIT)).click();
        return new TrainResultsPage(driver, wait);
    }
}
