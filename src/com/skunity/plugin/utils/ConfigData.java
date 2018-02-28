package com.skunity.plugin.utils;

public class ConfigData {

	public String value;
	public String key;
	public ConfigData(String key, String value) {
		if(value.startsWith("'") && value.endsWith("'")) {
			value = value.substring(1, value.length() - 1);
		}
		this.value = value;
		this.key = key;
	}

}
