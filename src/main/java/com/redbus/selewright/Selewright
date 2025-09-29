package com.redbus.selewright;

import java.util.*;
import java.util.List;

/**
 * Selewright is a lightweight abstraction layer built on top of Selenium and Playwright, designed for browser test automation frameworks.
 */
public interface Selewright {

    /**
     * Finds the first matching element for the given locator.
     *
     * @param address The locator of the element.
     * @return The first matching element.
     */
    Object findElement(String address);

    /**
     * Finds all matching elements for the given locator.
     *
     * @param address The locator of the elements.
     * @return A list of matching elements.
     */
    Object findElements(String address);

    /**
     * Clicks on the element identified by the given locator by simulating right mouse click.
     *
     * @param address The locator of the element to click.
     */
    void click(String address);

    /**
     * Clicks on the element identified by the given locator by simulating tap action.
     *
     * @param address The locator of the element to tap.
     */
    void tap(String address);

    /**
     * Hovers on the element identified by the given locator
     *
     * @param address The locator of the element to click.
     */
    void hover(String address);

    /**
     * Refreshes the page and mocks API responses based on the provided map.
     *
     * @param map A map containing request conditions and mock responses.
     * @return A map of mocked responses.
     */
    Map<String, Map<String, Object>> refreshAndMock(Map<RequestConditionsToMock, MockResponseToSend> map);

    /**
     * Clears the text in the input field identified by the given locator.
     *
     * @param address The locator of the input field.
     */
    void clear(String address);

    /**
     * Navigates to the specified URL.
     *
     * @param url The URL to navigate to.
     */
    void openUrl(String url);

    /**
     * Retrieves the title of the current page.
     *
     * @return The page title.
     */
    String getPageTitle();

    /**
     * Retrieves the URL of the current page.
     *
     * @return The current page URL.
     */
    String getCurrentPageUrl();

    /**
     * Retrieves the HTML content of the current page.
     *
     * @return The HTML content as a string.
     */
    String getHtmlPageContent();

    /**
     * Enters the specified text into the input field identified by the given locator.
     *
     * @param address The locator of the input field.
     * @param text    The text to enter.
     */
    void enterText(String address, String text);

    /**
     * Retrieves the text content of the element identified by the given locator.
     *
     * @param address The locator of the element.
     * @return The text content of the element.
     */
    String getText(String address);

    /**
     * Retrieves the text content of all elements matching the given locator.
     *
     * @param address The locator of the elements.
     * @return A list of text content from all matching elements.
     */
    List<String> getAllText(String address);

    /**
     * Retrieves the count of elements matching the given locator.
     *
     * @param address The locator of the elements.
     * @return The count of matching elements.
     */
    int getElementCount(String address);

    /**
     * Selects a dropdown option based on its value.
     *
     * @param address The locator of the dropdown.
     * @param value   The value of the option to select.
     */
    void selectDropdownBasedOnValue(String address, String value);

    /**
     * Checks if the checkbox or radio button identified by the given locator is checked.
     *
     * @param address The locator of the checkbox or radio button.
     * @return True if checked, otherwise false.
     */
    boolean isChecked(String address);

    /**
     * Checks if the element identified by the given locator is displayed.
     *
     * @param address The locator of the element.
     * @return True if displayed, otherwise false.
     */
    boolean isDisplayed(String address);

    /**
     * Checks if the element identified by the given locator is viewable in the viewport.
     *
     * @param address The locator of the element.
     * @return True if viewable, otherwise false.
     */
    boolean isViewable(String address);

    /**
     * Checks if the element identified by the given locator is displayed within the specified timeout.
     *
     * @param address          The locator of the element.
     * @param timeOutInSeconds The timeout in seconds.
     * @return True if displayed within the timeout, otherwise false.
     */
    boolean isDisplayed(String address, int timeOutInSeconds);

    /**
     * Retrieves the value of the specified attribute from the element identified by the given locator.
     *
     * @param address       The locator of the element.
     * @param attributeName The name of the attribute.
     * @return The value of the attribute.
     */
    String getAttribute(String address, String attributeName);

    /**
     * Switches to the child window if available.
     *
     * @return True if switched successfully, otherwise false.
     */
    boolean switchToChildWindow();

    /**
     * Switches back to the parent window.
     *
     * @return True if switched successfully, otherwise false.
     */
    boolean switchToParentWindow();

    /**
     * Checks if the radio button identified by the given locator is checked.
     *
     * @param address The locator of the radio button.
     * @return True if checked, otherwise false.
     */
    boolean isRadioChecked(String address);

