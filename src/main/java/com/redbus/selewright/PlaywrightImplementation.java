package com.redbus.selewright;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * This class implements Selewright Interface using Playwright. For method documentation please refer to the interface.
 */
public class PlaywrightImplementation implements Selewright {
    Page page;
    Page parentPage;
    BrowserContext context;
    private static final int defaultWait = 30;
    OtherHelpers otherHelpers;

    public PlaywrightImplementation(Page page) {
        this.page = page;
        this.parentPage = page;
        if (page != null) {
            this.context = page.context();
        }
        otherHelpers = new OtherHelpers();
    }

    @Override
    public Locator findElement(String locatorVal) {
        return page.locator(locatorVal).first();
    }

    @Override
    public List<Locator> findElements(String locatorVal) {
        return page.locator(locatorVal).all();
    }

    @Override
    public void click(String locator) {
        Locator element = findElement(locator);
        highlight(element);
        findElement(locator).click();
        unhighlight(element);
        waitUntilPageLoadComplete();
    }

    @Override
    public void tap(String locator) {
        Locator element = findElement(locator);
        highlight(element);
        findElement(locator).tap();
        unhighlight(element);
        waitUntilPageLoadComplete();
    }

    @Override
    public boolean isTouchDevice() {
        return (boolean) page.evaluate("() => 'ontouchstart' in window || navigator.maxTouchPoints > 0");
    }

    @Override
    public void hover(String locator) {
        findElement(locator).hover();
    }

