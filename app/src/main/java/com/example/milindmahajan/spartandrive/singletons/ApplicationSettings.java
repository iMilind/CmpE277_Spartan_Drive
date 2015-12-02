package com.example.milindmahajan.spartandrive.singletons;

/**
 * Created by milind.mahajan on 10/13/15.
 */
public class ApplicationSettings {

    private static ApplicationSettings sharedSettings = null;

    private String accessToken;
    private boolean authenticated;

    private ApplicationSettings() {

        accessToken = "";
        authenticated = false;
    }

    public static ApplicationSettings getSharedSettings() {

        if (sharedSettings == null) {

            sharedSettings = new ApplicationSettings();
        }

        return sharedSettings;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public void setAuthenticated (boolean authenticated) {

        this.authenticated = authenticated;
    }

    public boolean isAuthenticated () {

        return this.authenticated;
    }
}