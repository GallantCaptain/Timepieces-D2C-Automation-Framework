package com.timepieces.automation.context;

import java.util.HashMap;
import java.util.Map;

/**
 * PicoContainer injects a single instance of this class into all step definition classes
 * during the execution of a single scenario.
 * It serves as a strongly-typed state container to share data between steps.
 */
public class TestContext {
    
    // Add strongly typed properties here as needed
    private String orderId;
    private String registeredEmail;
    private String currentPhoneNumber;
    
    // Optional: A flexible map for temporary or generic data if needed occasionally
    private Map<String, Object> scenarioData = new HashMap<>();

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRegisteredEmail() {
        return registeredEmail;
    }

    public void setRegisteredEmail(String registeredEmail) {
        this.registeredEmail = registeredEmail;
    }

    public String getCurrentPhoneNumber() {
        return currentPhoneNumber;
    }

    public void setCurrentPhoneNumber(String currentPhoneNumber) {
        this.currentPhoneNumber = currentPhoneNumber;
    }

    public void setContextData(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object getContextData(String key) {
        return scenarioData.get(key);
    }
}
