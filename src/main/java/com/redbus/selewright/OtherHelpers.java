package com.redbus.selewright;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * This class contains utility methods which are typically not part of your browser automation but can be helpful with test automation tasks.
 * There is no on liner which can exactly describe this class. Please explore the methods to understand the purpose of this class :)
 */
public class OtherHelpers {

    /**
     * Returns JSON value based on the json key path in a json of string format
     *
     * @param jsonInStringFormat String representation of a JSON object
     * @param jsonKeyPath        Path to the desired value, using dot notation (e.g., "person.address.city")
     * @return String representation of the value at the specified path, or null if not found
     */
    public String getJsonValue(String jsonInStringFormat, String jsonKeyPath) {
        if (jsonInStringFormat == null || jsonKeyPath == null || jsonInStringFormat.isEmpty() || jsonKeyPath.isEmpty()) {
            return null;
        }

        // Trim the JSON string
        String json = jsonInStringFormat.trim();

        // Check if the JSON string is valid (basic check)
        if (!(json.startsWith("{") && json.endsWith("}"))) {
            return null;
        }

        // Split the path by dots
        String[] pathParts = jsonKeyPath.split("\\.");

        // Start with the entire JSON string
        String currentJson = json;

        for (String key : pathParts) {
            // Prepare the key for searching in JSON
            String formattedKey = "\"" + key + "\"";
            int keyIndex = currentJson.indexOf(formattedKey);

            if (keyIndex == -1) {
                return null; // Key not found
            }

            // Move past the key and colon
            int valueStartIndex = currentJson.indexOf(':', keyIndex) + 1;

            // Trim whitespace
            while (valueStartIndex < currentJson.length() &&
                    Character.isWhitespace(currentJson.charAt(valueStartIndex))) {
                valueStartIndex++;
            }

            // Handle different value types
            char firstChar = currentJson.charAt(valueStartIndex);

            if (firstChar == '{') {
                // Object
                currentJson = extractObject(currentJson, valueStartIndex);
            } else if (firstChar == '[') {
                // Array
                currentJson = extractArray(currentJson, valueStartIndex);
            } else if (firstChar == '"') {
                // String
                if (key.equals(pathParts[pathParts.length - 1])) {
                    // Last key in path, return the string value
                    return extractString(currentJson, valueStartIndex);
                } else {
                    return null; // Trying to navigate into a string value
                }
            } else {
                // Number, boolean, or null
                if (key.equals(pathParts[pathParts.length - 1])) {
                    // Last key in path, return the primitive value
                    return extractPrimitive(currentJson, valueStartIndex);
                } else {
                    return null; // Trying to navigate into a primitive value
                }
            }
        }

        // Return the final JSON object or array as a string
        return currentJson;
    }

    /**
     * Converts the IntelliJ json path format to a format which can be understood by JsonPath class
     *
     * @param customPath - json path which is found using "Copy Json Pointer" option in Intellij
     *                   Eg: Input: /details/0/data/1/Title    Output: $.details[0].data[1].Title
     *                   Note: If you don't want the number to be treated as index, prefix it with *
     *                   Eg: Input: /details/*0/data/1/Title    Output: $.details.0.data[1].Title
     * @return
     */
    public String convertIntelliJJsonPathToRightFormat(String customPath) {
        if (customPath.startsWith("/")) {
            String[] pathComponents = customPath.split("/");
            String correctJsonPath = "$";
            for (String component : pathComponents) {
                if (!component.equals("")) {
                    try {
                        Integer.parseInt(component);
                        correctJsonPath = correctJsonPath + "[" + component + "]";
                    } catch (NumberFormatException e) {
                        if (component.startsWith("*")) {
                            component = component.substring(1);
                        }
                        correctJsonPath = correctJsonPath + "." + component;
                    }
                }
            }
            return correctJsonPath;
        } else {
            return customPath;
        }
    }

