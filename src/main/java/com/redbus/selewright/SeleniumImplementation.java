package com.redbus.selewright;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class implements Selewright Interface using Selenium. For method documentation please refer to the interface.
 * Please note: Few methods in this class are yet to be implemented as Selenium is not the preferred choice to perform these tasks. Feel free to implement them as per your requirements.
 */
public class SeleniumImplementation implements Selewright {

    private final WebDriver driver;
    private JavascriptExecutor js;
    private static int defaultWait = 30;
    String parentWindowHandle;

    public SeleniumImplementation(WebDriver driver) {
        this.driver = driver;
        js = (JavascriptExecutor) driver;
        try {
            this.parentWindowHandle = driver.getWindowHandle();
        } catch (Exception e) {

        }
    }

    private By getLocator(String address) {
        for (int i = 0; i < address.length(); i++) {
            char c = address.charAt(i);
            if (c != '(') {
                if (c == '/') {
                    return By.xpath(address);
                } else {
                    return By.cssSelector(address);
                }
            }
        }
        return null;
    }

    @Override
    public WebElement findElement(String address) {
        waitForPresenceOfElement(address, getGlobalWait());
        return driver.findElement(getLocator(address));
    }

    @Override
    public List<WebElement> findElements(String address) {
        waitForPresenceOfAllElements(address, getGlobalWait());
        return driver.findElements(getLocator(address));
    }

    @Override
    public void click(String address) {
        waitForElementToBeClickable(address, getGlobalWait());
        List<WebElement> elements = findElements(address);
        if(elements.size()>1){
            System.out.println("Multiple elements found for the locator: "+address+". Clicking on the first one.");
            throw new RuntimeException("Multiple elements found for the locator: "+address+". Clicking on the first one.");
        }
        highlight(elements.get(0));
        try {
            findElement(address).click();
        } catch (Exception e) {
            System.out.println("Normal click did not work for" + address + " Trying with js");
            js.executeScript("arguments[0].click();", elements.get(0));
        }
        unhighlight(elements.get(0));
        waitUntilPageLoadComplete();
    }

    @Override
    public void tap(String address) {
        click(address);
    }

    @Override
    public void clear(String address) {
        waitForElementToBeVisible(address, getGlobalWait());
        findElement(address).clear();
    }

    @Override
    public void openUrl(String url) {
        driver.get(url);
        waitUntilPageLoadComplete();
    }

    @Override
    public String getPageTitle() {
        waitUntilPageLoadComplete();
        return driver.getTitle();
    }

    @Override
    public String getCurrentPageUrl() {
        waitUntilPageLoadComplete();
        return driver.getCurrentUrl();
    }

    @Override
    public String getHtmlPageContent() {
        waitUntilPageLoadComplete();
        return (String) js.executeScript("return document.documentElement.outerHTML;");
    }

    @Override
    public void enterText(String address, String text) {
        waitForElementToBeClickable(address, getGlobalWait());
        findElement(address).sendKeys(text);
    }

    @Override
    public String getText(String address) {
        return findElement(address).getText();
    }

    @Override
    public List<String> getAllText(String locator) {
        List<WebElement> elements = findElements(locator);
        List<String> texts = new ArrayList<>();
        for (WebElement element : elements) {
            texts.add(element.getText());
        }
        return texts;
    }

    @Override
    public List<String> getAllAttributes(String locator, String attribute) {
        List<WebElement> elements = findElements(locator);
        List<String> links = new ArrayList<>();
        for (WebElement element : elements) {
            links.add(element.getAttribute(attribute));
        }
        return links;
    }

    @Override
    public String takeScreenshotAsPNG(String locationToStore) {
        String path = locationToStore + "screenshot_" + System.currentTimeMillis() + ".png";
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(path);
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return path;
    }

    @Override
    public String takeScreenshotAsBase64(boolean fullPageScreenshot) {
        //Note: By default only viewport screenshot is taken. Full page screenshot requires Ashot dependency for Selenium which has vulnerabilities
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        return Base64.getEncoder().encodeToString(screenshotBytes);
    }

    @Override
    public int getElementCount(String address) {
        return findElements(address).size();
    }

