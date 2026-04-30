package base;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.itmo.exceptions.InvalidPropertiesException;
import utils.Constants;
import utils.Context;
import utils.Properties;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {

    private static final Logger logger = Logger.getLogger(BaseTest.class);

    public Context context;
    public List<WebDriver> driverList;

    @BeforeEach
    public void setUp() {
        context = new Context();
        driverList = new ArrayList<>();
        try {
            Properties.getInstance().reading(context);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }

        if (context.getGeckodriver() != null) {
            System.setProperty(Constants.WEBDRIVER_FIREFOX_DRIVER, context.getGeckodriver());
            driverList.add(new FirefoxDriver());
        }
        if (context.getChromedriver() != null) {
            System.setProperty(Constants.WEBDRIVER_CHROME_DRIVER, context.getChromedriver());
            driverList.add(new ChromeDriver());
        }
        if (driverList.isEmpty()) throw new InvalidPropertiesException();
    }

    @BeforeEach
    public void tearDown() {
        driverList.forEach(WebDriver::quit);
    }

}