    /**
     * Sets a cookie with the specified name, value, and domain.
     *
     * @param name   The name of the cookie.
     * @param value  The value of the cookie.
     * @param domain The domain for the cookie.
     */
    void setCookie(String name, String value, String domain);

    /**
     * Retrieves the value of the specified cookie.
     *
     * @param cookieName The name of the cookie.
     * @return The value of the cookie.
     */
    String getCookieValue(String cookieName);

    /**
     * Checks if a cookie with the specified name exists.
     *
     * @param cookieName The name of the cookie.
     * @return True if the cookie exists, otherwise false.
     */
    boolean doesCookieExist(String cookieName);

    /**
     * Deletes the cookie with the specified name.
     *
     * @param cookieName The name of the cookie.
     */
    void deleteCookie(String cookieName);

    /**
     * Refreshes the current page.
     */
    void refreshPage();

    /**
     * Retrieves the bounding box coordinates of the element identified by the given locator.
     *
     * @param address The locator of the element.
     * @return An array containing the x and y coordinates.
     */
    double[] getBoundingBoxCoordinates(String address);

    /**
     * Scrolls to the element identified by the given locator.
     *
     * @param address The locator of the element.
     */
    void scrollToElement(String address);

    /**
     * Closes the browser.
     */
    void closeBrowser();

    /**
     * Closes the current tab.
     */
    void closeCurrentTab();

    /**
     * Navigates forward in the browser.
     */
    void navigateForward();

    /**
     * Navigates back in the browser.
     */
    void navigateBack();

    /**
     * Waits for the element identified by the given locator to be visible.
     *
     * @param address       The locator of the element.
     * @param timeInSeconds The timeout in seconds.
     */
    void waitForElementToBeVisible(String address, int timeInSeconds);

    /**
     * Waits for the element identified by the given locator to be clickable.
     *
     * @param address       The locator of the element.
     * @param timeInSeconds The timeout in seconds.
     */
    void waitForElementToBeClickable(String address, int timeInSeconds);

    /**
     * Waits for the presence of the element identified by the given locator.
     *
     * @param address       The locator of the element.
     * @param timeInSeconds The timeout in seconds.
     */
    void waitForPresenceOfElement(String address, int timeInSeconds);

    /**
     * Waits for the presence of all elements matching the given locator.
     *
     * @param address       The locator of the elements.
     * @param timeInSeconds The timeout in seconds.
     */
    void waitForPresenceOfAllElements(String address, int timeInSeconds);

    /**
     * Waits until the page load is complete.
     *
     * @return True if the page load is complete, otherwise false.
     */
    boolean waitUntilPageLoadComplete();

    /**
     * Validates if the element identified by the given locator is in the viewport.
     *
     * @param address The locator of the element.
     * @return True if in the viewport, otherwise false.
     */
    boolean validateElementInViewportOrNot(String address);

    /**
     * Validates if the specified API is triggered based on the given action and locator.
     *
     * @param apiName The name of the API.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform.
     * @return True if the API is triggered, otherwise false.
     */
    boolean validateAPI(String apiName, String locator, BrowserAction action);

    /**
     * Retrieves the complete URL from the request call for the specified API.
     *
     * @param apiName The name of the API.
     * @return The complete URL.
     */
    String getCompleteUrlFromRequestCall(String apiName);

    public enum BrowserAction {
        CLICK,
        SCROLL,
        REFRESH,
        NULL,
        ENTER_TEXT,
        SWIPE,
        OPEN
    }

    /**
     * Fetches the response body for a specific API triggered by a browser action.
     *
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform (e.g., CLICK, SCROLL, REFRESH).
     * @return The response body as a string.
     */
    String fetchResponse(String apiName, String locator, BrowserAction action);

    /**
     * Fetches the response bodies for multiple APIs triggered by a browser action.
     *
     * @param apiNames A list of API names to intercept.
     * @param locator  The locator of the element triggering the APIs.
     * @param action   The browser action to perform (e.g., CLICK, SCROLL, REFRESH).
     * @return A map where the keys are API names and the values are their respective response bodies.
     */
    Map<String, String> fetchMultipleResponse(List<String> apiNames, String locator, BrowserAction action);

    /**
     * Fetches the response body for a specific API triggered by a browser action, with additional text input.
     *
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform (e.g., CLICK, SCROLL, REFRESH, ENTER_TEXT).
     * @param text    The text to enter if the action is ENTER_TEXT.
     * @return The response body as a string.
     */
    String fetchResponse(String apiName, String locator, BrowserAction action, String text);

