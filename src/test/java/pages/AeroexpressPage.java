package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;

import java.time.Duration;

public class AeroexpressPage extends Page {

    public AeroexpressPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, Constants.AEROEXPRESS_URL);
    }

    @Override
    public AeroexpressPage open() {
        super.open();
        return this;
    }

    public AeroexpressPage waitForLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.urlContains("aeroexpress"));
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> d.findElements(By.tagName("body")).size() > 0
                        && d.findElement(By.tagName("body")).getText().length() > 100);
        return this;
    }

    public AeroexpressPage waitForSchedulePageLoad() {
        wait.until(ExpectedConditions.urlContains("/aeroexpress/schedule/"));
        wait.until(d -> d.findElement(By.tagName("body")).getText().length() > 100);
        return this;
    }

    public boolean hasAirportName() {
        String[] airports = {"Шереметьево", "Внуково", "Домодедово", "Павелецк", "Белорусский", "Киевский"};
        for (String a : airports) {
            if (hasText(a)) return true;
        }
        return false;
    }

    public boolean hasText(String keyword) {
        return driver.findElements(By.xpath("//*[contains(text(),\"" + keyword + "\")]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    private boolean isDisplayedSafe(WebElement el) {
        try {
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public AeroexpressPage clickScheduleButton() {
        WebElement scheduleLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/aeroexpress/schedule/')]")
        ));
        scheduleLink.click();
        return this;
    }
}
