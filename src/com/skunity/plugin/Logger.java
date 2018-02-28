package com.skunity.plugin;

public class Logger {

	public static void info(String msg) {
		Main.bukkitLogger.info("[skUnity] " + msg);
	}
	
	public static void warning(String msg) {
		Main.bukkitLogger.warning("[skUnity] " + msg);
	}
	
	public static void severe(String msg) {
		Main.bukkitLogger.severe("[skUnity] " + msg);
	}
	
	public static void debug(String msg) {
		if(Config.DEBUG_ENABLED) {
			Main.bukkitLogger.info("[skUnity] " + msg);
		}
	}
	
	public static void skriptError(String msg) {
		if(Config.SHOW_SKRIPT_ERRORS) {
			Main.bukkitLogger.warning("[skUnity][Skript Error] " + msg);
		}
	}
	
	

}