    /**
     * Fetches the HTTP status code for a specific API triggered by a browser action.
     *
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform (e.g., CLICK, SCROLL, REFRESH, ENTER_TEXT).
     * @param text    The text to enter if the action is ENTER_TEXT.
     * @return The HTTP status code as an integer.
     */
    int fetchResponseStatus(String apiName, String locator, BrowserAction action, String text);

    /**
     * Aborts a specific API request triggered by a browser action.
     *
     * @param apiName The name of the API to intercept and abort.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform (e.g., CLICK, SCROLL, REFRESH).
     */
    void abortApi(String apiName, String locator, BrowserAction action);

    /**
     * Fetches the response payload for a specific API triggered by a browser action.
     *
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform (e.g., CLICK, SCROLL, REFRESH).
     * @return The request payload as a string.
     */
    String fetchRequestPayload(String apiName, String locator, BrowserAction action);

    /**
     * Fetches the query parameters and request body for a specific API triggered by a browser action.
     *
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform (e.g., CLICK, SCROLL, REFRESH).
     * @return An array containing the query parameters and request body.
     */
    String[] fetchHeaderRequestPayLoad(String apiName, String locator, BrowserAction action);

    /**
     * Waits for a response to be captured and returns the result.
     *
     * @param result The initial result to wait for.
     * @return The captured response as a string.
     */
    String waitForResponseToBeCaptured(String result);

    /**
     * Clicks on all elements matching the given locator.
     *
     * @param address The locator of the elements to click.
     */
    void clickAllElements(String address);

    /**
     * Retrieves Google Analytics events from the data layer.
     *
     * @return A JSON string representing the data layer.
     */
    String getGAEvents();

    /**
     * Clicks on a specific coordinate on the page.
     *
     * @param xCoordinates The x-coordinate to click.
     * @param yCoordinates The y-coordinate to click.
     */
    void clickUsingCoordinates(int xCoordinates, int yCoordinates);

    /**
     * This method uses keyboard actions to enter the specified text into the focused element.
     *
     * @param text The text to be entered.
     */
    void enterTextUsingKeyboard(String text);

    /**
     * Clicks on a specific coordinate inside a canvas element.
     *
     * @param xCoordinates The x-coordinate to click.
     * @param yCoordinates The y-coordinate to click.
     */
    void clickUsingCoordinatesInsideCanvas(int xCoordinates, int yCoordinates);

    /**
     * Verifies if an alert message matches the expected text.
     *
     * @param toast_msg The expected alert message.
     * @return True if the alert message matches, otherwise false.
     */
    boolean verifyAlertMessage(String toast_msg);

    /**
     * Scrolls the page up and down by the specified coordinates.
     *
     * @param xCoordinates The x-coordinate for scrolling.
     * @param yCoordinates The y-coordinate for scrolling.
     */
    void scrollPageUpAndDown(int xCoordinates, int yCoordinates);

    /**
     * Scrolls the page slowly by a specified number of steps.
     *
     * @param count The number of steps to scroll.
     */
    void scrollPageSlowly(int count);

    /**
     * Clicks on the element identified by the given locator and mocks API responses based on the provided map.
     *
     * @param address The locator of the element to click.
     * @param map     A map containing request conditions and mock responses.
     * @return A map of mocked responses where the keys are request URLs and the values are their respective mock details.
     */
    Map<String, Map<String, Object>> clickAndMock(String address, Map<RequestConditionsToMock, MockResponseToSend> map);

    /**
     * Opens the specified URL and mocks API responses based on the provided map.
     *
     * @param url The URL to navigate to.
     * @param map A map containing request conditions and mock responses.
     * @return A map of mocked responses where the keys are request URLs and the values are their respective mock details.
     */
    Map<String, Map<String, Object>> openUrlAndMock(String url, Map<RequestConditionsToMock, MockResponseToSend> map);

    /**
     * Scrolls to the element identified by the given locator and mocks API responses based on the provided map.
     *
     * @param address The locator of the element to scroll to.
     * @param map     A map containing request conditions and mock responses.
     * @return A map of mocked responses where the keys are request URLs and the values are their respective mock details.
     */
    Map<String, Map<String, Object>> scrollToElementAndMock(String address, Map<RequestConditionsToMock, MockResponseToSend> map);

    /**
     * Performs a swipe action on the page and mocks API responses based on the provided map.
     *
     * @param address The locator of the element to swipe.
     * @param map     A map containing request conditions and mock responses.
     * @return A map of mocked responses where the keys are request URLs and the values are their respective mock details.
     */
    Map<String, Map<String, Object>> swipeAndMock(String address, Map<RequestConditionsToMock, MockResponseToSend> map);

