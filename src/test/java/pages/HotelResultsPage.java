package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HotelResultsPage extends Page {

    public HotelResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public HotelResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60), Duration.ofMillis(500));
        longWait.until(ExpectedConditions.urlContains("hotel.tutu.ru"));
        longWait.until(d -> countHotelCards() >= 1 || hasText("отел") || hasText("Отел"));
        return this;
    }

    public int countHotelCards() {
        List<WebElement> ratings = driver.findElements(By.xpath(
                "//*[contains(text(),'отзыв') or contains(text(),'Отел') or contains(text(),'хостел')]"
        ));
        return (int) ratings.stream().filter(this::isDisplayedSafe).count();
    }

    public boolean hasText(String keyword) {
        return driver.findElements(By.xpath("//*[contains(text(),\"" + keyword + "\")]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    public boolean hasPriceFormat() {
        return driver.findElements(By.xpath("//*[contains(text(),'₽')]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
