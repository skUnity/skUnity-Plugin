package com.skunity.plugin.updater;

import java.text.MessageFormat;

import com.google.gson.JsonObject;

public class SKUResourceData {

	// JSON values as variables
	public String hash, result, version_string, title, author;
	public Integer resource_id, this_version, latest_version, author_id;
	public Boolean is_latest;
	
	// Custom variables
	public String download_url;
	
	public SKUResourceData(JsonObject data) {
		hash = data.get("hash").getAsString();
		result = data.get("result").getAsString();
		version_string = data.get("version_string").getAsString();
		title = data.get("title").getAsString();
		author = data.get("author").getAsString();
		
		resource_id = Integer.valueOf(data.get("resource_id").getAsString());
		this_version = Integer.valueOf(data.get("this_version").getAsString());
		latest_version = Integer.valueOf(data.get("latest_version").getAsString());
		author_id = Integer.valueOf(data.get("author_id").getAsString());
		
		is_latest = Boolean.valueOf(data.get("is_latest").getAsString());
		
		download_url = MessageFormat.format("https://forums.skunity.com/resources/{0}/download?version={1}", resource_id, latest_version);
	}
}
