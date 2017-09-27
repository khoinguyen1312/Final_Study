package com;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class uniRest {
    public static JSONObject uniRestPost(String url, String authorKey, JSONObject json) throws UnirestException {
    	
    	HttpResponse<String> response = Unirest.post(url)
    			  .header("authorization", authorKey)
    			  .header("content-type", "application/json")
    			  .header("cache-control", "no-cache")
    			  .header("postman-token", "abc281f9-397e-628b-9b2c-297a12b8e657")
    			  .body(json.toString())
    			  .asString();
    	
    	return new JSONObject(response.getBody());
    }
    
}
