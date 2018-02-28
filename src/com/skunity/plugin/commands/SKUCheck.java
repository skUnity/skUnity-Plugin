package com.skunity.plugin.commands;

import org.bukkit.command.CommandSender;

import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUCheck extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
		
	public SKUCheck() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "check";
		info.permission = "skunity.command.check";
		info.aliases = new String[]{""};
		info.description = "Check all your plugins and scripts for out of date content";
		info.usage = "/sku check [scripts|plugins|{all}]";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		String out_of_date = "Here's any out of date content";
		if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
			PlayerChat.info(out_of_date);
			PlayerChat.info("Scripts:");
			CommandUtils.logUpdateInfo(
					CommandUtils.checkScripts(),
					PlayerChat.getChatColour("warning"),
					"/sku update %hash%",
					"%title% is out date, click to update it",
					true
			);
	    	PlayerChat.info("Plugins:");
	    	CommandUtils.logUpdateInfo(
	    			CommandUtils.checkPlugins(),
					PlayerChat.getChatColour("warning"),
					"/sku update %hash%",
					"%title% is out date, click to update it",
					true
			);
		} else if(args[1].equalsIgnoreCase("scripts")) {
			PlayerChat.info(out_of_date);
			PlayerChat.info("Scripts:");
			CommandUtils.logUpdateInfo(
					CommandUtils.checkScripts(),
					PlayerChat.getChatColour("warning"),
					"/sku update %hash%",
					"%title% is out date, click to update it",
					true
			);
		} else if(args[1].equalsIgnoreCase("plugins")) {
			PlayerChat.info(out_of_date);
			PlayerChat.info("Plugins:");
			CommandUtils.logUpdateInfo(
					CommandUtils.checkPlugins(),
					PlayerChat.getChatColour("warning"),
					"/sku update %hash%",
					"%title% is out date, click to update it",
					true
			);
		} else {
			PlayerChat.warning("Invalid option. Available options:");
			PlayerChat.info("all - scripts - plugins");
			PlayerChat.info("Not providing a first argument defaults to all");
		}
	}

}
