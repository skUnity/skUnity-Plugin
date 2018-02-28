/*
 *  YAMLManager - by nfell2009
 *  All rights reserved
 *  Version: 1.3
 * 
 */

package com.skunity.plugin.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import com.skunity.plugin.Logger;

public class YAMLManager {
	
	private JavaPlugin plugin;
	private File file;
	private HashMap<String, ConfigData> configDataMap = new HashMap<String, ConfigData>();
	
	private Boolean isLoaded = false;
	
	// CONSTRUCT
	public YAMLManager(JavaPlugin p, File f) {
		this.plugin = p;
		this.file = f;
		load();
	}
	
	public YAMLManager(JavaPlugin p, File f, Boolean autoLoad) {
		this.plugin = p;
		this.file = f;
		if(autoLoad) {
			load();
		}
	}
	
	public void ensureLoaded() {
		if(!isLoaded()) {
			load();
		}
	}
	
	public Boolean isLoaded() {
		return isLoaded;
	}
	
	public void load() {
		isLoaded = true;
		configDataMap = new HashMap<String, ConfigData>();
		if(this.file == null) {
			Logger.severe("Unable to access the config file!");
			return;
		}
		try {
			LineIterator it = FileUtils.lineIterator(this.file, "UTF-8");
			try {
			    while (it.hasNext()) {
			    	String line = it.nextLine();
			    	if(line.startsWith("#")) { continue; }
			    	if(line == "" || line == " " || line == System.lineSeparator()) { continue; }
			    	String[] lineSplit = null;
			    	if(line.contains(": ")) {
			    		lineSplit = line.split(": ");
			    	} else if(line.contains(":")) {
			    		lineSplit = line.split(":");
			    	} else {
			    		continue;
			    	}
			    	if(lineSplit == null) { continue; }

			    	ConfigData cd = new ConfigData(lineSplit[0], lineSplit[1]);
			    	configDataMap.put(lineSplit[0], cd);
			    }
			} finally {
			    it.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ConfigData getConfigData(String key) {
		if(configDataMap.containsKey(key)) {
			return configDataMap.get(key);
		} else {
			Logger.severe("skUnity tried to access '" + key + "', however, there was no value for it. Is your config out of date?");
			return null;
		}
	}
	
	public String getString(String key) {
		ConfigData cd = getConfigData(key);
		if(cd == null) {
			return "";
		}
		return cd.value;
	}
	
	public Double getDouble(String key) {
		ConfigData cd = getConfigData(key);
		if(cd == null) {
			return 0.0;
		}
		if(CoreUtil.isDouble(cd.value)) {
			return Double.valueOf(cd.value);
		} else {
			return 0.0;
		}
	}
	
	public Integer getInt(String key) {
		return getInteger(key);
	}
	
	public Integer getInteger(String key){
		ConfigData cd = getConfigData(key);
		if(cd == null) {
			return 0;
		}
		if(StringUtils.isNumeric(cd.value)) {
			return Integer.valueOf(cd.value);
		} else {
			return 0;
		}
	}
	
	public Boolean getBoolean(String key) {
		ConfigData cd = getConfigData(key);
		if(cd == null) {
			return false;
		}
		if(cd.value.equalsIgnoreCase("true") || cd.value.equalsIgnoreCase("false")) {
			return Boolean.valueOf(cd.value);
		} else {
			return false;
		}
	}
	
	public void update(Integer highestConfigVersion) {
		ensureLoaded();
		Integer configVersion = getInteger("config-version");
		if(highestConfigVersion <= configVersion) {
			return;
		}
		
		file.delete();
		plugin.saveDefaultConfig();
		plugin.getConfig().options().copyDefaults(true);

		try {
			Integer lineNumber = 0;
			LineIterator it = FileUtils.lineIterator(file, "UTF-8");
			String sFile = file.getAbsolutePath();
			try {
			    while (it.hasNext()) {
			    	lineNumber++;
			    	String line = it.nextLine();
			    	if(line.startsWith("#")) { continue; }
			    	if(line == "" || line == " " || line == System.lineSeparator()) { continue; }
			    	String[] lineSplit = null;
			    	if(line.contains(": ")) {
			    		lineSplit = line.split(": ");
			    	} else if(line.contains(":")) {
			    		lineSplit = line.split(":");
			    	} else {
			    		continue;
			    	}
			    	if(lineSplit == null) { continue; }
			    	if(configDataMap.get(lineSplit[0]) == null) { continue; }
			    	if(lineSplit[0].equalsIgnoreCase("config-version")) { 
			    		ConfigData cd = new ConfigData(lineSplit[0], String.valueOf(highestConfigVersion));
			    		configDataMap.put(lineSplit[0], cd);
			    	}
			    	if(lineSplit[1] == configDataMap.get(lineSplit[0]).value) { continue; }
			    	
			    	String newLineContent;
			    	
			    	newLineContent = lineSplit[0] + ": " + configDataMap.get(lineSplit[0]).value;

			        ChangeLineInFile changeFile = new ChangeLineInFile();
			        changeFile.changeALineInATextFile(sFile, newLineContent, lineNumber);
			    }
			} finally {
			    it.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}