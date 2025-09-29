package com.redbus.selewright;

import java.util.Map;
import java.util.Set;

/**
 * This class is used to define the conditions for a request to be intercepted and mocked.
 */
public class RequestConditionsToMock {
    private Map<String, String> requestBodyKeyPathsAndValues;
    private Map<String, String> requestHeaders;
    private Set<String> requestURLSubStrings;

    /**
     * This method is used to get the request URL sub strings upon which response should be mocked.
     */
    public Set<String> getRequestURLSubStrings() {
        return requestURLSubStrings;
    }

    /**
     * This method is used to get the request headers upon which response should be mocked.
     */
    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * This method is used to get the request payload key value pairs upon which response should be mocked.
     */
    public Map<String, String> getRequestBodyKeyPathsAndValues() {
        return requestBodyKeyPathsAndValues;
    }

    /**
     * This method is used to set the request headers upon which response should be mocked.
     */
    public RequestConditionsToMock setRequestURLSubStrings(Set<String> requestURLSubStrings) {
        this.requestURLSubStrings = requestURLSubStrings;
        return this;
    }

    /**
     * This method is used to set the request URL sub strings upon which response should be mocked.
     */
    public RequestConditionsToMock setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
        return this;
    }

    /**
     * This method is used to set the request payload key value pairs upon which response should be mocked.
     */
    public RequestConditionsToMock setRequestBodyKeyPathsAndValues(Map<String, String> requestBodyKeyPathsAndValues) {
        this.requestBodyKeyPathsAndValues = requestBodyKeyPathsAndValues;
        return this;
    }


}
