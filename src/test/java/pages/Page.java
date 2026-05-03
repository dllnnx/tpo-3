package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Page {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private final String url;

    protected Page(WebDriver driver, WebDriverWait wait, String url) {
        this.driver = driver;
        this.wait = wait;
        this.url = url;
    }

    public Page open() {
        if (url != null && (driver.getCurrentUrl() == null || !driver.getCurrentUrl().equals(url))) {
            driver.get(url);
        }
        hideOverlayWidgets();
        return this;
    }

    protected void hideOverlayWidgets() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('iframe.tutu-chat-widget-iframe, " +
                            "[class*=\"tutuSmart\"], [data-ti=\"disclaimer_wrapper\"], " +
                            "[class*=\"chat-widget\"]').forEach(e => e.style.display = 'none');"
            );
        } catch (Exception ignored) {
        }
    }

}