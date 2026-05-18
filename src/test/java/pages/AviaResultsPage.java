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
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AviaResultsPage extends Page {

    private static final By AIRLINE_NAME = By.xpath("//div[@data-ti='card-badges'] | //motion.div[@data-ti='card-badges']");
    private static final By FLIGHT_CARD = By.xpath("//div[@data-ti='card-badges'] | //motion.div[@data-ti='card-badges']");
    private static final By FILTERS_PANEL = By.xpath(
            "//*[@data-ti='filterGroups'] | //*[@data-ti='filters']"
    );
    private static final By FILTERS_OPEN_BUTTON = By.xpath(
            "//button[contains(normalize-space(),'Фильтры')]"
    );
    private static final By SORT_TRIGGER = By.xpath(
            "//*[@data-ti='sorting'] | //button[contains(@data-ti,'sort')]"
                    + " | //button[contains(normalize-space(),'Сначала')]"
                    + " | //*[@data-ti='panel-chip' and contains(.,'Сначала')]"
    );
    private static final By OFFER_PRICE = By.xpath(
            "//*[@data-ti='card-badges']/ancestor::*[contains(@data-ti,'offer') or contains(@class,'offer')]"
                    + "//*[contains(text(),'₽')]"
                    + " | //*[contains(@data-ti,'offer')]//*[contains(text(),'₽')]"
                    + " | //div[@data-ti='card-badges']/ancestor::div[.//*[contains(text(),'₽')]][1]//*[contains(text(),'₽')]"
    );

    private static final Map<String, String> FILTER_DATA_TI_SUFFIX = Map.of(
            "С багажом", "with_baggage",
            "Без багажа", "without_baggage",
            "Прямой", "transfers_0",
            "1 пересадка", "transfers_1"
    );

    private static final Map<String, String> SWITCH_FILTER_DATA_TI = Map.of(
            "С бронированием", "switchFilter-avia_bookable"
    );

    private static final Pattern HHMM = Pattern.compile("^\\d{2}:\\d{2}$");
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\d[\\d\\s\\u202f\\u00a0]*)\\s*₽");

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(50);
    private static final Duration FILTER_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration UPDATE_TIMEOUT = Duration.ofSeconds(20);

    public AviaResultsPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait, null);
    }

    public AviaResultsPage waitForResults() {
        WebDriverWait longWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));
        longWait.until(d -> countVisibleFlightCards() >= 1
                || countDepartureTimes() >= 1
                || hasFilterControls()
                || hasText("Авиабилеты")
                || hasText("Прямой"));
        return this;
    }

    public AviaResultsPage waitForResultsUpdate() {
        WebDriverWait updateWait = new WebDriverWait(driver, UPDATE_TIMEOUT, Duration.ofMillis(300));
        updateWait.until(d -> countVisibleFlightCards() >= 1);

        int stablePolls = 0;
        int lastCount = -1;
        String lastSignature = "";
        long deadline = System.currentTimeMillis() + UPDATE_TIMEOUT.toMillis();
        while (System.currentTimeMillis() < deadline && stablePolls < 2) {
            int count = countVisibleFlightCards();
            String signature = resultsSignature();
            if (count >= 1 && count == lastCount && signature.equals(lastSignature)) {
                stablePolls++;
            } else {
                stablePolls = 0;
            }
            lastCount = count;
            lastSignature = signature;
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return this;
    }

    public AviaResultsPage openFiltersPanel() {
        hideOverlayWidgets();
        WebDriverWait filterWait = new WebDriverWait(driver, RESULTS_TIMEOUT, Duration.ofMillis(500));

        List<WebElement> openButtons = driver.findElements(FILTERS_OPEN_BUTTON);
        for (WebElement btn : openButtons) {
            if (isDisplayedSafe(btn)) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", btn);
                break;
            }
        }

        filterWait.until(d -> hasFilterControls());

        WebElement panel = findFiltersContainer();
        if (panel != null) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'start'});", panel);
        }
        return this;
    }

    public AviaResultsPage scrollFiltersPanelDown() {
        WebElement panel = findFiltersContainer();
        if (panel == null) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, window.innerHeight * 0.8);");
            return this;
        }
        ((JavascriptExecutor) driver).executeScript(
                "const panel = arguments[0];"
                        + "let scrollable = panel;"
                        + "while (scrollable && scrollable !== document.body) {"
                        + "  const style = window.getComputedStyle(scrollable);"
                        + "  const overflowY = style.overflowY;"
                        + "  if ((overflowY === 'auto' || overflowY === 'scroll')"
                        + "      && scrollable.scrollHeight > scrollable.clientHeight + 20) break;"
                        + "  scrollable = scrollable.parentElement;"
                        + "}"
                        + "if (!scrollable) scrollable = panel;"
                        + "scrollable.scrollTop = scrollable.scrollTop + scrollable.clientHeight * 0.85;"
                        + "return scrollable.scrollTop;",
                panel);

        WebElement carrierSection = findFilterSection("filter_avia_carrier");
        if (carrierSection != null) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center'});", carrierSection);
        }
        return this;
    }

    public AviaResultsPage clickFilter(String label) {
        hideOverlayWidgets();

        if (SWITCH_FILTER_DATA_TI.containsKey(label)) {
            clickSwitchFilter(SWITCH_FILTER_DATA_TI.get(label));
            waitForResultsUpdate();
            return this;
        }

        String suffix = FILTER_DATA_TI_SUFFIX.get(label);
        if (suffix != null) {
            clickShortcutFilter(suffix);
        } else {
            clickFilterByLabelText(label);
        }

        waitForResultsUpdate();
        return this;
    }

    public boolean isFilterActive(String label) {
        if (SWITCH_FILTER_DATA_TI.containsKey(label)) {
            return isSwitchFilterActive(SWITCH_FILTER_DATA_TI.get(label));
        }

        String suffix = FILTER_DATA_TI_SUFFIX.get(label);
        if (suffix != null) {
            By chipLocator = shortcutChipLocator(suffix);
            for (WebElement chip : driver.findElements(chipLocator)) {
                if (isFilterChipSelected(chip)) {
                    return true;
                }
            }
            return false;
        }

        return isFilterByLabelSelected(label);
    }

    public AviaResultsPage selectSort(String option) {
        hideOverlayWidgets();
        openSortMenu();

        String optionNeedle = option.toLowerCase().contains("деш") ? "деш" : option;
        WebDriverWait sortWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(300));
        WebElement optionEl = sortWait.until(d -> {
            for (WebElement el : d.findElements(By.xpath("//*[contains(text(), " + optionNeedle + ")]"))) {
                if (isDisplayedSafe(el)) {
                    return el;
                }
            }
            return null;
        });

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", optionEl);
        waitForResultsUpdate();

        return this;
    }

    public List<Integer> getVisibleOfferPrices() {
        List<Integer> prices = new ArrayList<>();
        for (WebElement priceEl : driver.findElements(OFFER_PRICE)) {
            if (!isDisplayedSafe(priceEl)) {
                continue;
            }
            Integer price = parsePrice(priceEl.getText());
            if (price != null) {
                prices.add(price);
            }
            if (prices.size() >= 5) {
                break;
            }
        }

        if (prices.size() < 2) {
            for (WebElement card : driver.findElements(FLIGHT_CARD)) {
                if (!isDisplayedSafe(card)) {
                    continue;
                }
                WebElement container = card.findElement(By.xpath(
                        "./ancestor::div[.//*[contains(text(),'₽')]][1] | ./ancestor::motion.div[.//*[contains(text(),'₽')]][1]"
                ));
                Integer price = parsePrice(container.getText());
                if (price != null) {
                    prices.add(price);
                }
                if (prices.size() >= 5) {
                    break;
                }
            }
        }

        return prices;
    }

    public boolean isSortedByPriceAscending() {
        List<Integer> prices = getVisibleOfferPrices();
        if (prices.size() < 2) {
            return true;
        }
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                return false;
            }
        }
        return true;
    }

    public int countVisibleFlightCards() {
        List<WebElement> cards = driver.findElements(FLIGHT_CARD);
        int count = 0;
        for (WebElement card : cards) {
            if (isDisplayedSafe(card)) {
                count++;
            }
        }
        return count;
    }

    public boolean resultsMention(String keyword) {
        return hasText(keyword);
    }

    public int countDepartureTimes() {
        List<WebElement> candidates = driver.findElements(By.xpath(
                "//span[contains(text(),':') and string-length(normalize-space())=5]"
        ));

        int count = 0;
        for (WebElement el : candidates) {
            if (isDisplayedSafe(el)) {
                String text = el.getText().trim();
                if (HHMM.matcher(text).matches()) {
                    count++;
                }
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

    public boolean hasAirlineName() {
        Set<String> airlines = Set.of("Аэрофлот", "S7", "Победа", "Россия", "Utair", "Etihad", "Qatar", "Lufthansa");
        return driver.findElements(AIRLINE_NAME).stream()
                .map(WebElement::getText)
                .anyMatch(airlines::contains);
    }

    private void openSortMenu() {
        WebDriverWait sortWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(300));
        WebElement trigger = sortWait.until(d -> {
            for (WebElement el : d.findElements(SORT_TRIGGER)) {
                if (isDisplayedSafe(el)) {
                    return el;
                }
            }
            for (WebElement el : d.findElements(By.xpath(
                    "//*[contains(normalize-space(),'Сначала') or contains(normalize-space(),'сортиров')]"
            ))) {
                if (isDisplayedSafe(el) && (el.getTagName().equalsIgnoreCase("button")
                        || "button".equals(el.getDomAttribute("role")))) {
                    return el;
                }
            }
            return null;
        });
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", trigger);
    }

    private void clickShortcutFilter(String suffix) {
        By chipLocator = shortcutChipLocator(suffix);
        WebDriverWait filterWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(500));
        WebElement chip = filterWait.until(d -> {
            List<WebElement> candidates = d.findElements(chipLocator);
            return candidates.isEmpty() ? null : candidates.get(0);
        });

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", chip);

        try {
            filterWait.until(d -> {
                for (WebElement candidate : d.findElements(chipLocator)) {
                    if (isFilterChipSelected(candidate)) {
                        return true;
                    }
                }
                return false;
            });
        } catch (Exception ignored) {
        }
    }

    private void clickFilterByLabelText(String label) {
        WebDriverWait filterWait = new WebDriverWait(driver, FILTER_TIMEOUT, Duration.ofMillis(500));
        WebElement target = filterWait.until(d -> findClickableFilterByLabel(label));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", target);
    }

    private void clickSwitchFilter(String dataTi) {
        By locator = By.xpath("//*[@data-ti='" + dataTi + "']");
        WebElement toggle = new WebDriverWait(driver, FILTER_TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        WebElement clickable = toggle.findElements(By.xpath(".//input | .//button | .//*[@role='switch']")).stream()
                .filter(this::isDisplayedSafe)
                .findFirst()
                .orElse(toggle);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", clickable);
    }

    private boolean isSwitchFilterActive(String dataTi) {
        try {
            WebElement toggle = driver.findElement(By.xpath("//*[@data-ti='" + dataTi + "']"));
            WebElement input = toggle.findElement(By.xpath(".//input"));
            String checked = input.getDomAttribute("checked");
            if ("true".equals(checked)) {
                return true;
            }
            String ariaChecked = input.getDomAttribute("aria-checked");
            return "true".equals(ariaChecked);
        } catch (Exception e) {
            String cls = driver.findElement(By.xpath("//*[@data-ti='" + dataTi + "']")).getDomAttribute("class");
            return cls != null && cls.contains("selected");
        }
    }

    private boolean isFilterByLabelSelected(String label) {
        WebElement el = findClickableFilterByLabel(label);
        if (el == null) {
            return false;
        }
        if (isFilterChipSelected(el) || isCheckboxChecked(el)) {
            return true;
        }

        WebElement current = el;
        for (int depth = 0; depth < 6 && current != null; depth++) {
            if (isFilterChipSelected(current) || isCheckboxChecked(current)) {
                return true;
            }
            String cls = current.getDomAttribute("class");
            if (cls != null && (cls.contains("selected") || cls.contains("checked"))) {
                return true;
            }
            try {
                current = current.findElement(By.xpath("./parent::*"));
            } catch (Exception e) {
                break;
            }
        }

        List<WebElement> inputs = driver.findElements(By.xpath(
                "//input[@type='checkbox' and ("
                        + "@checked='true' or @aria-checked='true')]"
                        + "/following::*[contains(normalize-space(),'" + label + "')]"
                        + " | //label[contains(normalize-space(),'" + label + "')]/input"
                        + "[@checked='true' or @aria-checked='true']"
        ));
        return !inputs.isEmpty();
    }

    private WebElement findClickableFilterByLabel(String label) {
        String[] xpaths = {
                "//*[@data-ti='filter_avia_carrier' or @data-ti='filter_avia_departure_0']"
                        + "//*[contains(normalize-space(),'" + label + "')]"
                        + "/ancestor::*[contains(@data-ti,'shortcut') or self::label or self::button][1]",
                "//*[contains(@data-ti,'shortcut') or contains(@data-ti,'filter_')]"
                        + "//*[contains(normalize-space(),'" + label + "')]"
                        + "/ancestor::*[contains(@data-ti,'shortcut') or self::label][1]",
                "//*[contains(normalize-space(),'" + label + "')]"
                        + "/ancestor::button[1] | //*[contains(normalize-space(),'" + label + "')]/ancestor::label[1]"
        };
        for (String xpath : xpaths) {
            for (WebElement el : driver.findElements(By.xpath(xpath))) {
                if (isDisplayedSafe(el)) {
                    return el;
                }
            }
        }
        return null;
    }

    private WebElement findFilterSection(String dataTi) {
        List<WebElement> sections = driver.findElements(By.xpath("//*[@data-ti='" + dataTi + "']"));
        for (WebElement section : sections) {
            if (isDisplayedSafe(section)) {
                return section;
            }
        }
        return null;
    }

    private boolean hasFilterControls() {
        return !driver.findElements(FILTERS_PANEL).isEmpty()
                || !driver.findElements(shortcutChipLocator("with_baggage")).isEmpty()
                || hasText("Багаж");
    }

    private WebElement findFiltersContainer() {
        for (WebElement panel : driver.findElements(FILTERS_PANEL)) {
            if (isDisplayedSafe(panel)) {
                return panel;
            }
        }
        return null;
    }

    private By shortcutChipLocator(String suffix) {
        return By.xpath(
                "//*[contains(@data-ti,'shortcut') and contains(@data-ti,'" + suffix + "')]"
        );
    }

    private String resultsSignature() {
        List<WebElement> cards = driver.findElements(FLIGHT_CARD);
        StringBuilder sb = new StringBuilder();
        int added = 0;
        for (WebElement card : cards) {
            if (!isDisplayedSafe(card)) {
                continue;
            }
            try {
                String text = card.getText().replaceAll("\\s+", " ").trim();
                if (!text.isEmpty()) {
                    sb.append(text, 0, Math.min(40, text.length()));
                    sb.append('|');
                }
            } catch (Exception ignored) {
            }
            if (++added >= 3) {
                break;
            }
        }
        return sb.toString();
    }

    private Integer parsePrice(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        Matcher matcher = PRICE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return null;
        }
        String digits = matcher.group(1).replaceAll("[^\\d]", "");
        if (digits.isEmpty()) {
            return null;
        }
        return Integer.parseInt(digits);
    }

    private boolean isFilterChipSelected(WebElement chip) {
        String cls = chip.getDomAttribute("class");
        if (cls != null && cls.contains("selected")) {
            return true;
        }
        String ariaChecked = chip.getDomAttribute("aria-checked");
        return "true".equals(ariaChecked);
    }

    private boolean isCheckboxChecked(WebElement el) {
        try {
            WebElement input = el.findElement(By.xpath(".//input"));
            return "true".equals(input.getDomAttribute("checked"))
                    || "true".equals(input.getDomAttribute("aria-checked"));
        } catch (Exception e) {
            return false;
        }
    }

}
