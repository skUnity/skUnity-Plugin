package com.skunity.plugin;

public class Config {
	public static boolean OP_OVERRIDE;
	public static boolean IGNORE_DISABLED_SCRIPTS;
	public static boolean AUTO_DOWNLOAD_UPDATES;
	public static int 	  AUTO_DOWNLOAD_TIMER;
	public static boolean RECURSIVE_SCRIPTS_FOLDER_SEARCH;
	public static boolean DEBUG_ENABLED;
	public static String  API_KEY;
	public static boolean AUTO_RUN_SCRIPTS;
	public static boolean AUTO_RUN_SCRIPTS_AS_CONSOLE;
	public static boolean SHOW_SKRIPT_ERRORS;
	public static boolean AUTO_RUN_PLUGINS;
	
	public static Integer CONFIG_VERSION;
	public static void setup() {
		OP_OVERRIDE 					= Main.yamlConfig.getBoolean("op-override");
		IGNORE_DISABLED_SCRIPTS 		= Main.yamlConfig.getBoolean("ignore-disabled-scripts");
		AUTO_DOWNLOAD_UPDATES 			= Main.yamlConfig.getBoolean("auto-download-updates");
		/*AUTO_DOWNLOAD_TIMER 			= Main.yamlConfig.getInt("auto-download-timer");*/
		/*RECURSIVE_SCRIPTS_FOLDER_SEARCH = Main.yamlConfig.getBoolean("recursive-scripts-folder-search");*/
		DEBUG_ENABLED                   = Main.yamlConfig.getBoolean("enable-debug");
		API_KEY                         = Main.yamlConfig.getString("api-key");
		AUTO_RUN_SCRIPTS                = Main.yamlConfig.getBoolean("auto-run-scripts");
		AUTO_RUN_SCRIPTS_AS_CONSOLE     = Main.yamlConfig.getBoolean("auto-run-scripts-as-console");
		CONFIG_VERSION					= Main.yamlConfig.getInteger("config-version");
		SHOW_SKRIPT_ERRORS				= Main.yamlConfig.getBoolean("show-skript-errors");
		AUTO_RUN_PLUGINS				= Main.yamlConfig.getBoolean("auto-run-plugins");
	}
}
