package com.johannesbrodwall.jz14.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServer implements HttpHandler {

    private Path publicFiles;
    private Map<String,OauthDemoAppSession> sessions = new HashMap<>();
    private SecureRandom sessionKeyGenerator = new SecureRandom();

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(3000), 0);
        httpServer.createContext("/", new WebServer());
        httpServer.start();
    }

    public WebServer() {
        publicFiles = Paths.get("public/").toAbsolutePath();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (handlePublicFile(exchange)) return;
            if (handleApiRequest(exchange)) return;
            if (handleOauthCallback(exchange)) return;

            try (Writer body = new OutputStreamWriter(exchange.getResponseBody())) {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                body.write("Not found");
            }
        } catch (Exception e) {
            e.printStackTrace();

            try (Writer body = new OutputStreamWriter(exchange.getResponseBody())) {
                body.write(e.toString());
            }
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(404, 0);
        }
    }

    private boolean handleOauthCallback(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestURI().getPath().equals("/oauth2callback")) return false;

        OauthDemoAppSession session = findOrCreateSession(exchange);
        Map<String, String> parameters = toMap(exchange.getRequestURI().getQuery(), "&", "=");
        OauthProviderSession providerSession = session.getProviders().get(parameters.get("state"));

        String postUrlQuery =
                  "code=" + parameters.get("code") + "&"
                + "client_id=" + providerSession.getClientId() + "&"
                + "client_secret=" + providerSession.getClientSecret() + "&"
                + "redirect_uri=" + getServerUrl(exchange) + "/oauth2callback" + "&"
                + "grant_type=authorization_code";

        URL tokenUrl = new URL(providerSession.getTokenUrl() + "?" + postUrlQuery);

        HttpURLConnection tokenConnection = (HttpURLConnection) tokenUrl.openConnection();
        tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        tokenConnection.setDoOutput(true);
        tokenConnection.setRequestMethod("POST");
        try (Writer writer = new OutputStreamWriter(tokenConnection.getOutputStream())) {
            writer.write(postUrlQuery);
        }
        int responseCode = tokenConnection.getResponseCode();
        if (responseCode < 400) {
            String message = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()))
                .lines()
                .reduce((a, b) -> a + b).get();
            providerSession.setAccessToken(new AccessToken(JsonObject.readFrom(message)));
        } else {
            String message = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()))
                .lines()
                .reduce((a, b) -> a + b).get();
            System.out.println(message);
        }

        exchange.getResponseHeaders().set("Location", getServerUrl(exchange));
        exchange.sendResponseHeaders(301, 0);

        return true;
    }

    private boolean handleApiRequest(HttpExchange exchange) throws IOException {
        OauthDemoAppSession session = findOrCreateSession(exchange);

        List<String> acceptTypes =
               Arrays.asList(exchange.getRequestHeaders().getFirst("Accept").split(","));
        if (!acceptTypes.contains("application/json")) return false;

        JsonObject response = new JsonObject();
        response.set("providers",
                toJsonArray(session.getProviders().values().stream().map(p -> toJSON(p, getServerUrl(exchange)))));

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        try (Writer body = new OutputStreamWriter(exchange.getResponseBody())) {
            body.write(response.toString());
        }

        return true;
    }

    private JsonArray toJsonArray(Stream<? extends JsonValue> stream) {
        JsonArray result = new JsonArray();
        stream.forEach(o -> result.add(o));
        return result;
    }

    private String getServerUrl(HttpExchange exchange) {
        return  "http://" + exchange.getRequestHeaders().getFirst("Host");
    }

    public JsonObject toJSON(OauthProviderSession oauthSession, String redirectUrl) {
        JsonObject provider = new JsonObject();
        provider.set("provider", oauthSession.getLabel());
        provider.set("username", oauthSession.getUsername());
        provider.set("clientSignup", oauthSession.getClientSignupUrl());
        provider.set("url", oauthSession.getUrl(redirectUrl  + "/oauth2callback"));
        return provider;
    }

    private OauthDemoAppSession findOrCreateSession(HttpExchange exchange) {
        String sessionId = getCookies(exchange)
                .computeIfAbsent("session_key", (key) -> generateSessionKey());
        exchange.getResponseHeaders().add("Set-Cookie", "session_key=" + sessionId);
        return sessions.computeIfAbsent(sessionId, (key) -> new OauthDemoAppSession());
    }

    private String generateSessionKey() {
        byte[] key = new byte[32];
        sessionKeyGenerator.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private Map<String,String> getCookies(HttpExchange exchange) {
        return toMap(exchange.getRequestHeaders().getFirst("Cookie"), ";", "=");
    }


    private boolean handlePublicFile(HttpExchange exchange) throws IOException {
        Path localFile = publicFiles.resolve(exchange.getRequestURI().getPath().substring(1));

        if (!localFile.startsWith(publicFiles)) return false;
        if (Files.isDirectory(localFile)) {
            localFile = localFile.resolve("index.html");
        }
        if (!Files.isRegularFile(localFile)) return false;

        try (OutputStream body = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, 0);
            Files.copy(localFile, body);
        }

        return true;
    }

    public static Map<String,String> toMap(String query, String delimiter, String split) {
        if (query == null) return new HashMap<>();
        return Arrays.stream(query.split(delimiter)).collect(Collectors.toMap(
                (p) -> p.trim().substring(0, p.trim().indexOf(split)),
                (p) -> p.trim().substring(1 + p.trim().indexOf("="))));
    }


}