    /**
     * Updates the global wait timeout for all actions performed on the page.
     *
     * @param waitInSeconds The timeout duration in seconds.
     */
    void updateGlobalWait(int waitInSeconds);

    /**
     * Checks if the input field identified by the given locator is enabled.
     *
     * @param address The locator of the input field.
     * @return True if the input field is enabled, otherwise false.
     */
    boolean isInputEnabled(String address);

    /**
     * Scrolls the page by the height of the viewport.
     */
    void scrollPageHeight();

    /**
     * Removes elements identified by the given locator.
     *
     * @param address The locator of the elements to remove.
     */
    void removeElements(String address);

    /**
     * Removes elements identified by the given locator and clears the specified data.
     *
     * @param address The locator of the elements to remove.
     * @param data    The data to clear.
     */
    void removeElements(String address, String data);

    /**
     * Retrieves the complete URL from the request call for the specified API triggered by a browser action.
     *
     * @param apiName The name of the API.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform.
     * @param text    The text to enter if the action is ENTER_TEXT.
     * @return The complete URL as a string.
     */
    String getCompleteUrlFromRequestCall(String apiName, String locator, BrowserAction action, String text);

    /**
     * Retrieves all CSS color values for elements matching the given class name.
     *
     * @param classname The class name of the elements.
     * @return A list of CSS color values as strings.
     */
    List<String> getAllCssColor(String classname);

    /**
     * Retrieves all attribute values for elements matching the given locator.
     *
     * @param address   The locator of the elements.
     * @param attribute The name of the attribute to retrieve.
     * @return A list of attribute values as strings.
     */
    List<String> getAllAttributes(String address, String attribute);

    /**
     * Takes a screenshot of the current page in png format and saves it to the specified location.
     *
     * @param locationToStore: Path to store the screenshot
     * @return: File path of the screenshot
     */
    String takeScreenshotAsPNG(String locationToStore);

    /**
     * Takes a screenshot of the current page in base64 encoded string format and returns the same
     *
     * @param fullPageScreenshot true if a full-page screenshot is required, otherwise false.
     * @return The screenshot as a base64 encoded string.
     */
    String takeScreenshotAsBase64(boolean fullPageScreenshot);

    /**
     * Retrieves the CSS color value for the first element matching the given class name.
     *
     * @param classname The class name of the element.
     * @return The CSS color value as a string.
     */
    String getCssColor(String classname);

    /**
     * Waits until the specified text is present in the input field identified by the given locator.
     *
     * @param locator           The locator of the input field.
     * @param waitTimeInSeconds The maximum time to wait in seconds.
     * @return True if the text is present within the timeout, otherwise false.
     */
    boolean waitTillTextPresent(String locator, int waitTimeInSeconds);

    /**
     * Enters the specified text into the input field letter by letter.
     *
     * @param locator The locator of the input field.
     * @param text    The text to enter.
     */
    void enterTextLetterByLetter(String locator, String text);

    /**
     * Accepts an alert message if present.
     */
    void acceptAlertMessage();

    /**
     * Highlights the specified element by adding a green border and outline.
     *
     * @param element The element to highlight.
     */
    void highlight(Object element);

    /**
     * Retrieves the alert message from the browser.
     *
     * @return The alert message as a string.
     */
    String getAlertMessage();

    /**
     * Retrieves the text of an element with opacity 0.2.
     *
     * @param locator The locator of the element.
     * @return The text of the element if its opacity is 0.2, otherwise an empty string.
     */
    String opacityText(String locator);

    /**
     * Clicks on an element and accepts the alert triggered by the click.
     *
     * @param locator The locator of the element to click.
     * @return The alert message after accepting the alert.
     */
    String clickAndAcceptAlert(String locator);

    /**
     * Refreshes the page and accepts the alert if present.
     *
     * @return The alert message after accepting the alert.
     */
    String refreshAndAcceptAlert();

    /**
     * Scrolls to the bottom of the page until the end is reached.
     *
     * @param api The API to track during scrolling.
     * @return The number of times the API was called during scrolling.
     */
    int scrollTillEnd(String api);

    /**
     * Removes the highlight from the specified element by resetting its styles.
     *
     * @param element The element to unhighlight.
     */
    void unhighlight(Object element);

    /**
     * Fetches the response payload for a specific API with a query parameter triggered by a browser action.
     *
     * @param apiName The name of the API.
     * @param query   The query parameter to match.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform.
     * @param text    The text to enter if the action is ENTER_TEXT.
     * @return The response payload as a string.
     */
    String fetchResponseWithQueryParam(String apiName, String query, String locator, BrowserAction action, String text);