    /**
     * Verifies a URL by checking the response status code
     *
     * @param urlString The url which needs to be verified
     * @param userAgent The request header user agent
     * @return True if response status code is greater than 200 and less than  300
     */
    public boolean verifyBrokenLink(String urlString, String userAgent, String baseUrl) {
        final int MAX_REDIRECTS = 20;
        int redirectCount = 0;
        try {

            URL url;
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                url = new URL(new URL(baseUrl), urlString);
            } else {
                url = new URL(urlString);
            }
            while (redirectCount < MAX_REDIRECTS) {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestProperty("User-Agent", userAgent);

                int statusCode = conn.getResponseCode();


                if (statusCode >= 300 && statusCode < 400) {
                    String redirectUrl = conn.getHeaderField("Location");
                    if (redirectUrl == null) {
                        break;
                    }
                    url = new URL(url, redirectUrl);
                    redirectCount++;
                } else {
                    return statusCode >= 200 && statusCode < 300;
                }
            }
            System.out.println(urlString);
            return false;
        } catch (Exception e) {
            System.out.println("Exception occurred : " + urlString + " " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifies a JSON string by updating the value at the specified JSON path.
     *
     * @param jsonString The original JSON string to be modified.
     * @param jsonPath   The JSON path to the key whose value needs to be updated.
     * @param newValue   The new value to set at the specified JSON path.
     * @return The modified JSON string with the updated value.
     */
    private String modifyJsonValue(String jsonString, String jsonPath, Object newValue) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        String keyPath = convertIntelliJJsonPathToRightFormat(jsonPath);
        String[] keys = keyPath.split("\\.");
        JsonObject current = jsonObject;

        for (int i = 1; i < keys.length - 1; i++) {
            String key = keys[i];
            if (key.contains("[")) {
                int arrayIndex = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
                key = key.substring(0, key.indexOf('['));
                current = current.getAsJsonArray(key).get(arrayIndex).getAsJsonObject();
            } else {
                current = current.getAsJsonObject(key);
            }
        }

        String finalKey = keys[keys.length - 1];

        if (newValue instanceof JsonArray) {
            JsonArray arr = new JsonArray();
            arr.add(newValue.toString());
            current.add(finalKey, arr);
        } else if (newValue instanceof JsonObject) {
            JsonObject obj = gson.fromJson(newValue.toString(), JsonObject.class);
            current.add(finalKey, obj);
        } else {
            if (newValue instanceof String) {
                current.addProperty(finalKey, (String) newValue);
            } else if (newValue instanceof Number) {
                current.addProperty(finalKey, (Number) newValue);
            } else if (newValue instanceof Boolean) {
                current.addProperty(finalKey, (Boolean) newValue);
            } else if (newValue instanceof Character) {
                current.addProperty(finalKey, (Character) newValue);
            }
        }
        return gson.toJson(jsonObject);
    }

    /**
     * Extracts a JSON object from the given string starting at the specified index
     */
    private String extractObject(String json, int startIndex) {
        int depth = 0;
        int endIndex = startIndex;

        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0) {
                    endIndex = i + 1;
                    break;
                }
            }
        }

        return json.substring(startIndex, endIndex);
    }

    /**
     * Extracts a JSON array from the given string starting at the specified index
     */
    private String extractArray(String json, int startIndex) {
        int depth = 0;
        int endIndex = startIndex;

        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    endIndex = i + 1;
                    break;
                }
            }
        }

        return json.substring(startIndex, endIndex);
    }

    /**
     * Extracts a string value from the given JSON string starting at the specified index
     */
    private String extractString(String json, int startIndex) {
        int endIndex = startIndex + 1; // Skip the opening quote
        boolean escaped = false;

        for (int i = startIndex + 1; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                endIndex = i;
                break;
            }
        }

        // Return the string without surrounding quotes
        String value = json.substring(startIndex + 1, endIndex);

        // Handle escape sequences
        value = value.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f");

        return value;
    }

    /**
     * Extracts a primitive value (number, boolean, null) from the given JSON string
     */
    private String extractPrimitive(String json, int startIndex) {
        int endIndex = startIndex;

        for (int i = startIndex; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                endIndex = i;
                break;
            }

            // If we reach the end of the string
            if (i == json.length() - 1) {
                endIndex = json.length();
            }
        }

        return json.substring(startIndex, endIndex).trim();
    }

    /**
     * Checks if the specified text is present in the clipboard.
     *
     * @param expectedText The text to check in the clipboard.
     * @return True if the clipboard contains the expected text, otherwise false.
     */
    public boolean isTextInClipboard(String expectedText) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);

            if (contents != null) {
                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                return clipboardText.equals(expectedText);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Modifies multiple JSON values in a JSON string based on the provided key paths and values.
     *
     * @param jsonString        The original JSON string to be modified.
     * @param keyPathsAndValues A map where keys are JSON paths and values are the new values to set.
     * @return The modified JSON string with updated values.
     */
    public String modifyJsonValues(String jsonString, Map<String, Object> keyPathsAndValues) {
        String curr = jsonString;
        for (String jsonPath : keyPathsAndValues.keySet()) {
            curr = modifyJsonValue(curr, jsonPath, keyPathsAndValues.get(jsonPath));
        }
        return curr;
    }

    /**
     * Converts the given time in minutes to a formatted string of hours and minutes.
     *
     * @param minutes The time in minutes to convert.
     * @return A formatted string representing the time in hours and minutes.
     */
    public String convertMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return hours + "h " + remainingMinutes + "m";
    }

    /**
     * Reads the contents of a file from the specified file path and returns a FileInputStream.
     *
     * @param filePath The absolute path of the file to be read.
     * @return A FileInputStream object for the specified file.
     * @throws RuntimeException if the file is not found or cannot be accessed.
     */
    public FileInputStream getFileContents(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return fis;
    }

    /**
     * Reads the contents of a JSON file from the specified file path and returns it as a JSONObject.
     *
     * @param filePath The path to the JSON file to be read.
     * @return A JSONObject representing the contents of the JSON file.
     * @throws RuntimeException If there is an error reading the file or parsing the JSON.
     */
    public JsonObject getJSONObjects(String filePath) {
        FileInputStream fis = getFileContents(filePath);
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            return jsonObject;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Converts the contents of a JSON file at the specified file path into a string.
     *
     * @param filePath The path to the JSON file to be read and converted.
     * @return A string representation of the JSON object.
     */
    public String jsonToString(String filePath) {
        JsonObject data = getJSONObjects(filePath);
        return data.toString();
    }

    /**
     * Strips the protocol, www, port number, query params, and path params from a URL and returns only the domain.
     *
     * @param url The full URL of the website.
     * @return The domain name.
     */
    public String extractDomain(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }

        try {
            // Remove protocol (http:// or https://)
            String domain = url.replaceFirst("^(https?://)", "");

            // Remove path, query parameters, and fragment
            int pathIndex = domain.indexOf('/');
            if (pathIndex != -1) {
                domain = domain.substring(0, pathIndex);
            }

            // Remove port number if present
            int portIndex = domain.indexOf(':');
            if (portIndex != -1) {
                domain = domain.substring(0, portIndex);
            }

            return domain;
        } catch (Exception e) {
            return "";
        }
    }

}
