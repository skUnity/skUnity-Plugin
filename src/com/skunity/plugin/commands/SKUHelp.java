package com.skunity.plugin.commands;

import org.bukkit.command.CommandSender;

import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUHelp extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUHelp() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "help";
		info.permission = null;
		info.aliases = new String[]{""};
		info.description = "A simple list of commands with descriptions";
		info.usage = "/sku help";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		// command - description
		PlayerChat.info("You're using the official skUnity plugin (" + Main.version + ")!");
		PlayerChat.sendBlank();
		for(SKUSubCommandInfo info : Main.getCommandRegistry().getInfo().values()) {
			PlayerChat.warning(" " + info.usage);
			PlayerChat.info("  » " + info.description);
			PlayerChat.sendBlank();
		}
		
	}

}
