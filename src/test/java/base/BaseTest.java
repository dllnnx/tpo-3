package base;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.DriverFactory;

import java.time.Duration;

public abstract class BaseTest {

    private static final Logger logger = Logger.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        logger.info("Starting browser: " + browser);
        driver = DriverFactory.create(browser);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        wait = new WebDriverWait(driver, Duration.ofSeconds(3), Duration.ofMillis(500));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
