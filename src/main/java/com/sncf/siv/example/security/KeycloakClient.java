package com.sncf.siv.example.security;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;


/**
 * This code is very simple.
 * Based upon this article : https://github.com/wpic/sample-keycloak-getting-token
 *
 */
@AllArgsConstructor
public final class KeycloakClient {

    private String serverUrl;
    private String realm;
    private String clientId;
    private String username;
    private String password;


    private String getTokenEndpoint() {
        return serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public TokenCollection getTokens() throws IOException {

        HttpPost post = new HttpPost(getTokenEndpoint());
        List<NameValuePair> params = asList(new BasicNameValuePair("grant_type", "password"),
                new BasicNameValuePair("client_id", clientId),
                new BasicNameValuePair("username", username),
                new BasicNameValuePair("password", password)
        );

        post.setEntity(new UrlEncodedFormEntity(params));

        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            return httpclient.execute(post, response -> {
                ObjectMapper mapper = new ObjectMapper();
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    return mapper.readValue(response.getEntity().getContent(), TokenCollection.class);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            });
        }
    }

    public TokenCollection refreshTokens(String refreshToken) throws IOException {

        HttpPost post = new HttpPost(getTokenEndpoint());
        List<NameValuePair> params = asList(new BasicNameValuePair("grant_type", "refresh_token"),
                new BasicNameValuePair("refresh_token", refreshToken),
                new BasicNameValuePair("client_id", clientId)
        );

        post.setEntity(new UrlEncodedFormEntity(params));

        try(CloseableHttpClient httpclient = HttpClients.createDefault()){
            return httpclient.execute(post, response -> {
                ObjectMapper mapper = new ObjectMapper();
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    return mapper.readValue(response.getEntity().getContent(), TokenCollection.class);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            });
        }
    }


    @Data
    public final class TokenCollection {

        @JsonProperty("access_token")
        String accessToken;

        @JsonProperty("expires_in")
        Integer expiresIn;

        @JsonProperty("refresh_expires_in")
        Integer refreshExpiresIn;

        @JsonProperty("refresh_token")
        String refreshToken;

        @JsonProperty("token_type")
        String tokenType;

        @JsonProperty("id_token")
        String idToken;

        @JsonProperty("not-before-policy")
        Integer notBeforePolicy;

        @JsonProperty("session_state")
        String sessionState;
    }


}
