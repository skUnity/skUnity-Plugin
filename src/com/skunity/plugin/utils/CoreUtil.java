package com.skunity.plugin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import ch.njol.skript.Skript;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skunity.plugin.Config;
import com.skunity.plugin.Logger;
import com.skunity.plugin.Main;
import com.skunity.plugin.files.SKUFile;
import com.skunity.plugin.files.SKUResourceData;

public class CoreUtil {

	public static String API_URL;
	public static String DOCS_API_URL;
	
	public static void setAPIURL() {
		API_URL = "https://docs.skunity.com/api/?function=forumsAPI&key=" + Config.API_KEY + "&request=";
		DOCS_API_URL = "https://docs.skunity.com/api/?&key=" + Config.API_KEY + "&function=";
	}
	
	public static String booleanToString(Boolean b) {
		return b ? "yes" : "no";
	}
	
	public static ArrayList<SKUFile> getFiles(String directory, Boolean recursive) {
		return getFiles(directory, recursive, true);
	}
	
	public static ArrayList<SKUFile> getFiles(String directory, Boolean recursive, Boolean intilise) {
		File path = new File(directory);

	    File[] files = path.listFiles();
	    ArrayList<SKUFile> outputFiles = new ArrayList<SKUFile>();
	    
	    for(File tempFile : files) {
	    	if (tempFile.isFile()){
	    		if(!tempFile.getName().endsWith(".sk")) {
	    			if(!tempFile.getName().endsWith(".jar")) {
	    				continue;
	    			}
	    		}
	    		if(tempFile.getName().startsWith("-")) {
	    			if(!Config.IGNORE_DISABLED_SCRIPTS) {
	    				outputFiles.add(new SKUFile(tempFile, intilise));
	    			}
	    		} else {
	    			outputFiles.add(new SKUFile(tempFile, intilise));
	    		}
	        } else if (recursive && Config.RECURSIVE_SCRIPTS_FOLDER_SEARCH) {
	        	String tempDirectory;
	        	if(directory.endsWith("/")) {
	        		tempDirectory = directory + tempFile;
	        	} else {
	        		tempDirectory = directory + "/" + tempFile;
	        	}
	        	for(SKUFile otherFile : getFiles(tempDirectory, recursive)) {
	        		outputFiles.add(otherFile);
	        	}
	        }
	    }
		return outputFiles;
	}
	
	public static SKUFile resolveFile(String originalInput) {
		String input = makeFilename(originalInput, ".sk");
		SKUFile foundFile = null;
		// attempt scripts first...
		ArrayList<SKUFile> scripts = getFiles("./plugins/Skript/scripts/", true, false);
		for(SKUFile script : scripts) {
			if(script.getFile().getName().equalsIgnoreCase(input) || script.getHash().equalsIgnoreCase(originalInput)) {
				foundFile = script;
				break;
			}
		}
		if(foundFile == null) {
			input = makeFilename(originalInput, ".jar");
			ArrayList<SKUFile> addons = getFiles("./plugins/", false, false);
			for(SKUFile script : addons) {
				if(script.getFile().getName().equalsIgnoreCase(input) || script.getHash().equalsIgnoreCase(originalInput)) {
					foundFile = script;
					break;
				}
			}
		}
		if(foundFile == null) {
			return null;
		}
		foundFile.intilise();
		return foundFile;
	}
	
	public static String makeFilename(String input, String extension) {
		if(!input.endsWith(extension)) {
			input += extension;
		}
		return input;
	}
	
	// local file caching (not saved)
	public static HashMap<String, SKUFile> fileCache = new HashMap<String, SKUFile>();
	public static void cacheFile(String hash, SKUFile file) {
		if(fileCache.containsKey(hash)) {
			fileCache.remove(hash);
		}
		fileCache.put(hash, file);
	}
	
	public static SKUFile getFile(String hash) {
		if(fileCache.containsKey(hash)) {
			return fileCache.get(hash);
		} else {
			return null; 
		}
	}
		
