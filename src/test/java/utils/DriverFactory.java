package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {

    private DriverFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static WebDriver create(String browser) {
        return switch (browser.toLowerCase()) {
            case "chrome" -> createChrome();
            case "firefox" -> createFirefox();
            default -> throw new IllegalArgumentException("Unknown browser: " + browser);
        };
    }

    private static WebDriver createChrome() {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments(
                "--window-size=1440,900",
                "--disable-blink-features=AutomationControlled",
                "--disable-notifications",
                "--lang=ru-RU",
                "--remote-allow-origins=*"
        );
        return new ChromeDriver(opts);
    }

    private static WebDriver createFirefox() {
        FirefoxOptions opts = new FirefoxOptions();
        opts.addPreference("intl.accept_languages", "ru-RU,ru");
        return new FirefoxDriver(opts);
    }
}
