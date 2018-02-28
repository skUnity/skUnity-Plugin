package com.skunity.plugin.updater;

import java.io.File;

import org.bukkit.Bukkit;

import com.skunity.plugin.Logger;
import com.skunity.plugin.Main;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.PlayerChat;

public class Updater {
	CommandUtils CommandUtils;
	
	public void runUpdaterAsync(Main main, int time) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(main, runnableUpdate(), 0, 20 * time);
	}

	private Runnable runnableUpdate() {
		return new Runnable(){
			@Override
			public void run() {
				runUpdater();
			}
		};
	}

	public void runUpdater() {
		File skriptFolder = new File("./plugins/Skript/");
		CommandUtils = new CommandUtils(new PlayerChat(Logger.class));
		// [Map] mode values
		// 0 = None
		// 1 = Skript
		// 2 = Plugins
		// 3 = Skript and Plugins
		Integer mode = 0;
		if(Bukkit.getPluginManager().getPlugin("Skript") == null || !skriptFolder.exists() || !skriptFolder.isDirectory()) {
			Logger.severe("[Auto Updater] Unable to locate Skript, the Skript folder or the Skript folder or is not a directory at " + skriptFolder.getPath());
			Logger.severe("[Auto Updater] skUnity Auto Updater will continue, but will only check jars located in /plugins/");
			mode = 2;
		} else {
			mode = 3;
		}
		Logger.debug("Updater started in mode " + mode);
		// TODO:
		// Remove this line below because its just for debugging
		mode = 1;
		/*if(mode == 1 || mode == 3) {
			CommandUtils.logUpdateInfo(SKUCheck.checkScripts());
		}
		if(mode == 2 || mode == 3) {
			CommandUtils.logUpdateInfo(SKUCheck.checkPlugins());
		}*/
	}

}
