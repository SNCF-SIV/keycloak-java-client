package com.sncf.siv.example;

import com.sncf.siv.example.security.KeycloakClient;
import com.sncf.siv.example.security.KeycloakClient.TokenCollection;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

		// initiate the Keycloak connection
		String keycloakServer = "http://localhost:8080/auth";
		String realm = "your_realm";
		String clientId = "your_client_id";
		String password = "your_password";
		String username = "your_username";

		KeycloakClient kc = new KeycloakClient(keycloakServer, realm, clientId, username, password);

		// retrieve tokens
		TokenCollection tokens = kc.getTokens();

		// Once we have the tokens, we can use the access token to call the protected API throught APIMAN
		try(CloseableHttpClient httpclient = HttpClients.createDefault()) {

			String apiEndpoint = "http://localhost:8080/my_awesome_api";
			HttpGet get = new HttpGet(apiEndpoint);
			get.setHeader("Authorization", "Bearer " + tokens.getAccessToken());

			String result = httpclient.execute(get, response -> {

				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					return EntityUtils.toString(response.getEntity());

				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			});

			System.out.println("API response : " + result);
		}


		// eventually you'll have to refresh the tokens.
		TokenCollection newTokens = kc.refreshTokens(tokens.getRefreshToken());

		System.out.println("old access token : " + tokens.getAccessToken());
		System.out.println("new access token : " + newTokens.getAccessToken());

	}
}

