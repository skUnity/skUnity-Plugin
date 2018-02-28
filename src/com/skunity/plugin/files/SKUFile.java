package com.skunity.plugin.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skunity.plugin.Main;
import com.skunity.plugin.utils.CoreUtil;

public class SKUFile {

	private String md5 = null;
	private String path;
	private File file;
	private JsonObject jsonData = null;
	private Boolean onSkUnity = false;
	private HashMap<String, String> urlData = new HashMap<String, String>();
	private SKUResourceData resourceData;
	public SKUFile(File path, Boolean intilise) {
		this.path = path.getPath();
		this.file = path;
		try {
			FileInputStream fis = new FileInputStream(path);
			this.md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if(intilise) {
			intilise();
		}
	}
	
	public SKUFile(File path) {
		this(path, true);
	}
	
	public void intilise() {
		if(jsonData == null) {
			fetchJSONData();
		}
		if(jsonData != null) {
			resourceData = new SKUResourceData(jsonData);
			assignURLs();
		}
	}
	
	public SKUResourceData getResourceData() {
		return resourceData;
	}
		
	public void setJSONData(JsonObject data) {
		jsonData = data;
	}
	
	public JsonObject getJSONData() {
		if(jsonData == null) {
			fetchJSONData();
		}
		return jsonData;
	}
	
	public String getHash() {
		return md5 == null ? "" : md5;
	}
	
	public String getPath() {
		return path;
	}
	
	public File getFile() {
		return file;
	}
	
	public Boolean onSkUnity() {
		return onSkUnity;
	}
	
	public void assignURLs() {
		if(isJSONReady()) {
			assignURL("author",
					String.format("https://forums.skunity.com/resources/authors/%s.%s/", resourceData.author, resourceData.author_id)
			);
			assignURL("download",
					String.format("https://forums.skunity.com/resources/%s/download?version=%s", resourceData.resource_id, resourceData.latest_version)
			);
			assignURL("page",
					String.format("https://forums.skunity.com/resources/%s/", resourceData.resource_id)
			);
		}
	}
	
	public void assignURL(String key, String value) {
		urlData.put(key, value);
	}
	
	public String getURL(String key) {
		if(urlData.containsKey(key)) {
			return urlData.get(key);
		} else {
			return "";
		}
		
	}
	
	public Boolean fetchJSONData() {
		if(md5 == null) {
			return false;
		}			
		String json = CoreUtil.getData(md5);
		JsonArray jsonData = Main.commandUtils.parseJSON(json);
		for(JsonElement jE : jsonData) {
			JsonObject jO = jE.getAsJsonObject();
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				setJSONData(jO);
				CoreUtil.cacheFile(getHash(), this);
				onSkUnity = true;
			} else {
				onSkUnity = false;
			}
		}
		return onSkUnity;
	}
	
	public Boolean isJSONReady() {
		if(md5 == null) {
			return false;
		}
		if(jsonData == null) {
			intilise();
			if(jsonData == null) {
				return false;
			}
		}
		return true;
	}
	
	public String getDownloadURL() {
		if(!isJSONReady()) {
			intilise();
		}
		return resourceData.download_url;
	}
}
