package com.skunity.plugin.files;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SKUResourceData {

	// JSON values as variables
	public String hash, result, version_string, title, author, filename;
	public Integer resource_id, this_version, latest_version, author_id;
	public Boolean is_latest;
	
	public HashMap<String, Object> dynValues = new HashMap<String, Object>();
	
	public JsonObject jsonData;
	
	// Custom variables
	public String download_url;
			
	public SKUResourceData(JsonObject data) {
		jsonData = data;
		
		setValue("hash", "String");
		setValue("result", "String");
		setValue("version_string", "String");
		setValue("title", "String");
		setValue("author", "String");
		setValue("filename", "String");
		
		setValue("resource_id", "Integer");
		setValue("this_version", "Integer");
		setValue("latest_version", "Integer");
		setValue("author_id", "Integer");
		
		setValue("is_latest", "Boolean");
		
		download_url = MessageFormat.format("https://forums.skunity.com/resources/{0}/download?version={1}", resource_id, latest_version);
	}
		
	public Object get(String key) {
		return dynValues.get(key);
	}
	
	public String getString(String key) {
		return (String) dynValues.get(key);
	}
	
	public Integer getInteger(String key) {
		return (Integer) dynValues.get(key);
	}
	
	public Boolean getBoolean(String key) {
		return (Boolean) dynValues.get(key);
	}
	
	public void setValue(String key) {
		if(jsonData.has(key)) {
			JsonPrimitive o = jsonData.get(key).getAsJsonPrimitive();
			if(o.isString()) {
				setValue(key, "String");
			} else if(o.isNumber()) {
				setValue(key, "Integer");
			} else if(o.isBoolean()) {
				setValue(key, "Boolean");
			} else {
				System.out.println("Unknown instanceof for " + key + ". Real type: " + o.getClass().getCanonicalName());
			}
		} else {		
			System.out.print("Attempted to get " + key + " but is not set");
		}
	}
	
	public void setValue(String key, String type) {
		Object value = null;
		try {
			if(type == "String") {
				value = jsonData.get(key).getAsString();
			} else if(type == "Integer") {
				value = Integer.valueOf(jsonData.get(key).getAsString());
			} else if(type == "Boolean") {
				value = Boolean.valueOf(jsonData.get(key).getAsString());
			} else {
				System.out.println("Invalid type given for " + key);
				return;
			}
		} catch(Exception e) {
			/*System.out.println("An exception happened for " + key);*/
			/*e.printStackTrace();*/
			return;
		}
		if(value == null) {
			System.out.println("Null value for " + key);
			return;
		}
		dynValues.put(key, value);
		
		
		try {
			SKUResourceData test = this;
	        Class<?> c = test.getClass();
			Field field = c.getField(key);
			field.setAccessible(true);
			field.set(this, value);
			System.out.print("Set " + key + " to " + value);
			return;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			System.out.println("An exception happened for (2) " + key);
			e.printStackTrace();
			return;
		}
		
		
	}
}
