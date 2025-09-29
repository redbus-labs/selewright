package com.redbus.selewright;

import java.util.Map;

/**
 * This class is used to define the mock response to be sent when a request is intercepted.
 */
public class MockResponseToSend {
    private Integer responseCode;
    private Map<String, String> responseHeaders;
    private String responseBody;
    private Map<String, Object> responseBodyModificationParams;

    /**
     * Get mock response code
     */
    public Integer getResponseCode() {
        return responseCode;
    }

    /**
     * Set mock response code
     */
    public MockResponseToSend setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    /**
     * Get mock response headers
     */
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Set mock response headers
     */
    public MockResponseToSend setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
        return this;
    }

    /**
     * Get mock response body in case of complete replacement
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Set mock response body by replacing the entire body
     */
    public MockResponseToSend setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    /**
     * Get mock response body in case of partial replacement
     *
     * @return
     */
    public Map<String, Object> getResponseBodyModificationParams() {
        return responseBodyModificationParams;
    }

    /**
     * Set mock response body by replacing few components of the body based on the key paths
     *
     * @param keyPathsAndValues: Map of key paths and values to be replaced. Value can be of any datatype you want, including JsonObject in case of replacing with a JSON
     */
    public MockResponseToSend setResponseBodyModificationParams(Map<String, Object> keyPathsAndValues) {
        this.responseBodyModificationParams = keyPathsAndValues;
        return this;
    }

}
