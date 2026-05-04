package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TrainResultsPage extends Page {

    private static final By OFFER_CARD = By.xpath("//*[@data-ti='offer-card']");
    private static final By TRAIN_NAME_BADGE = By.xpath("//*[@data-ti='train-name-badge']");
    private static final By FILTER_SAPSAN = By.xpath("//*[@data-ti='filter-sapsan']");
    private static final By DATE_STRIP_CHIPS = By.xpath(
            "//*[@data-ti='otherDates']//*[@data-ti='panel-chip']"
    );

    public TrainResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public TrainResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(60), Duration.ofMillis(500));
        longWait.until(ExpectedConditions.urlContains("/poezda/"));
        longWait.until(d -> {
            try {
                List<WebElement> cards = d.findElements(OFFER_CARD);
                return cards.size() >= 1 && cards.get(0).isDisplayed();
            } catch (Exception e) {
                return false;
            }
        });
        return this;
    }

    public int countTrainCards() {
        return (int) driver.findElements(OFFER_CARD).stream()
                .filter(this::isDisplayedSafe).count();
    }

    public boolean hasText(String keyword) {
        return driver.findElements(By.xpath("//*[contains(text(),\"" + keyword + "\")]"))
                .stream().anyMatch(this::isDisplayedSafe);
    }

    public boolean hasWagonClassMention() {
        return hasText("Купе") || hasText("Плацкарт") || hasText("Сидячий") || hasText("СВ");
    }

    public List<String> visibleTrainNames() {
        return driver.findElements(TRAIN_NAME_BADGE).stream()
                .filter(this::isDisplayedSafe)
                .map(e -> {
                    try {
                        return e.getText().trim();
                    } catch (Exception x) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public TrainResultsPage clickSapsanFilter() {
        WebElement chip = new WebDriverWait(driver, Duration.ofSeconds(45), Duration.ofMillis(500))
                .until(ExpectedConditions.presenceOfElementLocated(FILTER_SAPSAN));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", chip);
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.elementToBeClickable(FILTER_SAPSAN));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chip);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> {
                        WebElement c = d.findElement(FILTER_SAPSAN);
                        return c.getDomAttribute("class") != null
                                && c.getDomAttribute("class").contains("selected");
                    });
        } catch (Exception ignored) {
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
        if (names.isEmpty()) return false;
        return names.stream().allMatch(n -> n.contains("Сапсан"));
    }

    public TrainResultsPage clickAdjacentDateInStrip() {
        String oldUrl = driver.getCurrentUrl();
        try {
            WebElement chip = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> {
                        List<WebElement> chips = d.findElements(DATE_STRIP_CHIPS);
                        for (WebElement c : chips) {
                            try {
                                if (c.isDisplayed()) return c;
                            } catch (Exception ignored) {
                            }
                        }
                        return null;
                    });
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", chip);
            new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(d -> !d.getCurrentUrl().equals(oldUrl));
        } catch (Exception ignored) {
        }
        return this;
    }
}
