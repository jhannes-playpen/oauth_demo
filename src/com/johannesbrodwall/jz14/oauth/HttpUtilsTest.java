package com.johannesbrodwall.jz14.oauth;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;

import com.eclipsesource.json.JsonObject;

public class HttpUtilsTest {

    @Test
    public void shouldConvertQueryToMap() throws Exception {
        URI uri = new URI(null, null, "/oauth2callback", "state=foo&code=bar", null);

        Map<String, String> parameters = WebServer.toMap(uri.getQuery(), "&", "=");
        assertEquals("foo", parameters.get("state"));
        assertEquals("bar", parameters.get("code"));
    }

    @Test
    public void shouldConvertNullToEmptyMap() throws Exception {
        Map<String, String> parameters = WebServer.toMap(null, "&", "=");
        assertTrue(parameters.isEmpty());
    }

    @Test
    public void shouldSupportEmptyParameters() throws Exception {
        Map<String, String> parameters = WebServer.toMap("state=&code=bar", "&", "=");
        assertEquals("", parameters.get("state"));
    }

    @Test
    public void shouldConvertCookiesToEmptyMap() throws Exception {
        Map<String, String> cookies = WebServer.toMap("__utma=1.189636975.1368433074.1399896755.1400009298.77; __utmz=1.1400009298.77.52.utmcsr=johannesbrodwall.com|utmccn=(referral)|utmcmd=referral|utmcct=/2010/03/08/why-and-how-to-use-jetty-in-mission-critical-production/; _octo=GH1.1.1399054623.1404188688; logged_in=yes; dotcom_user=jhannes; tz=Europe%2FBerlin; _ga=GA1.2.1977976159.1398197790; user_session=O_6UKWdbrnFt4siSt8q_dg4LYvr785D2bInGDsjkLDFigqHsU9ph5J5W_5VeMOZb7p6Hxpkf_y4xd1nk; _gh_sess=eyJzZXNzaW9uX2lkIjoiNjU5OGY5YmEzYTFkZDE0MTUxZTU0YzBmNDZhYTE4MTUiLCJzcHlfcmVwbyI6ImpoYW5uZXMvb2F1dGhfZnVuIiwic3B5X3JlcG9fYXQiOjE0MDY4MjA4MzZ9--f89e81ff517aac955260e38d1d78ad9b13fa04f7",
                ";", "=");
        assertEquals(new HashSet<>(Arrays.asList("_octo", "user_session", "__utmz", "tz", "logged_in", "_ga", "dotcom_user", "_gh_sess", "__utma")),
                cookies.keySet());
        assertEquals("1.1400009298.77.52.utmcsr=johannesbrodwall.com|utmccn=(referral)|utmcmd=referral|utmcct=/2010/03/08/why-and-how-to-use-jetty-in-mission-critical-production/",
                cookies.get("__utmz"));
    }


    @Test
    public void shouldDecodeAuthenticationToken() throws Exception {
        String tokenText = "{  \"access_token\" : \"ya29.UwCoQL1_UjDOEiEAAAB1JkY83fHPL37pa6BGYhHtOaR62_CKh7B-5L8WC9t3I4wN6olmt_it0pSK0uxzOrA\",  \"token_type\" : \"Bearer\",  \"expires_in\" : 3600,  \"id_token\" : \"eyJhbGciOiJSUzI1NiIsImtpZCI6IjE3NzE5MzFlYjBlYjY0ZWI5NzczM2U4NTc2ODViZTE1M2UwNzliYjkifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiaWQiOiIxMTQ4ODI0OTM5NTQ2ODUyOTA4NTkiLCJzdWIiOiIxMTQ4ODI0OTM5NTQ2ODUyOTA4NTkiLCJhenAiOiI3NzI0ODcwNTg4NC1pamViN2NuMDBham1udjRmZ2pscDl0dTJoaWhsM29xNS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoiamhhbm5lc0BnbWFpbC5jb20iLCJhdF9oYXNoIjoiY2VvX3JaQjZabk5qUDNKVnBDSnNfZyIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdWQiOiI3NzI0ODcwNTg4NC1pamViN2NuMDBham1udjRmZ2pscDl0dTJoaWhsM29xNS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInRva2VuX2hhc2giOiJjZW9fclpCNlpuTmpQM0pWcENKc19nIiwidmVyaWZpZWRfZW1haWwiOnRydWUsImNpZCI6Ijc3MjQ4NzA1ODg0LWlqZWI3Y24wMGFqbW52NGZnamxwOXR1MmhpaGwzb3E1LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiaWF0IjoxNDA2ODMzMzE3LCJleHAiOjE0MDY4MzcyMTd9.UVfc6jupDCA0NDFWcNKLBz6Hy0koaxkWgTFaqajStick-R_TEdMWShA19VVvnsYd8vrmiVwC7mSJK0sk747hpiJLNZVl0GLtRmKrqlzNA0nqjP4Bm808VpO53v5EvAlftSkWYKGGURfw_JtKZ9vilSWhYYI86OVf2akzGSJKaYU\"}";
        AccessToken token = new AccessToken(JsonObject.readFrom(tokenText));
        assertEquals("ya29.UwCoQL1_UjDOEiEAAAB1JkY83fHPL37pa6BGYhHtOaR62_CKh7B-5L8WC9t3I4wN6olmt_it0pSK0uxzOrA",
                token.getAccessToken());
        assertEquals("eyJhbGciOiJSUzI1NiIsImtpZCI6IjE3NzE5MzFlYjBlYjY0ZWI5NzczM2U4NTc2ODViZTE1M2UwNzliYjkifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiaWQiOiIxMTQ4ODI0OTM5NTQ2ODUyOTA4NTkiLCJzdWIiOiIxMTQ4ODI0OTM5NTQ2ODUyOTA4NTkiLCJhenAiOiI3NzI0ODcwNTg4NC1pamViN2NuMDBham1udjRmZ2pscDl0dTJoaWhsM29xNS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImVtYWlsIjoiamhhbm5lc0BnbWFpbC5jb20iLCJhdF9oYXNoIjoiY2VvX3JaQjZabk5qUDNKVnBDSnNfZyIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhdWQiOiI3NzI0ODcwNTg4NC1pamViN2NuMDBham1udjRmZ2pscDl0dTJoaWhsM29xNS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInRva2VuX2hhc2giOiJjZW9fclpCNlpuTmpQM0pWcENKc19nIiwidmVyaWZpZWRfZW1haWwiOnRydWUsImNpZCI6Ijc3MjQ4NzA1ODg0LWlqZWI3Y24wMGFqbW52NGZnamxwOXR1MmhpaGwzb3E1LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiaWF0IjoxNDA2ODMzMzE3LCJleHAiOjE0MDY4MzcyMTd9.UVfc6jupDCA0NDFWcNKLBz6Hy0koaxkWgTFaqajStick-R_TEdMWShA19VVvnsYd8vrmiVwC7mSJK0sk747hpiJLNZVl0GLtRmKrqlzNA0nqjP4Bm808VpO53v5EvAlftSkWYKGGURfw_JtKZ9vilSWhYYI86OVf2akzGSJKaYU",
                token.getIdToken());

        assertEquals("jhannes@gmail.com (verified)", token.getIdentity());
    }

    @Test
    public void urlTest() throws Exception {
        URI uri = new URI("http", "jhannes", "localhost", 80, "/test/something", "a=b&c=d", "fragment");
        assertEquals("http://jhannes@localhost:80/", uri.resolve("/").toString());
    }


}
