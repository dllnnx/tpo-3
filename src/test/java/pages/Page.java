package pages;

import lombok.AllArgsConstructor;
import org.openqa.selenium.WebDriver;

@AllArgsConstructor
public abstract class Page {

    public WebDriver driver;
    private final String url;

    public Page open() {
        if (driver.getCurrentUrl() == null || !driver.getCurrentUrl().equals(url)) {
            driver.get(url);
        }

        return this;
    }
}