package com.johannesbrodwall.jz14.oauth;

import java.util.Base64;

import com.eclipsesource.json.JsonObject;

public class AccessToken {

    private String accessToken;
    private String idToken;

    public AccessToken(JsonObject jsonObject) {
        this.accessToken = jsonObject.get("access_token").asString();
        this.idToken = jsonObject.get("id_token").asString();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getIdentity() {
        JsonObject payload = JsonObject.readFrom(new String(Base64.getDecoder().decode(idToken.split("\\.")[1])));
        return payload.get("email").asString() + " " + (payload.get("email_verified").asBoolean() ? "(verified)" : "");
    }

}