	public static String doAPIRequest(String function, String args) {
		String line = "";
		try {
			URL url = new URL(API_URL + function + args);
			Logger.debug("Request made to " + API_URL + function + args);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "The official skUnity Plugin");	
		    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    
		    line = in.readLine();
		    in.close();
		    Logger.debug("Response: " + line);
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return line;
	}
	
	public static String doDocsAPIRequest(String function, String args) {
		String line = "";
		try {
			
			URL url = new URL(DOCS_API_URL + function + args);
			Logger.debug("Request made to " + DOCS_API_URL + function + args);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "The official skUnity Plugin");	
		    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    
		    line = in.readLine();
		    in.close();
		    Logger.debug("Response: " + line);
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return line;
	}
	
	public static String getData(String md5) {
		String line = "";
		try {
			URL url = new URL(API_URL + "skUnityAutoUpdater&hash=" + md5);
			Logger.debug("Request made to " + API_URL + "skUnityAutoUpdater&hash=" + md5);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent", "The official skUnity Plugin");	
		    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    
		    line = in.readLine();
		    in.close();
		    Logger.info("Response: " + line);
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		return line;
	}
	
	public static Boolean downloadFile(String downloadURL, String path, PlayerChat PlayerChat, String success, String failed) {
		try {
			/*BackupFile backup = new BackupFile(file.getFile());
			backup.createBackup();*/
			File newFile = HttpDownloadUtility.downloadFile(downloadURL, path);
			if(newFile.exists()) {
				PlayerChat.success(success);
				return true;
			} else {
				/*backup.restoreBackup();*/
				PlayerChat.warning(failed);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Boolean justDownloadFile(String downloadURL, String path) {
		try {
			/*BackupFile backup = new BackupFile(file.getFile());
			backup.createBackup();*/
			File newFile = HttpDownloadUtility.downloadFile(downloadURL, path);
			if(newFile.exists()) {
				return true;
			} else {
				/*backup.restoreBackup();*/
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static File returndownloadFile(String downloadURL, String path, PlayerChat PlayerChat, String success, String failed) {
		try {
			/*BackupFile backup = new BackupFile(file.getFile());
			backup.createBackup();*/
			File newFile = HttpDownloadUtility.downloadFile(downloadURL, path);
			if(newFile.exists()) {
				PlayerChat.success(success);
				return newFile;
			} else {
				/*backup.restoreBackup();*/
				PlayerChat.warning(failed);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static File justReturndownloadFile(String downloadURL, String path) {
		try {
			File newFile = HttpDownloadUtility.downloadFile(downloadURL, path);
			if(newFile.exists()) {
				return newFile;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getTypeFromString(String input) {
		if(input.startsWith("'") && input.endsWith("'")) {
			return "String";
		} else if(input == "true" || input == "false") {
			return "Boolean";
		} else if(isDouble(input)) {
			return "Double";
		} else if(StringUtils.isNumeric(input)) {
			return "Integer";
		} else {
			return "String";
		}
	}
	
	private static Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?");
	public static boolean isDouble(String string) {
	    return doublePattern.matcher(string).matches();
	}
	
	public static SKUResourceData getResourceData(String resourceId) {		
		String json = CoreUtil.doAPIRequest("getResource", "&resourceid=" + resourceId);
		JsonArray jsonData = Main.commandUtils.parseJSON(json);
		JsonObject jsonObject = null;
		for(JsonElement jE : jsonData) {
			JsonObject jO = jE.getAsJsonObject();
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				jsonObject = jO;
				break;
			} else {
				return null;
			}
		}
		if(jsonObject == null) {
			return null;
		} else {
			return new SKUResourceData(jsonObject);
		}
	}
	
	public static Boolean allowLateSkriptRegisteration(Boolean allowReg) {
        try {
        	Field field;
			field = Skript.class.getDeclaredField("acceptRegistrations");
			field.setAccessible(true);
	        field.set(null, allowReg);
	        return true;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			return false;
		}
        
    }

}
