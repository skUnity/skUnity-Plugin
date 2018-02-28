package com.skunity.plugin.commands;

import java.io.File;

import org.bukkit.command.CommandSender;

import com.skunity.plugin.Config;
import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;
import com.skunity.plugin.utils.YAMLManager;

public class SKUReloadConfig extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUReloadConfig() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "reload";
		info.permission = null;
		info.aliases = new String[]{"reloadconfig", "configreload", "rc", "cr", "config"};
		info.description = "Reload the configutation file";
		info.usage = "/sku reload";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		PlayerChat.info("Reloading config...");
		PlayerChat.sendBlank();
		
		Main.yamlConfig = new YAMLManager(Main.get(), new File("./plugins/skUnity/config.yml"));
		Main.yamlConfig.update(Main.getConfigurationVersion());
		
		Config.setup();
		CoreUtil.setAPIURL();
		PlayerChat.success("Reloaded the configuration file!");
	}

}
