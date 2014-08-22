package com.johannesbrodwall.jz14.oauth;

import java.util.HashMap;
import java.util.Map;

public class OauthDemoAppSession {

    private Map<String, OauthProviderSession> oauthSessions = new HashMap<String, OauthProviderSession>();

    public OauthDemoAppSession() {
        OauthProviderSession linkedin = new OauthProviderSession("LinkedIn");
        linkedin.setUsername("Johannes");

        oauthSessions.put(linkedin.getLabel(), linkedin);
        oauthSessions.put("Google", createGoogleProvider());
        oauthSessions.put("Facebook", createFacebookProvider());
    }

    private OauthProviderSession createFacebookProvider() {
        OauthProviderSession facebook = new OauthProviderSession("Facebook");
        facebook.setClientSignup("https://developers.facebook.com/");
        facebook.setScope("email");
        facebook.setAuthUrl("https://www.facebook.com/dialog/oauth");
        facebook.setTokenUrl("https://graph.facebook.com/oauth/access_token");

        facebook.setClientId(System.getProperty("oauth2.facebook.client_id"));
        facebook.setClientSecret(System.getProperty("oauth2.facebook.client_secret"));
        facebook.setRedirectUrl(System.getProperty("oauth2.facebook.redirect_url"));
        return facebook;
    }

    private OauthProviderSession createGoogleProvider() {
        OauthProviderSession google = new OauthProviderSession("Google");
        google.setClientSignup("https://console.developers.google.com/project");
        google.setScope("profile email");
        google.setAuthUrl("https://accounts.google.com/o/oauth2/auth");
        google.setTokenUrl("https://accounts.google.com/o/oauth2/token");

        google.setClientId(System.getProperty("oauth2.google.client_id"));
        google.setClientSecret(System.getProperty("oauth2.google.client_secret"));
        google.setRedirectUrl(System.getProperty("oauth2.google.redirect_url"));
        return google;
    }

    public Map<String, OauthProviderSession> getProviders() {
        return oauthSessions;
    }

}
