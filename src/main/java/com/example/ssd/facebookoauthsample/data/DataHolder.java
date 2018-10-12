package com.example.ssd.facebookoauthsample.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

public class DataHolder {

	private static volatile Map<String, JsonNode> resourceMap;
	private static DataHolder resourceDataHolder;

	private DataHolder() {

		resourceMap = new HashMap<String, JsonNode>();
	}

	public static DataHolder getInstance() {

		if (resourceDataHolder == null) {

			synchronized (DataHolder.class) {
				if (resourceDataHolder == null) {
					resourceDataHolder = new DataHolder();
				}
			}
		}

		return resourceDataHolder;
	}

	public void addResource(String key, JsonNode value) {
		resourceMap.put(key, value);
	}

	public JsonNode getResource(String key) {
		return resourceMap.get(key);
	}
	
	public Map<String, JsonNode> getAllResources(){
		return resourceMap;
	}

}
