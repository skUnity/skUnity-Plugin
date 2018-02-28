package com.skunity.plugin.utils;

import java.util.ArrayList;

import net.md_5.bungee.api.ChatColor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skunity.plugin.Config;
import com.skunity.plugin.files.SKUFile;

public class CommandUtils {

	private PlayerChat PlayerChat;
	public CommandUtils(PlayerChat playerChatClass) {
		PlayerChat = playerChatClass;
	}
	
	public String jsonHas(JsonObject jO, String key) {
		if(jO.has(key)) {
			return jO.get(key).getAsString();
		} else {
			return "";
		}
	}
	
	public String customReplace(JsonObject jO, String parse) {
		parse = parse.replaceAll("%title%", jsonHas(jO, "title"));
		parse = parse.replaceAll("%hash%", jsonHas(jO, "hash"));
		parse = parse.replaceAll("%resource_id%", jsonHas(jO, "resource_id"));
		parse = parse.replaceAll("%id%", jsonHas(jO, "id"));
		parse = parse.replaceAll("%name%", jsonHas(jO, "name"));
		parse = parse.replaceAll("%addon%", jsonHas(jO, "addon"));
		
		if(jO.has("title")) {
			parse = parse.replaceAll("%title-32%", cutTitle(jO.get("title"), 32));
		}
		if(jO.has("name")) {
			parse = parse.replaceAll("%name-32%", cutTitle(jO.get("name"), 32));
		}
		
		
		return parse;
	}
	
	public void logUpdateInfo(JsonArray jsonArray, ChatColor mainTextColour, String command, String hover, Boolean showOutOfDate) {
		logUpdateInfo(jsonArray, mainTextColour, command, hover, showOutOfDate, null);
	}
		
    public void logUpdateInfo(JsonArray jsonArray, ChatColor mainTextColour, String command, String hover, Boolean showOutOfDate, String title) {
    	if(jsonArray == null) {
    		PlayerChat.warning(" » No content was found that is on skUnity");
			return;
		}
    	Integer i = 0;
    	UpdateOutput uino = UpdateOutput.UNSET;
    	for(JsonElement jE : jsonArray) {
    		i++;
			JsonObject jO = jE.getAsJsonObject();
			if(!jO.has("result")) {
				jO.addProperty("result", "success");
			}
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				if(showOutOfDate && jO.get("is_latest").getAsBoolean()) {
					if(uino == UpdateOutput.UNSET) {
						uino = UpdateOutput.UP_TO_DATE;
					}
				} else {
					uino = UpdateOutput.DONE;
					//PlayerChat.warning(" » [" + i + "] " + cutTitle(jO.get("title"), 32) + " by " + jO.get("author").getAsString());
					if(title == null) {
						PlayerChat.sendJSONCommandHover(
								" » [" + i + "] " + cutTitle(jO.get("title"), 32) + " by " + jO.get("author").getAsString(),
								mainTextColour,
								customReplace(jO, command),
								customReplace(jO, hover),
								PlayerChat.getChatColour("warning"));
					} else {
						PlayerChat.sendJSONCommandHover(
								" » [" + i + "] " + customReplace(jO, title),
								mainTextColour,
								customReplace(jO, command),
								customReplace(jO, hover),
								PlayerChat.getChatColour("warning"));
						
					}
				}
			}
		}
    	if(uino == UpdateOutput.UNSET) {
			uino = UpdateOutput.NOTHING;
		}
    	if(uino != UpdateOutput.DONE) {
    		PlayerChat.warning(uino.getValue());
    	}
    }
    
    public enum UpdateOutput {
    	NOTHING(" » No content was found that is on skUnity"),
    	UP_TO_DATE(" » All of your content is up to date!"), 
    	DONE(""),
    	UNSET(" » Oops! It looks like something went wrong!");

        private final String message;
        UpdateOutput(String message) {
        	this.message = message;
        }
        public String getValue() {
        	return message;
        }
    }
    
    public String cutTitle(JsonElement input, Integer cutLength) {
    	return cutString(input.getAsString(), cutLength);
    }
    
    public String cutString(String input, Integer cutLength) {
    	if(input.length() <= cutLength) {
    		return input;
    	} else {
    		String tString = input.substring(0, cutLength);
    		if(tString.endsWith(",") || tString.endsWith(" ")) {
    			tString = tString.substring(0, tString.length() - 1);
    		}
    		if(tString.endsWith(" ") || tString.endsWith(",")) {
    			tString = tString.substring(0, tString.length() - 1);
    		}
    		return tString + "...";
    	}
    }
    
	public JsonArray runCheck(String path, Boolean recursive) {
		ArrayList<SKUFile> scripts = CoreUtil.getFiles(path, recursive, false);
		String md5s = null;
		Integer counter = -1;
		for(SKUFile script : scripts) {
			CoreUtil.cacheFile(script.getHash(), script);
			counter++;
			if(md5s == null) {
				md5s = script.getHash();
			} else {
				md5s = md5s + "," + script.getHash();
			}
			//Logger.info("Hash of " + script.getFile().getName() + " is " + script.getHash());
		}
		String json = CoreUtil.getData(md5s);
		JsonArray jsonData = parseJSON(json);
		for(JsonElement jE : jsonData) {
			JsonObject jO = jE.getAsJsonObject();
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				SKUFile script = CoreUtil.getFile(jO.get("hash").getAsString());
				if(script == null) {
					continue;
				}
				script.setJSONData(jO);
				CoreUtil.cacheFile(script.getHash(), script);
			}
		}
		return jsonData;
	}
	
	public JsonArray parseJSON(String json) {
		JsonArray result = null;
		try {
			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
			result = jsonObject.getAsJsonArray("result");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public JsonObject readJSON(String json) {
		JsonObject result = null;
		try {
			result = new JsonParser().parse(json).getAsJsonObject();
		} catch(Exception e) {}
		return result;
	}
	
	public JsonArray getJsonArray(String json, String array) {
		JsonArray result = null;
		try {
			JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
			result = jsonObject.getAsJsonArray(array);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public JsonObject getJsonObject(String json) {
		JsonObject jsonObject = null;
		try {
			jsonObject = new JsonParser().parse(json).getAsJsonObject();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public Object jsonArrayGet(JsonArray array, String member) {
		for(JsonElement jE : array) {
			JsonObject jO = jE.getAsJsonObject();
			return jO.get(member);
		}
		return null;
	}
	
	public JsonArray checkScripts() {
		return runCheck("./plugins/Skript/scripts/", Config.RECURSIVE_SCRIPTS_FOLDER_SEARCH);
	}
	public JsonArray checkPlugins() {
		return runCheck("./plugins/", false);
	}

}
