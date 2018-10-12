package com.example.ssd.facebookoauthsample.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.ssd.facebookoauthsample.data.DataHolder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AuthController {
	
	public static final String TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token";
	public static final String ID_FEILD_ENDPOINT ="https://graph.facebook.com/v2.8/me?fields=id";
	public static final String GRAPH_ENDPOINT ="https://graph.facebook.com/v2.8/";
	public static final String GRANT_TYPE = "authorization_code";
	public static final String REDIRECT_URI = "http://localhost:8080/validate";
	public static final String CLIENT_ID = "244993339493539";
	public static final String CLIENT_SECRET = "b535677dd9e4c3a4db2ee3c9d3267d98";


	@RequestMapping("/profileviewapp/callback")
	public ResponseEntity<?> getResource(@RequestParam("code") String code)
			throws JSONException, IOException {
		String authorizationCode = code;
		System.out.println("The code is = "+authorizationCode);
		if (authorizationCode != null && authorizationCode.length() > 0) {
			
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Content-Type", "text/plain");
			String bodyPart1 = ("grant_type="+ URLEncoder.encode(GRANT_TYPE, StandardCharsets.UTF_8.name()));
			String bodyPart2 = ("redirect_uri="+ URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8.name()));
			String bodyPart3 = ("code="+ URLEncoder.encode(authorizationCode, StandardCharsets.UTF_8.name()));
			String bodyPart4 = ("client_id="+ URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8.name()));

			String body=(bodyPart1+"&"+bodyPart2+"&"+bodyPart3+"&"+bodyPart4);
			
			String clientCredentials = CLIENT_ID + ":" + CLIENT_SECRET;
			String encodedClientCredentials = new String(Base64.encodeBase64(clientCredentials.getBytes()));
			httpHeaders.set("Authorization", "Basic " + encodedClientCredentials);
			HttpEntity<String> httpEntity = new HttpEntity<String>(body, httpHeaders);
			RestTemplate restTemplate = new RestTemplate();
			String responce = restTemplate.postForObject(TOKEN_ENDPOINT, httpEntity, String.class);

			JSONObject responceObj = new JSONObject(responce);
			
			String accessToken = null;

			try {
				
				accessToken = responceObj.get("access_token").toString();
				System.out.println("Access token: " + accessToken);

			} catch (JsonParseException e) {
				System.out.println("Error while parsing the response from facebook : " + e.getMessage());
			}

			
			
			RestTemplate idRestTemplate = new RestTemplate();
			HttpHeaders idkenHeader = new HttpHeaders();
			idkenHeader.add("Authorization", "Bearer " + accessToken);
			HttpEntity<?> idHttpEntiy = new HttpEntity<>(idkenHeader);
 			ResponseEntity<String> idResponce = idRestTemplate.exchange(ID_FEILD_ENDPOINT, HttpMethod.GET,idHttpEntiy,String.class);
 			
 			ObjectMapper mapper = new ObjectMapper();
 			JsonNode idRoot = mapper.readTree(idResponce.getBody());
 			JsonNode idData = idRoot.path("id");
 			System.out.println("USER_ID = "+idData.asText());
			
			
			String requestUrl = GRAPH_ENDPOINT+"/me?fields=id,first_name,last_name,email,picture";

			RestTemplate dataRestTemplate = new RestTemplate();
			HttpHeaders tokenHeader = new HttpHeaders();
			tokenHeader.add("Authorization", "Bearer " + accessToken);
			HttpEntity<?> dataHttpEntiy = new HttpEntity<>(tokenHeader);
 			ResponseEntity<String> dataResponce = dataRestTemplate.exchange(requestUrl, HttpMethod.GET,dataHttpEntiy,String.class);
			

 			JsonNode root = mapper.readTree(dataResponce.getBody());
 			
// 			String imageRequestUrl = GRAPH_ENDPOINT+"me/picture?type=normal";
// 			
// 		
// 			ResponseEntity<String> dataImageResponce = dataRestTemplate.exchange(imageRequestUrl, HttpMethod.GET,dataHttpEntiy,String.class);
//			
// 			JsonNode rootImge = mapper.readTree(dataImageResponce.getBody());
//

			DataHolder.getInstance().addResource(String.valueOf("profile"), root);
//			DataHolder.getInstance().addResource(String.valueOf("image"), rootImge);

			
			
			
			return new ResponseEntity<>(HttpStatus.OK);
		

		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping("/profileviewapp/user")
	public ResponseEntity<?> getAllFeeds(){
		HashMap<String, JsonNode> detailsNode = (HashMap<String, JsonNode>) DataHolder.getInstance().getAllResources();
		return new ResponseEntity<>(detailsNode.values(),HttpStatus.OK);
	
	}
	
	
	
	

}
