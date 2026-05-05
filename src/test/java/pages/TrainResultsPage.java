package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TrainResultsPage extends Page {

    private static final By OFFER_CARD = By.xpath("//*[@data-ti='offer-card']");
    private static final By TRAIN_NAME_BADGE = By.xpath("//*[@data-ti='train-name-badge']");
    private static final By FILTER_SAPSAN = By.xpath("//*[@data-ti='filter-sapsan']");
    private static final By DATE_STRIP_CHIPS = By.xpath(
            "//*[@data-ti='otherDates']//*[@data-ti='panel-chip']"
    );

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(60);

    private static final Duration FILTER_TIMEOUT = Duration.ofSeconds(15);

    public TrainResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public TrainResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(d -> {
            try {
                List<WebElement> cards = d.findElements(OFFER_CARD);
                return !cards.isEmpty() && cards.get(0).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
        return this;
    }

    public int countTrainCards() {
        List<WebElement> cards = driver.findElements(OFFER_CARD);
        int count = 0;
        for (WebElement card : cards) {
            if (isDisplayedSafe(card)) {
                count++;
            }
        }
        return count;
    }

    public boolean hasText(String keyword) {
        List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(),\"" + keyword + "\")]"));
        for (WebElement el : elements) {
            if (isDisplayedSafe(el)) {
                return true;
            }
        }
        return false;
    }

    public List<String> visibleTrainNames() {
        List<WebElement> badges = driver.findElements(TRAIN_NAME_BADGE);
        List<String> names = new ArrayList<>();
        for (WebElement badge : badges) {
            if (isDisplayedSafe(badge)) {
                try {
                    String text = badge.getText().trim();
                    if (!text.isEmpty()) {
                        names.add(text);
                    }
                } catch (Exception e) {
                    // Skip elements that can't be read
                }
            }
        }
        return names;
    }

    public TrainResultsPage clickSapsanFilter() {
        WebDriverWait filterWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(500));
        WebElement chip = filterWait.until(ExpectedConditions.presenceOfElementLocated(FILTER_SAPSAN));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", chip);

        WebDriverWait clickWait = new WebDriverWait(driver, Duration.ofSeconds(8));
        clickWait.until(ExpectedConditions.elementToBeClickable(FILTER_SAPSAN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chip);

        // Wait for filter to be marked as selected (non-critical)
        try {
            filterWait.until(d -> {
                WebElement c = d.findElement(FILTER_SAPSAN);
                String cls = c.getDomAttribute("class");
                return cls != null && cls.contains("selected");
            });
        } catch (Exception e) {
            // Filter may still be applied even if class doesn't update immediately
        }

        return this;
    }

    public boolean isSapsanFilterActive() {
        try {
            String cls = driver.findElement(FILTER_SAPSAN).getDomAttribute("class");
            return cls != null && cls.contains("selected");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean allVisibleTrainsAreSapsan() {
        List<String> names = visibleTrainNames();
        if (names.isEmpty()) {
            return false;
        }
        for (String name : names) {
            if (!name.contains("Сапсан")) {
                return false;
            }
        }
        return true;
    }

    public TrainResultsPage clickAdjacentDateInStrip() {
        String oldUrl = driver.getCurrentUrl();

        try {
            WebDriverWait chipWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement chip = chipWait.until(d -> {
                List<WebElement> chips = d.findElements(DATE_STRIP_CHIPS);
                for (WebElement c : chips) {
                    if (isDisplayedSafe(c)) {
                        return c;
                    }
                }
                return null;
            });

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", chip);

            WebDriverWait navWait = new WebDriverWait(driver, Duration.ofSeconds(20));
            navWait.until(d -> !d.getCurrentUrl().equals(oldUrl));
        } catch (Exception e) {
            // Date navigation failed - may not have adjacent dates available
        }

        return this;
    }
}