    @Override
    public Map<String, Map<String, Object>> clickAndMock(String locator, Map<RequestConditionsToMock, MockResponseToSend> map) {
        Map<String, Map<String, Object>> mockMap = sendMockResponse(map);
        page.waitForRequest("**/*", () -> {
            click(locator);
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        stopMocking();
        return mockMap;
    }

    @Override
    public Map<String, Map<String, Object>> swipeAndMock(String locator, Map<RequestConditionsToMock, MockResponseToSend> map) {
        Map<String, Map<String, Object>> mockMap = sendMockResponse(map);
        page.waitForRequest("**/*", this::swipeElement);
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        stopMocking();
        return mockMap;
    }

    @Override
    public Map<String, Map<String, Object>> openUrlAndMock(String url, Map<RequestConditionsToMock, MockResponseToSend> map) {
        Map<String, Map<String, Object>> mockMap = sendMockResponse(map);
        page.waitForRequest("**/*", () -> {
            openUrl(url);
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        stopMocking();
        return mockMap;
    }

    @Override
    public Map<String, Map<String, Object>> scrollToElementAndMock(String address, Map<RequestConditionsToMock, MockResponseToSend> map) {
        Map<String, Map<String, Object>> mockMap = sendMockResponse(map);
        page.waitForRequest("**/*", () -> {
            scrollToElement(address);
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        stopMocking();
        return mockMap;
    }

    @Override
    public boolean isRadioChecked(String address) {
        return findElement(address).isChecked();
    }

    @Override
    public void updateGlobalWait(int waitInSeconds) {
        page.context().setDefaultTimeout((double) waitInSeconds * 1000);
    }

    @Override
    public Map<String, Map<String, Object>> refreshAndMock(Map<RequestConditionsToMock, MockResponseToSend> map) {
        Map<String, Map<String, Object>> mockMap = sendMockResponse(map);
        page.waitForRequest("**/*", this::refreshPage);
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        stopMocking();
        return mockMap;
    }

    @Override
    public void clear(String locator) {
        findElement(locator).clear();
        waitUntilPageLoadComplete();
    }

    @Override
    public void openUrl(String url) {
        page.navigate(url);
        waitUntilPageLoadComplete();
    }

    @Override
    public String getPageTitle() {
        return page.title();
    }

    @Override
    public String getCurrentPageUrl() {
        return page.url();
    }

    @Override
    public String getHtmlPageContent() {
        return page.content();
    }

    @Override
    public void enterText(String locator, String text) {
        findElement(locator).fill(text);
        waitUntilPageLoadComplete();
    }

    @Override
    public String getText(String locator) {
        waitForPresenceOfElement(locator, 5);
        return findElement(locator).textContent();
    }

    @Override
    public List<String> getAllText(String address) {
        List<Locator> elements = findElements(address);
        List<String> texts = new ArrayList<>();
        for (Locator element : elements) {
            texts.add(element.textContent());
        }
        return texts;
    }

    @Override
    public List<String> getAllAttributes(String address, String attribute) {
        List<Locator> elements = findElements(address);
        List<String> links = new ArrayList<>();
        for (Locator element : elements) {
            links.add(element.getAttribute(attribute));
        }
        return links;
    }

    @Override
    public String takeScreenshotAsPNG(String locationToStore) {
        String filePath = locationToStore + "screenshot_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
        System.out.println("Screenshot taken: " + filePath);
        return filePath;
    }

    @Override
    public String takeScreenshotAsBase64(boolean fullPageScreenshot) {
        byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(fullPageScreenshot));
        return Base64.getEncoder().encodeToString(screenshotBytes);
    }

    @Override
    public void selectDropdownBasedOnValue(String address, String value) {
        findElement(address).selectOption(value);
    }

    @Override
    public boolean isChecked(String address) {
        return findElement(address).isChecked();
    }

    @Override
    public boolean isDisplayed(String address) {
        try {
            waitUntilPageLoadComplete();
            waitForElementToBeVisible(address, defaultWait);
            return findElement(address).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isViewable(String address) {
        try {
            Locator element = findElement(address);

            if (!element.isVisible()) {
                return false;
            }

            return (Boolean) element.evaluate("""
                        element => {
                            const rect = element.getBoundingClientRect();
                            if (rect.width === 0 || rect.height === 0) {
                                return false;
                            }

                            if (rect.bottom < 0 || rect.top > window.innerHeight ||
                                rect.right < 0 || rect.left > window.innerWidth) {
                                return false;
                            }

                            const elementAtPoint = document.elementFromPoint(
                                rect.left + rect.width/2,
                                rect.top + rect.height/2
                            );

                            return element.contains(elementAtPoint) || element === elementAtPoint;
                        }
                    """);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isDisplayed(String address, int timeOutInSeconds) {
        try {
            waitForElementToBeVisible(address, timeOutInSeconds);
            return findElement(address).isVisible();
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
        List<Page> windows = context.pages();
        if (windows.size() > 2) {
            System.out.println("More than 2 windows found!!");
            return false;
        } else if (windows.size() < 1) {
            System.out.println("No windows found!!");
            return false;
        }
        context.pages().get(windows.size() - 1).bringToFront();
        for (Page windowHandle : windows) {
            if (windowHandle != parentPage) {
                page = windowHandle;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean switchToParentWindow() {
        context.pages().get(0).bringToFront();
        page = parentPage;
        return true;
    }

    @Override
    public void setCookie(String name, String value, String domain) {
        context.addCookies(Arrays.asList(new Cookie[]{
                new Cookie(name, value).setDomain(domain).setPath("/")
        }));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        refreshPage();
    }

    @Override
    public String getCookieValue(String cookieName) {
        List<Cookie> cookies = context.cookies();
        for (Cookie cookie : cookies) {
            if (cookie.name.equals(cookieName)) {
                return cookie.value;
            }
        }
        return null;
    }

    @Override
    public boolean doesCookieExist(String cookieName) {
        // Get all cookies for the URL
        List<Cookie> cookies = context.cookies();
        Optional<Cookie> targetCookie = cookies.stream()
                .filter(cookie -> cookie.name.equals(cookieName))
                .findFirst();
        return targetCookie.isPresent();
    }

    @Override
    public void deleteCookie(String cookieName) {
        List<Cookie> cookies = context.cookies();
        List<Cookie> remainingCookies = cookies.stream()
                .filter(cookie -> !cookie.name.equals(cookieName))
                .collect(Collectors.toList());
        context.clearCookies();
        context.addCookies(remainingCookies);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshPage() {
        waitUntilPageLoadComplete();
        page.reload();
        waitUntilPageLoadComplete();
    }

    @Override
    public double[] getBoundingBoxCoordinates(String address) {
        double[] coordinates = new double[2];
        try {
            Long leftCoordinate = (Long) findElement(address).evaluate("element => element.getBoundingClientRect().left");
            coordinates[0] = (double) leftCoordinate.doubleValue();
        } catch (ClassCastException e) {
            try {
                Double leftCoordinate = (Double) findElement(address).evaluate("element => element.getBoundingClientRect().left");
                coordinates[0] = (double) leftCoordinate;
            } catch (Exception ex) {
                Integer leftCoordinate = (Integer) findElement(address).evaluate("element => element.getBoundingClientRect().left");
                coordinates[0] = (double) leftCoordinate.doubleValue();
            }
        }
        try {
            Long topCoordinate = (Long) findElement(address).evaluate("element => element.getBoundingClientRect().top");
            coordinates[1] = (double) topCoordinate.doubleValue();
        } catch (ClassCastException e) {
            try {
                Double topCoordinate = (Double) findElement(address).evaluate("element => element.getBoundingClientRect().top");
                coordinates[1] = (double) topCoordinate;
            } catch (ClassCastException e1) {
                Integer topCoordinate = (Integer) findElement(address).evaluate("element => element.getBoundingClientRect().top");
                coordinates[1] = (double) topCoordinate.doubleValue();
            }
        }
        return coordinates;
    }

    @Override
    public void scrollToElement(String address) {
        int count = 0;
        while (!isDisplayed(address, 5) && count < 11 && !isAtPageEnd()) {
            scrollPageHeight();
            count++;
        }
        findElement(address).scrollIntoViewIfNeeded();
    }

    @Override
    public void closeBrowser() {
        page.context().close();
    }

    @Override
    public void closeCurrentTab() {
        page.close();
    }

    @Override
    public void navigateForward() {
        page.goForward();
    }

    @Override
    public void navigateBack() {
        page.goBack();
    }

    @Override
    public void waitForElementToBeVisible(String address, int timeInSeconds) {
        page.waitForSelector(address, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Override
    public void waitForElementToBeClickable(String address, int timeInSeconds) {
        page.waitForSelector(address, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @Override
    public void waitForPresenceOfElement(String address, int timeInSeconds) {
        page.waitForSelector(address, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));
    }

    @Override
    public void waitForPresenceOfAllElements(String address, int timeInSeconds) {
        page.waitForSelector(address, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));
    }

    @Override
    public boolean waitUntilPageLoadComplete() {
        for (int i = 0; i < defaultWait; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String readyState = null;
            try {
                readyState = (String) page.evaluate("() => document.readyState");
            } catch (Exception e) {
            }
            if (readyState != null && readyState.equalsIgnoreCase("complete")) {
                return true;
            }
        }
        System.out.println("Page did not finish loading within " + defaultWait + " seconds");
        return false;
    }

    @Override
    public int getElementCount(String address) {
        return findElements(address).size();
    }

    @Override
    public boolean validateElementInViewportOrNot(String locator) {
        try {
            Locator element = page.locator(locator);
            // Using JavaScript to determine if the element is in the viewport
            Boolean isVisibleInViewport = (Boolean) element.evaluate(
                    "elem => {" +
                            "  var box = elem.getBoundingClientRect();" +
                            "  var cx = box.left + box.width / 2;" +
                            "  var cy = box.top + box.height / 2;" +
                            "  var e = document.elementFromPoint(cx, cy);" +
                            "  for (; e; e = e.parentElement) {" +
                            "    if (e === elem) return true;" +
                            "  }" +
                            "  return false;" +
                            "}"
            );

            return isVisibleInViewport;
        } catch (Exception e) {
            return false; // Return false if the element is not found or any error occurs
        }
    }

    /*
     * Fetch the request payload
     */
    @Override
    public String fetchRequestPayload(String apiName, String locator, BrowserAction action) {
        String[] result = new String[1];
        page.onRequest(request -> {
            if (request.url().contains(apiName)) {
                if (request.postData() != null) {
                    result[0] = request.postData();
                }
            }
        });
        page.waitForRequest("**/*", () -> {
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL:
                    scrollToElement(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
            }
        });
        waitUntilPageLoadComplete();
        return result[0];

    }

    @Override
    public String[] fetchHeaderRequestPayLoad(String apiName, String locator, BrowserAction action) {
        final String[] requestData = new String[2];

        // Listen for network requests
        page.onRequest(request -> {
            String url = request.url();

            // Check if URL matches the target API endpoint
            if (url.contains(apiName)) {
                // Extract query parameters
                String queryParams = url.contains("?") ? url.split("\\?", 2)[1] : "";

                // Extract request body, if available
                String body = request.postData() != null ? request.postData() : "";

                // Store query parameters and body in the array
                requestData[0] = queryParams;
                requestData[1] = body;

            }
        });

        switch (action) {
            case CLICK:
                click(locator);
                break;
            case SCROLL:
                scrollToElement(locator);
                break;
            case REFRESH:
                refreshPage();
                break;
        }

        requestData[1] = waitForResponseToBeCaptured(requestData[1]);

        // Clean up the result string by removing unwanted parts
        //result[0] = result[0].replace("Optional[", "").replace("]", "");

        return requestData;
    }

    @Override
    public String fetchResponse(String apiName, String locator, BrowserAction action) {
        String[] responseData = new String[1];
        page.onResponse(response -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            try {
                if (response.url().contains(apiName)) {
                    responseData[0] = response.text();
                }
            } catch (Exception e) {
                System.out.println("NO BODY");
            }
        });
        page.waitForRequest("**/*", () -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL: {
                    if (locator == null) scrollPageHeight();
                    else scrollToElement(locator);
                    break;
                }
                case REFRESH:
                    refreshPage();
                    break;
                case OPEN:
                    openUrl(locator);
                    break;
            }
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        waitForResponseToBeCaptured(responseData[0]);
        return responseData[0];
    }

    @Override
    public Map<String, String> fetchMultipleResponse(List<String> apiNames, String locator, BrowserAction action) {
        Map<String, String> responseMap = new HashMap<>();
        page.onResponse(response -> {
            for (String apiName : apiNames) {
                if (response.url().contains(apiName)) {
                    responseMap.put(apiName, response.text());
                }
            }
        });
        page.waitForRequest("**/*", () -> {
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL:
                    scrollToElement(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
                case SWIPE:
                    swipeElement();
                    break;
                case OPEN:
                    openUrl(locator);
                    break;
            }
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        //page.waitForTimeout(15000);
        return responseMap;
    }


    @Override
    public String fetchResponse(String apiName, String locator, BrowserAction action, String text) {
        String[] responseData = new String[1];
        page.onResponse(response -> {
            if (response.url().contains(apiName)) {
                responseData[0] = response.text();
            }
        });
        page.waitForRequest("**/*", () -> {
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL:
                    scrollToElement(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
                case ENTER_TEXT: {
                    clear(locator);
                    enterTextLetterByLetter(locator, text);
                    break;
                }
            }
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        //page.waitForTimeout(5000);
        return responseData[0];
    }

    @Override
    public String fetchResponseWithQueryParam(String apiName, String query, String locator, BrowserAction action, String text) {
        String[] responseData = new String[1];
        page.onResponse(response -> {
            if (response.url().contains(apiName) && response.url().contains(query)) {
                page.waitForTimeout(5000);
                responseData[0] = response.text();
            }
        });
        page.waitForRequest("**/*", () -> {
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL:
                    scrollToElement(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
                case ENTER_TEXT: {
                    clear(locator);
                    enterTextLetterByLetter(locator, text);
                    break;
                }
            }
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        waitForResponseToBeCaptured(responseData[0]);
        return responseData[0];
    }

    /**
     * Use this method to abort the api
     *
     * @param apiName
     * @param locator
     * @param action
     */
    @Override
    public void abortApi(String apiName, String locator, BrowserAction action) {


        try {
            // Set up route to intercept and abort specific requests
            context.route("**/*", route -> {
                String url = route.request().url();
                if (url.contains(apiName)) {
                    System.out.println("Blocked API: " + url);
                    route.abort();  // Abort the request if it matches
                    return;

                }
                route.resume();  // Continue with other requests
            });


            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL:
                    scrollToElement(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
            }

        } catch (Exception e) {
            System.out.println("Not able to abort the API");
        }

    }


    @Override
    public String waitForResponseToBeCaptured(String result) {
        try {
            // Wait for the condition to be true: result should not be null or empty
            page.waitForFunction("result => result !== ''", new Object[]{result},
                    new Page.WaitForFunctionOptions().setTimeout(10000));  // 10-second timeout
        } catch (Exception e) {
            // Return the result even if an exception occurs
            return result;
        }
        return result;
    }

    @Override
    public String getGAEvents() {
        waitUntilPageLoadComplete();
        return (String) page.evaluate("() => JSON.stringify(dataLayer)");
    }

    @Override
    public void clickUsingCoordinates(int xCoordinates, int yCoordinates) {
        page.mouse().click(xCoordinates, yCoordinates); // Example offset from top-left corner
    }

    @Override
    public void enterTextUsingKeyboard(String text) {
        page.keyboard().type(text);
    }

    @Override
    public void clickUsingCoordinatesInsideCanvas(int xCoordinates, int yCoordinates) {
        page.mouse().click(xCoordinates, yCoordinates); // Example offset from top-left corner
    }

    @Override
    public boolean verifyAlertMessage(String toast_msg) {
        boolean[] isSuccess = {false};
        page.onDialog(dialog -> {
            String alertMsg = dialog.message();
            System.out.println(alertMsg);
            if (alertMsg.contains(toast_msg)) {
                isSuccess[0] = true; //Accept the alert if the message matches
                System.out.println("Alert Found : " + alertMsg);
            }
            dialog.accept();
        });
        return isSuccess[0];
    }

    /**
     * Given the common address of the checkboxes this method clicks on all checkboxes
     *
     * @param address
     */
    public void clickAllElements(String address) {
        for (Locator element : findElements(address)) {
            highlight(element);
            element.click();
            unhighlight(element);
        }
    }

    @Override
    public void scrollPageUpAndDown(int xCoordinates, int yCoordinates) {
        page.mouse().wheel(xCoordinates, yCoordinates);
    }

    @Override
    public void scrollPageSlowly(int count) {
        if (count <= 0) {
            count = 2;
        }
        for (int i = 0; i < count / 2; i++) {
            try {
                page.mouse().wheel(0, 400);
                Thread.sleep(1000L);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private CompletableFuture<String> futureURL(String apiName) {
        CompletableFuture<String> urlFuture = new CompletableFuture<>();

        page.route("**/*", route -> {
            String url = route.request().url();
            if (url.contains(apiName)) {
                urlFuture.complete(url);
            }
            route.resume();
        });

        refreshPage();
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);

        CompletableFuture<Void> timeout = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return CompletableFuture.anyOf(urlFuture, timeout)
                .thenApply(result -> {
                    if (result instanceof String) {
                        return (String) result;
                    } else {
                        throw new RuntimeException("Timeout while waiting for URL match");
                    }
                })
                .whenComplete((result, ex) -> page.unroute("**/*"));
    }

    @Override
    public String getCompleteUrlFromRequestCall(String apiName) {
        CompletableFuture<String> future = futureURL(apiName);
        return future.join();
    }


    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx//

    //Playwright specific methods

    private Map<String, Map<String, Object>> sendMockResponse(Map<RequestConditionsToMock, MockResponseToSend> map) {
        Map<String, Map<String, Object>> mockMap = new HashMap<>();
        page.route("**/*", route -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            boolean mockingDone = false;
            for (RequestConditionsToMock requestConditionsToMock : map.keySet()) {
                MockResponseToSend mockResponseToSend = map.get(requestConditionsToMock);
                boolean isMockingRequired = isMockingRequired(requestConditionsToMock, route);
                if (isMockingRequired) {
                    String url = route.request().url();
                    System.out.println("Mocking: " + url);
                    Map<String, Object> mockResponse = mockTheResponse(mockResponseToSend, route);
                    mockMap.put(url, mockResponse);
                    mockingDone = true;
                    break;
                }
            }
            if (!mockingDone) {
                route.resume();
            }
//            for (Map.Entry<RequestConditionsToMock, MockResponseToSend> entry : map.entrySet()) {
//                RequestConditionsToMock requestConditionsToMock = entry.getKey();
//                MockResponseToSend mockResponseToSend = entry.getValue();
//                boolean check = isMockingRequired(requestConditionsToMock, route);
//                if (check) {
//                    String url = route.request().url();
//                    System.out.println("Mocking: " + url);
//                    Map<String, Object> mockResponse = mockTheResponse(mockResponseToSend, route);
//                    mockMap.put(url, mockResponse);
//                } else {
//                    route.resume();
//                }
//            }
        });
        return mockMap;
    }

    @Override
    public boolean isInputEnabled(String address) {
        return findElement(address).isEnabled();
    }

    private void stopMocking() {
        page.unrouteAll();
    }

    private Map<String, Object> mockTheResponse(MockResponseToSend mockResponseToSend, Route route) {
        Route.FulfillOptions options = new Route.FulfillOptions();
        Integer responseCode = mockResponseToSend.getResponseCode();
        if (responseCode == null) {
            System.out.println("Mock Response Code not available. Using Default");
            responseCode = route.fetch().status();
        }
        options.setStatus(responseCode);
        Map<String, String> responseHeaders = mockResponseToSend.getResponseHeaders();
        if (responseHeaders == null) {
            System.out.println("Mock Response Headers not available. Using Default");
            responseHeaders = route.fetch().headers();
        }
        options.setHeaders(responseHeaders);
        String responseBody = mockResponseToSend.getResponseBody();
        Map<String, Object> responseBodyModificationParams = mockResponseToSend.getResponseBodyModificationParams();
        String actualResponseBody = route.fetch().text();
        if (responseBodyModificationParams != null) {
            System.out.println("Dynamic Mocking of Response Body activated");
            System.out.println(actualResponseBody);
            responseBody = otherHelpers.modifyJsonValues(actualResponseBody, responseBodyModificationParams);
            System.out.println(responseBody);
        } else {
            if (responseBody != null) {
                System.out.println("Static Mocking of Response Body activated");
            } else {
                System.out.println("Mock Response Body not available. Using Default");
                responseBody = actualResponseBody;
            }
        }
        options.setBody(responseBody);
        route.fulfill(options);
        return Map.of("mockResponseCode", responseCode, "mockResponseHeaders", responseHeaders, "mockResponseBody", responseBody);
    }

    private boolean isMockingRequired(RequestConditionsToMock requestConditionsToMock, Route route) {
        Request request = route.request();
        Set<String> expectedRequestURLs = requestConditionsToMock.getRequestURLSubStrings();
        Map<String, String> expectedRequestHeaders = requestConditionsToMock.getRequestHeaders();
        Map<String, String> expectedRequestBodyKeyPathsAndValues = requestConditionsToMock.getRequestBodyKeyPathsAndValues();
        if (expectedRequestURLs == null && expectedRequestHeaders == null && expectedRequestBodyKeyPathsAndValues == null) {
            //No rule specified hence mocking not required
            return false;
        }
        if (expectedRequestURLs != null) {
            String actualURL = request.url();
            for (String expectedUrl : expectedRequestURLs) {
                if (!actualURL.contains(expectedUrl)) {
                    return false;
                }
            }
        }
        if (expectedRequestHeaders != null) {
            Map<String, String> actualRequestHeaders = request.allHeaders();
            for (Map.Entry<String, String> entry : expectedRequestHeaders.entrySet()) {
                String expectedKey = entry.getKey();
                String expectedValue = entry.getValue();
                if (!actualRequestHeaders.containsKey(expectedKey)) {
                    return false;
                } else {
                    if (!actualRequestHeaders.get(expectedKey).equals(expectedValue)) {
                        return false;
                    }
                }
            }
        }
        if (expectedRequestBodyKeyPathsAndValues != null) {
            String actualBody = request.postData();
            for (Map.Entry<String, String> entry : expectedRequestBodyKeyPathsAndValues.entrySet()) {
                String expectedKeyPath = entry.getKey();
                String expectedValue = entry.getValue();
                if (otherHelpers.getJsonValue(actualBody, expectedKeyPath) == null) { //key doesn't exist
                    return false;
                } else {
                    if (expectedValue != null) {//Validate value only if expected value is not null
                        if (!otherHelpers.getJsonValue(actualBody, expectedKeyPath).equals(expectedValue)) { //value is not as expected
                            return false;
                        }
                    }
                }
            }
        }
        return true; //As every rule is matched, we need to mock
    }

    @Override
    public void scrollPageHeight() {
        int innerHeight = (int) page.evaluate("window.innerHeight");
        scrollPageUpAndDown(0, innerHeight);
    }

    /**
     * Validates the api whether its triggered or not
     */
    @Override
    public boolean validateAPI(String apiName, String locator, BrowserAction action) {
        try {
            // Set up a predicate to filter responses containing the API URL part
            Predicate<Response> responseFilter = response -> response.url().contains(apiName);

            Response apiResponse = page.waitForResponse(responseFilter, () -> {
                switch (action) {
                    case CLICK:
                        click(locator);
                        break;
                    case SCROLL:
                        scrollToElement(locator);
                        break;
                    case REFRESH:
                        refreshPage();
                        break;
                }
            });

            if (apiResponse != null) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void removeElements(String address) {
        page.keyboard().press("Backspace");
    }

    @Override
    public void removeElements(String address, String data) {
        for (int i = 0; i < data.length(); i++) {
            page.keyboard().press("Backspace");
        }
    }

    /**
     * Get the complete url from the request called for a particular api name
     */
    @Override
    public String getCompleteUrlFromRequestCall(String apiName, String locator, BrowserAction action, String text) {
        CompletableFuture<String> future = futureURL(apiName, locator, action, text);
        return future.join();
    }

    private CompletableFuture<String> futureURL(String apiName, String locator, BrowserAction action, String text) {
        CompletableFuture<String> urlFuture = new CompletableFuture<>();

        page.route("**/*", route -> {
            String url = route.request().url();
            if (url.contains(apiName)) {
                urlFuture.complete(url);
            }
            route.resume();
        });

        switch (action) {
            case CLICK:
                click(locator);
                break;
            case SCROLL:
                scrollToElement(locator);
                break;
            case REFRESH:
                refreshPage();
                break;
            case ENTER_TEXT: {
                enterTextLetterByLetter(locator, text);
                break;
            }

        }

        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);

        CompletableFuture<Void> timeout = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        return CompletableFuture.anyOf(urlFuture, timeout)
                .thenApply(result -> {
                    if (result instanceof String) {
                        return (String) result;
                    } else {
                        throw new RuntimeException("Timeout while waiting for URL match");
                    }
                })
                .whenComplete((result, ex) -> page.unroute("**/*"));
    }

    public boolean waitTillTextPresent(String locator, int waitTimeInSeconds) {
        try {
            page.waitForFunction("locator => document.querySelector(locator) && " +
                            "document.querySelector(locator).value.trim() != ''", locator,
                    new Page.WaitForFunctionOptions().setTimeout(waitTimeInSeconds * 1000));
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }


    @Override
    public void highlight(Object elem) {
        Locator element = (Locator) elem;
        element.evaluate("element => { element.style.border = '3px solid green'; " +
                "element.style.outline = '2px solid green'; " +
                "element.style.padding = '5px'; }");
    }

    @Override
    public void unhighlight(Object elem) {
        try {
            Locator element = (Locator) elem;
            if (element.count() > 0) {
                element.evaluate("element => { element.style.border = ''; " +
                        "element.style.outline = ''; " +
                        "element.style.padding = ''; }");
            }
        } catch (Exception e) {

        }
    }

    @Override
    public String getCssColor(String classname) {
        Locator ratingBadge = page.locator(classname);
        return ratingBadge.evaluate("element => getComputedStyle(element).background-color").toString();
    }

    @Override
    public List<String> getAllCssColor(String selector) {
        List<String> colorValues = new ArrayList<>();
        String propertyName = "background-color";

        List<ElementHandle> elements = page.querySelectorAll(selector);

        for (ElementHandle element : elements) {
            String color = element.evaluate("(element, prop) => {" +
                    "const color = window.getComputedStyle(element).getPropertyValue(prop);" +
                    "if (color.startsWith('rgb(')) {" +
                    "   const [r, g, b] = color.match(/\\d+/g);" +
                    "   return `rgba(${r}, ${g}, ${b}, 1)`;" +
                    "}" +
                    "return color;" +
                    "}", propertyName).toString();
            colorValues.add(color);
        }

        return colorValues;
    }

    /**
     * Enter the text letter by letter
     */
    @Override
    public void enterTextLetterByLetter(String locator, String text) {
        Locator inputBox = findElement(locator);
        for (char c : text.toCharArray()) {
            inputBox.type(String.valueOf(c));
            page.waitForTimeout(500);
        }
    }

    /**
     * Fetch the response status
     */
    @Override
    public int fetchResponseStatus(String apiName, String locator, BrowserAction action, String text) {
        final String[] responseData = {"0"};
        try {
            // Listen to the 'response' event and capture the response body if the URL contains the apiEndPoints
            page.onResponse(response -> {
                if (response.url().contains(apiName)) {
                    try {
                        // Capture and store the response body as a JSON string
                        responseData[0] = String.valueOf(response.status());
                        // You can also use response.json() based on the response type
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Request interceptedRequest = page.waitForRequest("**/*", () -> {
                switch (action) {
                    case CLICK:
                        click(locator);
                        break;
                    case SCROLL:
                        scrollToElement(locator);
                        break;
                    case REFRESH:
                        refreshPage();
                        break;
                    case ENTER_TEXT:
                        enterTextLetterByLetter(locator, text);
                        break;
                }
            });

//            page.waitForFunction("responseData => responseData[0] !== '' ", new Object[]{responseData[0]},
//                    new Page.WaitForFunctionOptions().setTimeout(15000));
        } catch (Exception e) {
        }
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
        return Integer.parseInt(responseData[0]);
    }


    public String dialogMessage = "";

    /**
     * Accept the alert message
     */
    @Override
    public void acceptAlertMessage() {
        AtomicBoolean stopListening = new AtomicBoolean(false);
        try {
            page.onceDialog(dialog -> {
                if (stopListening.get()) {
                    return; // Do nothing if the condition is met
                }
                dialogMessage = dialog.message();
                dialog.accept(); // Accept the alert
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the alert message
     */
    @Override
    public String getAlertMessage() {
        return dialogMessage;
    }

    /**
     * returns the string which have opacity 0.2
     */
    @Override
    public String opacityText(String locator) {
        String result = "";
        Locator element = findElement(locator);
        String op = (String) element.evaluate("elem => window.getComputedStyle(elem).getPropertyValue('opacity')");
        if (op.equals("0.2")) {
            result = element.textContent();
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
        acceptAlertMessage();
        click(locator);
        return getAlertMessage();
    }

    /**
     * refreshes and trigger the alert and accept the alert
     *
     * @return alert message
     */
    @Override
    public String refreshAndAcceptAlert() {
        acceptAlertMessage();
        refreshPage();
        return getAlertMessage();
    }

    /**
     * Scroll till the bottom of the page
     *
     * @return int ( number of times scrolled )
     */
    @Override
    public int scrollTillEnd(String api) {
        final int[] apiCallCount = {0};

        // Track every call of specific api endpoint
        page.onRequest(request -> {
            if (request.url().contains(api)) {
                apiCallCount[0]++;
            }
        });

        int lastHeight = (int) page.evaluate("() => document.body.scrollHeight");
        int scrollCount = 0;

        while (true) {
            page.evaluate("window.scrollTo(0, document.body.scrollHeight);");
            scrollCount++;

            page.waitForTimeout(2000);

            int newHeight = (int) page.evaluate("() => document.body.scrollHeight");
            if (newHeight == lastHeight) {
                break;
            }
            lastHeight = newHeight;
        }
        return apiCallCount[0];
    }

    @Override
    public boolean isImageLoaded(String locator) {
        try {
            // Check if the image is displayed
            if (!isDisplayed(locator)) {
                return false;
            }

            Locator imageLocator = page.locator(locator);
            // Use page.evaluate to check if the image is fully loaded
            return (boolean) imageLocator.evaluate("img => img.complete && typeof img.naturalWidth !== 'undefined' && img.naturalWidth > 0");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public double getYCoordinates(String locator) {
        return findElement(locator).boundingBox().y;
    }

    @Override
    public boolean scrollHorizontally(String locator) {
        Locator element = findElement(locator);
        boolean isHorizontallyScrollable = (boolean) element.evaluate("element => element.scrollWidth > element.clientWidth");

        if (isHorizontallyScrollable) {
            System.out.println("The element is scrollable horizontally.");

            // Scroll the element to the right
            element.evaluate("element => element.scrollLeft = element.scrollWidth");
            waitUntilPageLoadComplete();
            return true;
        }
        return false;
    }


    @Override
    public String getUserAgent() {
        return page.evaluate("() => navigator.userAgent").toString();
    }

    @Override
    public int countElements(String locator) {
        List<Locator> list = findElements(locator);
        return list.size();
    }

    @Override
    public boolean validateLinksWithoutHref(String locator) {
        List<Locator> list = findElements(locator);
        for (Locator element : list) {
            element.click();
            switchToChildWindow();
            System.out.println(getCurrentPageUrl());
            switchToParentWindow();
        }
        return false;
    }

    /**
     * Get the color of the element
     */
    @Override
    public String getElementColor(String locator) {
        return (String) findElement(locator).evaluate("element => window.getComputedStyle(element).color");
    }

    @Override
    public boolean assertRelativePosition(String locator1, String locator2) {
        boolean flag = false;
        double elem1Y = findElement(locator1).boundingBox().y;
        double elem2Y = findElement(locator2).boundingBox().y;

        if (elem1Y < elem2Y) flag = true;
        return flag;
    }

    @Override
    public boolean checkForElementToBeInvisible(String address, int timeInSeconds) {
        try {
            findElement(address).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED).setTimeout(5000));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getCssValue(String locator, String property) {
        return findElement(locator).evaluate("(element, prop) => window.getComputedStyle(element).getPropertyValue(prop)", property).toString();
    }

    @Override
    public void clickUsingJavascriptExecutor(String locator) {
        String expression = "document.evaluate(\"" + locator + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click()";
        page.evaluate(expression);
    }


    @Override
    public void mockApiResponseCode(int rCode, String apiName, String locator, BrowserAction action) {
        context.route("**/*", route -> {
            String url = route.request().url();
            if (url.contains(apiName)) { // Check if URL contains the substring
                System.out.println("Intercepted request for URL: " + url);
                route.fulfill(new Route.FulfillOptions()
                        .setStatus(500) // Set HTTP status to 500
                        .setContentType("application/json") // Specify response content type
                        .setBody("{ \"error\": \"Simulated Server Error\" }")); // Mock response body
            } else {
                route.resume(); // Allow other requests to proceed
            }
        });


        page.waitForRequest("**/*", () -> {
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case SCROLL:
                    scrollToElement(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
            }
        });
        waitUntilPageLoadComplete();
        //page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    @Override
    public void swipeElement() {
        ElementHandle body = page.querySelector("body");
        JsonObject dimensions = JsonParser.parseString(page.evaluate("() => ({ width: window.innerWidth, height: window.innerHeight })").toString()).getAsJsonObject();
        int screenWidth = dimensions.get("width").getAsInt();
        int screenHeight = dimensions.get("height").getAsInt();

        int startX = screenWidth / 2;
        int startY = (int) (screenHeight * 0.8);
        int endX = screenWidth / 2;
        int endY = (int) (screenHeight * 0.2);

        page.mouse().move(startX, startY);
        page.mouse().down();
        page.mouse().move(endX, endY);
        page.mouse().up();
    }

    @Override
    public void swipeHorizontal(double startXPercent, double endXPercent, double yPercent, int durationMs) {
        int viewportWidth = page.viewportSize().width;
        int viewportHeight = page.viewportSize().height;

        double startX = (viewportWidth * startXPercent) / 100;
        double endX = (viewportWidth * endXPercent) / 100;
        double y = (viewportHeight * yPercent) / 100;

        String direction = startX > endX ? "left" : "right";
        System.out.println("Performing touch swipe " + direction + " from (" +
                startX + "," + y + ") to (" + endX + "," + y + ")");

        page.evaluate("([startX, endX, y]) => {" +
                "const canvas = document.createElement('canvas');" +
                "canvas.style.position = 'fixed';" +
                "canvas.style.top = '0';" +
                "canvas.style.left = '0';" +
                "canvas.width = window.innerWidth;" +
                "canvas.height = window.innerHeight;" +
                "canvas.style.pointerEvents = 'none';" +
                "canvas.style.zIndex = '9999';" +
                "document.body.appendChild(canvas);" +

                "const ctx = canvas.getContext('2d');" +
                "ctx.strokeStyle = 'blue';" +
                "ctx.lineWidth = 5;" +
                "ctx.beginPath();" +
                "ctx.moveTo(startX, y);" +
                "ctx.lineTo(endX, y);" +
                "ctx.stroke();" +

                "setTimeout(() => canvas.remove(), 2000);" +
                "}", new Object[]{
                (int) (startX),
                (int) (endX),
                (int) (y)
        });

        try {
            page.touchscreen().tap(startX, y);

            int steps = Math.max(5, durationMs / 20);
            int stepDelay = durationMs / steps;

            for (int i = 1; i <= steps; i++) {
                double progress = (double) i / steps;
                double currentX = startX + progress * (endX - startX);

                page.evaluate("([x, y]) => {" +
                        "const touchObj = new Touch({" +
                        "  identifier: Date.now()," +
                        "  target: document.elementFromPoint(x, y)," +
                        "  clientX: x," +
                        "  clientY: y," +
                        "  pageX: x," +
                        "  pageY: y," +
                        "  screenX: x," +
                        "  screenY: y" +
                        "});" +

                        "const touchEvent = new TouchEvent('touchmove', {" +
                        "  cancelable: true," +
                        "  bubbles: true," +
                        "  touches: [touchObj]," +
                        "  targetTouches: [touchObj]," +
                        "  changedTouches: [touchObj]," +
                        "  view: window" +
                        "});" +

                        "const element = document.elementFromPoint(x, y) || document.body;" +
                        "element.dispatchEvent(touchEvent);" +
                        "return element.tagName;" +
                        "}", new Object[]{currentX, y});

                page.waitForTimeout(stepDelay);
            }

            page.evaluate("([x, y]) => {" +
                    "const touchObj = new Touch({" +
                    "  identifier: Date.now()," +
                    "  target: document.elementFromPoint(x, y)," +
                    "  clientX: x," +
                    "  clientY: y," +
                    "  pageX: x," +
                    "  pageY: y," +
                    "  screenX: x," +
                    "  screenY: y" +
                    "});" +

                    "const touchEvent = new TouchEvent('touchend', {" +
                    "  cancelable: true," +
                    "  bubbles: true," +
                    "  touches: []," +
                    "  targetTouches: []," +
                    "  changedTouches: [touchObj]," +
                    "  view: window" +
                    "});" +

                    "const element = document.elementFromPoint(x, y) || document.body;" +
                    "element.dispatchEvent(touchEvent);" +
                    "return element.tagName;" +
                    "}", new Object[]{endX, y});

            System.out.println("Touch swipe completed");

        } catch (Exception e) {
            System.err.println("Error during touch swipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void swipeDown() {
        ElementHandle body = page.querySelector("body");
        JsonObject dimensions = JsonParser.parseString(page.evaluate("() => ({ width: window.innerWidth, height: window.innerHeight })").toString()).getAsJsonObject();
        int screenWidth = dimensions.get("width").getAsInt();
        int screenHeight = dimensions.get("height").getAsInt();

        int endX = screenWidth / 2;
        int endY = (int) (screenHeight * 0.8);
        int startX = screenWidth / 2;
        int startY = (int) (screenHeight * 0.2);

        page.mouse().move(startX, startY);
        page.mouse().down();
        page.mouse().move(endX, endY);
        page.mouse().up();
    }

    /*
     * Fetch the complete URL for matching api triggered
     */
    @Override
    public String fetchRequestUrl(String apiName, String locator, BrowserAction action, String text) {
        final String[] matchedUrl = {null};

        // Listen for requests matching the API name
        page.onRequest(request -> {
            if (request.url().contains(apiName)) {
                matchedUrl[0] = request.url();
            }
        });

        // Perform the action that triggers the request
        switch (action) {
            case CLICK:
                click(locator);
                break;
            case SCROLL:
                scrollToElement(locator);
                break;
            case REFRESH:
                refreshPage();
                break;
            case ENTER_TEXT:
                enterText(locator, text);
                break;
        }

        // Wait to ensure request has time to be captured
        page.waitForTimeout(3000);  // Adjust timeout as needed

        waitUntilPageLoadComplete();

        return matchedUrl[0] != null ? matchedUrl[0] : "";
    }

    /*
     * Scroll to the top of the page
     */
    @Override
    public void scrollToTheTopOfPage() {
        page.evaluate("window.scrollTo(0, 0);");
    }

    /*
     * Reached the end of the page or not
     */
    @Override
    public boolean isAtPageEnd() {
        return (Boolean) page.evaluate("() => window.innerHeight + window.scrollY >= document.body.scrollHeight");
    }

    /**
     * pauses request for sometime ex 5000ms
     */
    public boolean pauseRequest(String apiName, String locator, List<String> uiElements, BrowserAction action) {
        final boolean[] check = {true};
        page.route("**/*", route -> {
            if (route.request().url().contains(apiName)) {
                System.out.println("API request intercepted: " + route.request().url());
                try {
                    System.out.println("Pausing request...");
                    Thread.sleep(1000);
                    for (String ele : uiElements) {
                        if (isDisplayed(ele)) {
                            check[0] = false;
                            return;
                        }
                    }
                    //uiElements.forEach(elem -> Assert.assertTrue(isDisplayed(elem), elem));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            route.resume();
        });
        if (!check[0]) {
            return false;
        }
        page.waitForRequest("**/*", () -> {
            switch (action) {
                case CLICK:
                    click(locator);
                    break;
                case REFRESH:
                    refreshPage();
                    break;
            }
        });
        waitUntilPageLoadComplete();
        return check[0];
    }


}



