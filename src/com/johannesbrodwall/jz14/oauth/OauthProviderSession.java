package com.johannesbrodwall.jz14.oauth;

import java.net.MalformedURLException;
import java.net.URL;

public class OauthProviderSession {

    private String label;
    private String username;
    private String clientSignupUrl;
    private String scope;
    private String authUrl;
    private URL tokenUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;

    public OauthProviderSession(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setClientSignup(String clientSignupUrl) {
        this.clientSignupUrl = clientSignupUrl;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = toURL(tokenUrl);
    }

    private URL toURL(String tokenUrl) {
        try {
            return new URL(tokenUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getClientSignupUrl() {
        return clientSignupUrl;
    }

    public String getScope() {
        return scope;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public URL getTokenUrl() {
        return tokenUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getUrl(String redirectUrl) {
        if (clientId == null) return null;

        return getAuthUrl() + "?"
                + "response_type=code&"
                + "client_id=" + getClientId() + "&"
                + "redirect_uri=" + redirectUrl + "&"
                + "scope=" + getScope() + "&"
                + "state=" + getLabel()
                ;
    }

    public void setAccessToken(AccessToken accessToken) {
        username = accessToken.getIdentity();
    }


}

