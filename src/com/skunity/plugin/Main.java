package com.skunity.plugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

import com.skunity.plugin.commands.SKUCheck;
import com.skunity.plugin.commands.SKUDocs;
import com.skunity.plugin.commands.SKUGet;
import com.skunity.plugin.commands.SKUHelp;
import com.skunity.plugin.commands.SKUMyDownloads;
import com.skunity.plugin.commands.SKUInfo;
import com.skunity.plugin.commands.SKUReloadConfig;
import com.skunity.plugin.commands.SKUUpdate;
import com.skunity.plugin.updater.Updater;
import com.skunity.plugin.utils.CommandRegistry;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.YAMLManager;

public class Main extends JavaPlugin {
	
	public static java.util.logging.Logger bukkitLogger = Bukkit.getLogger();
	public static Class<?> logger;
	public final static Double version = 1.1;
	public static CommandRegistry commandRegistry = new CommandRegistry();
	public static CommandUtils commandUtils = new CommandUtils(null);
	public static Main skUnity;
	public static File dataFolder;
	public static File backupsFolder;
	public static YAMLManager yamlConfig;
	
	private static final Integer configVersion = 3;
	
	public void onEnable() {
		System.setProperty("http.agent", "The official skUnity Plugin");
		if(!getDataFolder().exists() && !getDataFolder().isDirectory()) {
			getDataFolder().mkdirs();
		}
		dataFolder = getDataFolder();
		backupsFolder = new File(getDataFolder().getAbsoluteFile() + "/File Backups");
		if(backupsFolder.exists()) {
			backupsFolder.delete();
		}
		if(!backupsFolder.exists() && !backupsFolder.isDirectory()) {
			backupsFolder.mkdirs();
		}
		this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
				
		yamlConfig = new YAMLManager(this, new File("./plugins/skUnity/config.yml"));
		yamlConfig.update(getConfigurationVersion());
		
		Config.setup();
		CoreUtil.setAPIURL();
		
		
		Updater u = new Updater();
		u.runUpdater();
		int updateTimer = Config.AUTO_DOWNLOAD_TIMER;
		if (updateTimer > 0) {
			u.runUpdaterAsync(this, updateTimer);
		}
		
		// register main sku/skunity command
		commandRegistry.register("sku", new SkunityCommand("sku"));
		// register sub-commands
		commandRegistry.registerSubCommand(new SKUCheck());
		commandRegistry.registerSubCommand(new SKUUpdate());
		commandRegistry.registerSubCommand(new SKUInfo()); 
		commandRegistry.registerSubCommand(new SKUMyDownloads());
		commandRegistry.registerSubCommand(new SKUHelp());
		commandRegistry.registerSubCommand(new SKUGet());
		commandRegistry.registerSubCommand(new SKUReloadConfig());
		commandRegistry.registerSubCommand(new SKUDocs());
		
		SkriptAddon skUnityAddon = Skript.registerAddon(this);
		try {
			skUnityAddon.loadClasses("com.skunity.plugin", "skript");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static CommandRegistry getCommandRegistry() {
		return commandRegistry;
	}
	
	public static Main get() {
		return skUnity;
	}
	
	public static Integer getConfigurationVersion() {
		return configVersion;
	}
	
	
}
