package com.gmailwatcher.gmailwatcher;

import java.util.List;

public class WatchRequest {
    private String email;
    private String password;
    private List<String> keywords;
    private int delay; // in minutes
    private String phone;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }

    public int getDelay() { return delay; }
    public void setDelay(int delay) { this.delay = delay; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