    @Override
    public void waitForElementToBeVisible(String address, int timeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(getLocator(address)));
    }

    @Override
    public void waitForElementToBeClickable(String address, int timeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(getLocator(address)));
    }

    @Override
    public void waitForPresenceOfElement(String address, int timeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
        wait.until(ExpectedConditions.presenceOfElementLocated(getLocator(address)));
    }

    @Override
    public void waitForPresenceOfAllElements(String address, int timeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getLocator(address)));
    }

    @Override
    public boolean waitUntilPageLoadComplete() {
        for (int i = 0; i < getGlobalWait(); i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (js.executeScript("return document.readyState").equals("complete")) {
                return true;
            }
        }
        System.out.println("Page did not finish loading within " + getGlobalWait() + " seconds");
        return false;
    }

    @Override
    public void selectDropdownBasedOnValue(String address, String value) {
        Select dropdown = new Select(findElement(address));
        dropdown.selectByValue(value);
    }

    @Override
    public boolean isChecked(String address) {
        return findElement(address).isSelected();
    }

    @Override
    public boolean isDisplayed(String address) {
        try {
            waitForElementToBeVisible(address, getGlobalWait());
            findElement(address).isDisplayed();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isViewable(String address) {
        try {
            WebElement element = findElement(address);

            if (!element.isDisplayed()) {
                return false;
            }

            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript("""
                            var elem = arguments[0];

                            if (!elem || !elem.getBoundingClientRect) {
                                return false;
                            }

                            var rect = elem.getBoundingClientRect();

                            if (rect.width === 0 || rect.height === 0) {
                                return false;
                            }

                            if (rect.bottom < 0 || rect.top > window.innerHeight ||
                                rect.right < 0 || rect.left > window.innerWidth) {
                                return false;
                            }

                            var centerX = rect.left + rect.width/2;
                            var centerY = rect.top + rect.height/2;
                            var elementAtPoint = document.elementFromPoint(centerX, centerY);

                            return elem.contains(elementAtPoint) || elem === elementAtPoint;
                            """,
                    element
            );
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isDisplayed(String address, int timeOutInSeconds) {
        try {
            waitForElementToBeVisible(address, timeOutInSeconds);
            findElement(address).isDisplayed();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getAttribute(String address, String attributeName) {
        return findElement(address).getAttribute(attributeName);
    }

    @Override
    public boolean switchToChildWindow() {
        Set<String> windows = driver.getWindowHandles();
        if (windows.size() > 2) {
            System.out.println("More than 2 windows found!!");
            return false;
        } else if (windows.size() < 1) {
            System.out.println("No windows found!!");
            return false;
        } else {
            for (String windowHandle : windows) {
                if (!windowHandle.equals(parentWindowHandle)) {
                    driver.switchTo().window(windowHandle);
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean switchToParentWindow() {
        driver.switchTo().window(parentWindowHandle);
        return true;
    }

    @Override
    public void setCookie(String name, String value, String domain) {
        Cookie c = new Cookie.Builder(name, value).domain(domain).path("/").build();
        driver.manage().addCookie(c);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        refreshPage();
    }

    @Override
    public boolean doesCookieExist(String cookieName) {
        Set<Cookie> cookies = driver.manage().getCookies();
        boolean isCookiePresent = cookies.stream()
                .anyMatch(cookie -> cookie.getName().equals(cookieName));
        return isCookiePresent;
    }

    @Override
    public String getCookieValue(String cookieName) {
        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void deleteCookie(String cookieName) {
        Set<Cookie> cookies = driver.manage().getCookies();
        Set<Cookie> remainingCookies = cookies.stream()
                .filter(cookie -> !cookie.getName().equals(cookieName))
                .collect(Collectors.toSet());
        driver.manage().deleteAllCookies();
        for (Cookie cookie : remainingCookies) {
            driver.manage().addCookie(cookie);
        }

    }

    @Override
    public void refreshPage() {
        driver.navigate().refresh();
        waitUntilPageLoadComplete();
    }

    @Override
    public double[] getBoundingBoxCoordinates(String address) {
        double[] coordinates = new double[2];
        try {
            Long leftCoordinate = (Long) js.executeScript("return arguments[0].getBoundingClientRect().left", findElement(address));
            coordinates[0] = (double) leftCoordinate.doubleValue();
        } catch (ClassCastException e) {
            try {
                Double leftCoordinate = (Double) js.executeScript("return arguments[0].getBoundingClientRect().left", findElement(address));
                coordinates[0] = (double) leftCoordinate;
            } catch (Exception ex) {
                Integer leftCoordinate = (Integer) js.executeScript("return arguments[0].getBoundingClientRect().left", findElement(address));
                coordinates[0] = (double) leftCoordinate.doubleValue();
            }
        }
        try {
            Long topCoordinate = (Long) js.executeScript("return arguments[0].getBoundingClientRect().top", findElement(address));
            coordinates[1] = (double) topCoordinate.doubleValue();
        } catch (ClassCastException e) {
            try {
                Double topCoordinate = (Double) js.executeScript("return arguments[0].getBoundingClientRect().top", findElement(address));
                coordinates[1] = (double) topCoordinate;
            } catch (ClassCastException e1) {
                Integer topCoordinate = (Integer) js.executeScript("return arguments[0].getBoundingClientRect().top", findElement(address));
                coordinates[1] = (double) topCoordinate.doubleValue();
            }
        }
        return coordinates;
    }

    @Override
    public void scrollToElement(String address) {
        js.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'});", findElement(address));
        isDisplayed(address);
    }

    @Override
    public void closeBrowser() {
        driver.quit();
    }

    @Override
    public void closeCurrentTab() {
        driver.close();
    }

    @Override
    public void navigateForward() {
        driver.navigate().forward();
        waitUntilPageLoadComplete();
    }

    @Override
    public void hover(String address) {
        Actions actions = new Actions(driver);
        WebElement element = findElement(address);
        actions.moveToElement(element).click().build().perform();
    }

    @Override
    public void navigateBack() {
        driver.navigate().back();
        waitUntilPageLoadComplete();
    }

    @Override
    public void enterTextUsingKeyboard(String text) {
        Actions actions = new Actions(driver);
        actions.sendKeys(text).perform();
    }

    @Override
    public void swipeHorizontal(double startXPercent, double endXPercent, double yPercent, int durationMs) {
        int screenWidth = driver.manage().window().getSize().getWidth();
        int screenHeight = driver.manage().window().getSize().getHeight();

        int startX = (int) (screenWidth * 0.8);
        int endX = (int) (screenWidth * 0.2);
        int y = (int) (screenHeight * 0.75);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String highlightScript = "let canvas = document.createElement('canvas');" +
                "canvas.style.position = 'absolute';" +
                "canvas.style.top = '0';" +
                "canvas.style.left = '0';" +
                "canvas.style.width = document.documentElement.scrollWidth + 'px';" +
                "canvas.style.height = document.documentElement.scrollHeight + 'px';" +
                "canvas.style.pointerEvents = 'none';" +
                "canvas.style.zIndex = '9999';" +
                "document.body.appendChild(canvas);" +
                "let ctx = canvas.getContext('2d');" +
                "ctx.strokeStyle = 'blue';" +
                "ctx.lineWidth = 5;" +
                "ctx.beginPath();" +
                "ctx.moveTo(arguments[0], arguments[1]);" +
                "ctx.lineTo(arguments[2], arguments[1]);" +
                "ctx.stroke();" +
                "setTimeout(() => canvas.remove(), 2000);";

        js.executeScript(highlightScript, startX, y, endX);

        Actions actions = new Actions(driver);
        actions.moveToElement(driver.findElement(By.tagName("body")), startX, y)
                .clickAndHold()
                .moveByOffset(endX - startX, 0)
                .release()
                .perform();
    }

    @Override
    public String getGAEvents() {
        waitUntilPageLoadComplete();
        return (String) js.executeScript("return JSON.stringify(dataLayer)");
    }

    /**
     * Given the common address of the checkboxes this method clicks on all checkboxes
     *
     * @param address
     */
    public void clickAllElements(String address) {
        waitForElementToBeClickable(address, getGlobalWait());
        for (WebElement element : findElements(address)) {
            highlight(element);
            element.click();
            unhighlight(element);
        }
    }

    /**
     * Validate Element is in ViewPort or not
     *
     * @param
     * @return boolean
     */
    @Override
    public boolean validateElementInViewportOrNot(String locator) {
        try {
            // Using JavaScript to determine if the element is in the viewport
            JavascriptExecutor js = (JavascriptExecutor) driver;
            boolean isVisibleInViewport = (Boolean) js.executeScript(
                    "var elem = arguments[0],                 " +
                            "  box = elem.getBoundingClientRect(),    " +
                            "  cx = box.left + box.width / 2,         " +
                            "  cy = box.top + box.height / 2,         " +
                            "  e = document.elementFromPoint(cx, cy); " +
                            "for (; e; e = e.parentElement) {         " +
                            "  if (e === elem)                        " +
                            "    return true;                         " +
                            "}                                        " +
                            "return false;", findElement(locator));

            return isVisibleInViewport;
        } catch (Exception e) {
            return false;  // Return false if the element is not found or any error occurs
        }
    }

    /**
     * Fetch the newtork response
     *
     * @param apiName , locator , action
     * @return json String
     */
    @Override
    public String fetchResponse(String apiName, String locator, BrowserAction action) {
        return null;
        //TODO
    }

    /**
     * Fetch the newtork response
     *
     * @param apiName , locator , action , text
     * @return json String
     */
    @Override
    public String fetchResponse(String apiName, String locator, BrowserAction action, String text) {
        return null;
        //TODO
    }

    /**
     * Fetch the newtork response
     *
     * @param apiName , locator
     * @return json String
     */
    @Override
    public String fetchRequestPayload(String apiName, String locator, BrowserAction action) {
        return null;
        //TODO
    }

    @Override
    public String[] fetchHeaderRequestPayLoad(String apiName, String locator, BrowserAction action) {
        //TODO
        return null;
    }

    /**
     * Wait for the Response to be Captured
     *
     * @param
     * @return json String
     */
    @Override
    public String waitForResponseToBeCaptured(String result) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));  // Set an appropriate timeout
            wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    // Return true when abc[0] is not null or empty, indicating the response has been captured
                    return result != null && !result.isEmpty();
                }
            });
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * Click using Coordinates of x & y
     *
     * @params xCoordinate , yCoordinate
     */
    @Override
    public void clickUsingCoordinates(int xCoordinates, int yCoordinates) {
        Actions actions = new Actions(driver);
        actions.moveToLocation(xCoordinates, yCoordinates) // Example offset from top-left corner
                .click().build().perform();
    }

    @Override
    public void clickUsingCoordinatesInsideCanvas(int xCoordinates, int yCoordinates) {
        String clickOnPoint = "var canvas = document.getElementsByTagName('canvas')[0];" +
                "if (canvas) {" +
                "  var clickEvent = new MouseEvent('click', {" +
                "    clientX: " + xCoordinates + "," +
                "    clientY: " + yCoordinates + "," +
                "    bubbles: true," +
                "    cancelable: true" +
                "  });" +
                "  canvas.dispatchEvent(clickEvent);" +
                "}" +
                "else {" +
                "  console.error('No canvas element found');" +
                "}";
        js.executeScript(clickOnPoint);
    }

    @Override
    public boolean verifyAlertMessage(String toast_msg) {
        Alert alert = driver.switchTo().alert();

        String alertMsg = alert.getText();
        if (alertMsg.contains(toast_msg)) return true;

        return false;
    }

    @Override
    public void scrollPageUpAndDown(int xCoordinates, int yCoordinates) {
        try {
            js.executeScript("window.scrollBy(" + xCoordinates + "," + yCoordinates + ")");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void scrollPageSlowly(int count) {
        if (count <= 0) {
            count = 2;
        }
        for (int i = 0; i < count / 2; i++) {
            try {
                js.executeScript("window.scrollBy(0,400)");
                Thread.sleep(1000L);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public Map<String, Map<String, Object>> clickAndMock(String address, Map<RequestConditionsToMock, MockResponseToSend> map) {
        //TODO
        return null;
    }

    @Override
    public boolean isRadioChecked(String address) {
        return findElement(address).isSelected();
    }

    @Override
    public Map<String, Map<String, Object>> openUrlAndMock(String url, Map<RequestConditionsToMock, MockResponseToSend> map) {
        //TODO
        return null;
    }

    @Override
    public Map<String, Map<String, Object>> swipeAndMock(String url, Map<RequestConditionsToMock, MockResponseToSend> map) {
        //TODO
        return null;
    }

    @Override
    public Map<String, Map<String, Object>> scrollToElementAndMock(String address, Map<RequestConditionsToMock, MockResponseToSend> map) {
        //TODO
        return null;
    }

    @Override
    public void updateGlobalWait(int waitInSeconds) {
        defaultWait = waitInSeconds;
    }

    private int getGlobalWait() {
        return defaultWait;
    }

    public Map<String, Map<String, Object>> refreshAndMock(Map<RequestConditionsToMock, MockResponseToSend> map) {
        //TODO
        return null;
    }

    private CompletableFuture<String> futureURL(String apiName, String locator, Selewright.BrowserAction action, String text) {
        return null;
        //TODO
    }

    @Override
    public String getCompleteUrlFromRequestCall(String apiName, String locator, Selewright.BrowserAction action, String text) {
        CompletableFuture<String> future = futureURL(apiName, locator, action, text);
        return future.join();
    }

    @Override
    public void scrollPageHeight() {
        scrollPageUpAndDown(0, (int) js.executeScript("return window.innerHeight;"));
    }

    @Override
    public String getCssColor(String classname) {
        WebElement ratingBadge = driver.findElement(By.cssSelector(classname));
        return ratingBadge.getCssValue("color");
    }

    @Override
    public List<String> getAllCssColor(String classname) {
        List<WebElement> elements = findElements(classname);
        List<String> colors = new ArrayList<>();
        for (WebElement element : elements) {
            colors.add(element.getCssValue("background-color"));
        }
        return colors;
    }

    @Override
    public boolean waitTillTextPresent(String locator, int waitTimeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until((ExpectedCondition<Boolean>) driver -> {
                String text = getText(locator);
                return text != null && !text.isEmpty();
            });
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInputEnabled(String address) {
        return findElement(address).isEnabled();
    }

    @Override
    public void highlight(Object elem) {
        WebElement element = (WebElement) elem;
        js.executeScript("arguments[0].style.border = '3px solid green'", element);
        js.executeScript("arguments[0].style.outline='2px solid green'", element);
        js.executeScript("arguments[0].style.padding='5px'", element);
    }

    @Override
    public void unhighlight(Object elem) {
        try {
            WebElement element = (WebElement) elem;
            js.executeScript("arguments[0].style.border = ''", element);
            js.executeScript("arguments[0].style.outline=''", element);
            js.executeScript("arguments[0].style.padding=''", element);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean validateAPI(String apiName, String locator, BrowserAction action) {
        return true;
    }

    @Override
    public String getCompleteUrlFromRequestCall(String apiName) {
        //TODO
        return "";
    }

    @Override
    public void removeElements(String address) {
        findElement(address).sendKeys(Keys.BACK_SPACE);
    }

    @Override
    public void removeElements(String address, String data) {
        for (int i = 0; i < data.length(); i++) {
            findElement(address).sendKeys(Keys.BACK_SPACE);
        }
    }

    /**
     * Enter the text letter by letter
     */
    @Override
    public void enterTextLetterByLetter(String locator, String text) {
        clear(locator);

        // Type each letter with a delay
        for (char c : text.toCharArray()) {
            enterText(locator, String.valueOf(c));
            // Introduce a delay between each key press to simulate real typing
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Fetch the newtork response status
     *
     * @param apiName , locator
     * @return status
     */
    @Override
    public int fetchResponseStatus(String apiName, String locator, BrowserAction action, String text) {
        return 0;
        //TODO
    }

    /**
     * use this method to aport any specific api
     *
     * @param apiName
     * @param locator
     * @param action
     */
    @Override
    public void abortApi(String apiName, String locator, BrowserAction action) {
        //TODO
    }


    public String[] alertMessage = new String[1];

    /**
     * Accept the alert message
     */
    @Override
    public void acceptAlertMessage() {
        try {
            Alert alert = driver.switchTo().alert();
            alertMessage[0] = alert.getText();
            alert.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the alert message
     */
    @Override
    public String getAlertMessage() {
        return alertMessage[0];
    }

    /**
     * returns the string which have opacity 0.2
     */
    @Override
    public String opacityText(String locator) {
        String result = "";
        WebElement element = findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String op = (String) js.executeScript("return window.getComputedStyle(arguments[0], null).getPropertyValue('opacity');", element);

        if (op.equals("0.2")) {
            result = element.getText();
        }

        return result;
    }

    /**
     * Click and trigger the alert and accept the alert
     *
     * @return alert message
     */
    @Override
    public String clickAndAcceptAlert(String locator) {
        click(locator);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.acceptAlertMessage();

        return getAlertMessage();
    }

    /**
     * refresh and trigger the alert and accept the alert
     *
     * @return alert message
     */
    @Override
    public String refreshAndAcceptAlert() {
        refreshPage();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.acceptAlertMessage();

        return getAlertMessage();
    }


    /**
     * Scroll till the bottom of the page
     *
     * @return int ( number of times scrolled )
     */
    @Override
    public int scrollTillEnd(String api) {
        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
        int scrollCount = 0;
        while (true) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            scrollCount++;
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) {
                break;
            }
            lastHeight = newHeight;
        }
        return scrollCount;
    }

    @Override
    public String fetchResponseWithQueryParam(String apiName, String query, String locator, BrowserAction action, String text) {
        return null; //TODO
    }


    /**
     * @return whether image specified in locator came in html or not
     */
    @Override
    public boolean isImageLoaded(String locator) {
        try {
            // Check if the image is displayed
            if (!isDisplayed(locator)) {
                return false;
            }

            // Use JavaScript to check if the image is fully loaded
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return (Boolean) js.executeScript(
                    "return arguments[0].complete && " +
                            "typeof arguments[0].naturalWidth != 'undefined' && " +
                            "arguments[0].naturalWidth > 0", findElement(locator));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public double getYCoordinates(String locator) {
        return findElement(locator).getRect().getY();
    }

    @Override
    public boolean scrollHorizontally(String locator) {
        WebElement element = findElement(locator);
        boolean isHorizontallyScrollable = (boolean) js.executeScript(
                "return arguments[0].scrollWidth > arguments[0].clientWidth;", element);

        if (isHorizontallyScrollable) {
            System.out.println("The element is scrollable horizontally.");

            // Scroll the element to the right
            js.executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;", element);
            return true;
        }
        return false;
    }

    public String getUserAgent() {
        return (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
    }

    @Override
    public Map<String, String> fetchMultipleResponse(List<String> apiNames, String locator, BrowserAction action) {
        //TODO
        return null;
    }

    @Override
    public int countElements(String locator) {
        List<WebElement> list = findElements(locator);
        return list.size();
    }

    @Override
    public boolean validateLinksWithoutHref(String locator) {
        return false;
    }

    /**
     * Get the color of the element
     */
    @Override
    public String getElementColor(String locator) {
        return findElement(locator).getCssValue("color");
    }

    @Override
    public boolean assertRelativePosition(String locator1, String locator2) {
        boolean flag = false;
        int elem1Y = findElement(locator1).getLocation().getY();
        int elem2Y = findElement(locator2).getLocation().getY();

        if (elem1Y < elem2Y) flag = true;
        return flag;
    }

    @Override
    public boolean checkForElementToBeInvisible(String address, int timeInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeInSeconds));
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(getLocator(address)));
    }

    @Override
    public String getCssValue(String locator, String property) {
        return findElement(locator).getCssValue(property);
    }

    @Override
    public void swipeElement() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long screenWidth = (Long) js.executeScript("return window.innerWidth");
        Long screenHeight = (Long) js.executeScript("return window.innerHeight");

        int startX = screenWidth.intValue() / 2;
        int startY = (int) (screenHeight * 0.8);
        int endX = screenWidth.intValue() / 2;
        int endY = (int) (screenHeight * 0.2);

        js.executeScript(
                "var touchObj = new Touch({" +
                        "    identifier: 123," +
                        "    target: document.body," +
                        "    clientX: " + startX + "," +
                        "    clientY: " + startY + "," +
                        "    pageX: " + startX + "," +
                        "    pageY: " + startY + "});" +
                        "var touchEvent = new TouchEvent('touchstart', {" +
                        "    touches: [touchObj]," +
                        "    targetTouches: [touchObj]," +
                        "    changedTouches: [touchObj]," +
                        "    bubbles: true," +
                        "    cancelable: true" +
                        "});" +
                        "document.body.dispatchEvent(touchEvent);"
        );

        js.executeScript(
                "var touchObj = new Touch({" +
                        "    identifier: 123," +
                        "    target: document.body," +
                        "    clientX: " + endX + "," +
                        "    clientY: " + endY + "," +
                        "    pageX: " + endX + "," +
                        "    pageY: " + endY + "});" +
                        "var touchEvent = new TouchEvent('touchend', {" +
                        "    touches: []," +
                        "    targetTouches: []," +
                        "    changedTouches: [touchObj]," +
                        "    bubbles: true," +
                        "    cancelable: true" +
                        "});" +
                        "document.body.dispatchEvent(touchEvent);"
        );
    }

    @Override
    public void swipeDown() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long screenWidth = (Long) js.executeScript("return window.innerWidth");
        Long screenHeight = (Long) js.executeScript("return window.innerHeight");

        int endX = screenWidth.intValue() / 2;
        int endY = (int) (screenHeight * 0.8);
        int startX = screenWidth.intValue() / 2;
        int startY = (int) (screenHeight * 0.2);

        js.executeScript(
                "var touchObj = new Touch({" +
                        "    identifier: 123," +
                        "    target: document.body," +
                        "    clientX: " + startX + "," +
                        "    clientY: " + startY + "," +
                        "    pageX: " + startX + "," +
                        "    pageY: " + startY + "});" +
                        "var touchEvent = new TouchEvent('touchstart', {" +
                        "    touches: [touchObj]," +
                        "    targetTouches: [touchObj]," +
                        "    changedTouches: [touchObj]," +
                        "    bubbles: true," +
                        "    cancelable: true" +
                        "});" +
                        "document.body.dispatchEvent(touchEvent);"
        );

        js.executeScript(
                "var touchObj = new Touch({" +
                        "    identifier: 123," +
                        "    target: document.body," +
                        "    clientX: " + endX + "," +
                        "    clientY: " + endY + "," +
                        "    pageX: " + endX + "," +
                        "    pageY: " + endY + "});" +
                        "var touchEvent = new TouchEvent('touchend', {" +
                        "    touches: []," +
                        "    targetTouches: []," +
                        "    changedTouches: [touchObj]," +
                        "    bubbles: true," +
                        "    cancelable: true" +
                        "});" +
                        "document.body.dispatchEvent(touchEvent);"
        );
    }

    @Override
    public void clickUsingJavascriptExecutor(String locator) {
        js.executeScript("arguments[0].click();", locator);
    }

    @Override
    public void mockApiResponseCode(int rCode, String apiName, String locator, BrowserAction action) {
    }


    /*
     * Fetch the complete URL for the matched api triggered
     */
    @Override
    public String fetchRequestUrl(String apiName, String locator, BrowserAction action, String text) {
        return null;
    }

    /*
     * Scroll to the top of the page
     */
    @Override
    public void scrollToTheTopOfPage() {
        js.executeScript("window.scrollTo(0, 0);");
    }

    /*
     * Reached the end of the page or not
     */
    @Override
    public boolean isAtPageEnd() {
        long scrollHeight = (long) js.executeScript("return document.documentElement.scrollHeight");
        long clientHeight = (long) js.executeScript("return document.documentElement.clientHeight");
        long scrollTop = (long) js.executeScript("return document.documentElement.scrollTop");

        return (scrollTop + clientHeight) >= scrollHeight;
    }

    /**
     * pauses request for sometime
     */
    public boolean pauseRequest(String apiName, String locator, List<String> locators, BrowserAction action) {
        return false;
        // todo implementation
    }

    @Override
    public boolean isTouchDevice() {
        return false;
    }


}