    /**
     * Checks if the image identified by the given locator is fully loaded.
     *
     * @param locator The locator of the image element.
     * @return True if the image is loaded, otherwise false.
     */
    boolean isImageLoaded(String locator);

    /**
     * Retrieves the Y-coordinate of the element identified by the given locator.
     *
     * @param locator The locator of the element.
     * @return The Y-coordinate of the element.
     */
    double getYCoordinates(String locator);

    /**
     * Scrolls the element horizontally if it is scrollable.
     *
     * @param locator The locator of the element.
     * @return True if the element is scrollable horizontally, otherwise false.
     */
    boolean scrollHorizontally(String locator);

    /**
     * Retrieves the user agent string of the browser.
     *
     * @return The user agent string.
     */
    String getUserAgent();

    /**
     * Counts the number of elements matching the given locator.
     *
     * @param locator The locator of the elements.
     * @return The count of matching elements.
     */
    int countElements(String locator);

    /**
     * Validates links without an href attribute by clicking on them and switching between windows.
     *
     * @param locator The locator of the links to validate.
     * @return False if validation fails.
     */
    boolean validateLinksWithoutHref(String locator);

    /**
     * Asserts the relative position of two elements identified by their locators.
     *
     * @param locator1 The locator of the first element.
     * @param locator2 The locator of the second element.
     * @return True if the relative position is as expected, otherwise false.
     */
    boolean assertRelativePosition(String locator1, String locator2);

    /**
     * Retrieves the color of the element identified by the given locator.
     *
     * @param locator The locator of the element.
     * @return The color of the element as a string.
     */
    String getElementColor(String locator);

    /**
     * Performs a swipe action on an element.
     */
    void swipeElement();

    /**
     * Performs a horizontal swipe action on the screen.
     *
     * @param startXPercent The starting X percentage of the screen width.
     * @param endXPercent   The ending X percentage of the screen width.
     * @param yPercent      The Y percentage of the screen height where the swipe occurs.
     * @param durationMs    The duration of the swipe in milliseconds.
     */
    void swipeHorizontal(double startXPercent, double endXPercent, double yPercent, int durationMs);

    /**
     * Performs a swipe down action on the screen.
     */
    void swipeDown();


    /**
     * Waits for the element identified by the given locator to become invisible within the specified timeout.
     *
     * @param address       The locator of the element.
     * @param timeInSeconds The timeout in seconds.
     * @return True if the element becomes invisible within the timeout, otherwise false.
     */
    boolean checkForElementToBeInvisible(String address, int timeInSeconds);

    /**
     * Retrieves the value of the specified CSS property for the element identified by the given locator.
     *
     * @param locator  The locator of the element.
     * @param property The name of the CSS property.
     * @return The value of the CSS property as a string.
     */
    String getCssValue(String locator, String property);

    /**
     * Clicks on the element identified by the given locator using JavaScript execution.
     *
     * @param locator The XPath locator of the element to click.
     */
    void clickUsingJavascriptExecutor(String locator);

    /**
     * Mocks the API response code for a specific API request triggered by a browser action.
     *
     * @param rCode   The response code to mock.
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform.
     */
    void mockApiResponseCode(int rCode, String apiName, String locator, BrowserAction action);

    /**
     * Fetches the complete URL for a matching API triggered by a browser action.
     *
     * @param apiName The name of the API to intercept.
     * @param locator The locator of the element triggering the API.
     * @param action  The browser action to perform.
     * @param text    The text to enter if the action is ENTER_TEXT.
     * @return The complete URL as a string.
     */
    String fetchRequestUrl(String apiName, String locator, BrowserAction action, String text);

    /**
     * Scrolls to the top of the page.
     */
    void scrollToTheTopOfPage();

    /**
     * Checks if the browser has reached the end of the page.
     *
     * @return True if the browser is at the end of the page, otherwise false.
     */
    boolean isAtPageEnd();

    /**
     * Pauses the request for a specific API for a given duration.
     *
     * @param apiName  The name of the API to pause.
     * @param locator  The locator of the element triggering the API.
     * @param locators A list of locators to validate after the API is triggered.
     * @param action   The browser action to perform.
     */
    boolean pauseRequest(String apiName, String locator, List<String> locators, BrowserAction action);

    /**
     * Checks if the browser is running on a touch device.
     *
     * @return True if the browser is a touch device, otherwise false.
     */
    boolean isTouchDevice();

}
