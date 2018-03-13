package com.skunity.plugin.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.CommandSender;
import com.google.common.io.Files;
import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.files.SKUFile;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.HttpDownloadUtility;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUUpdate extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUUpdate() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "update";
		info.permission = "skunity.command.update";
		info.aliases = new String[]{""};
		info.description = "Update a plugin or script either by file name or hash. Providing \"confirm\" will force the update";
		info.usage = "/sku update <file|hash> [confirm]";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		// /sku update <hash (no confirm)|file name (confirm) [confirm]
		//        0                      1                        2
		// length 1                      2                        3
		if(args.length >= 2) {
			SKUFile foundFile = CoreUtil.resolveFile(args[1]);
			if(foundFile == null) {
				PlayerChat.warning("Unable to find any script or addon with the name/hash of: " + args[1]);
				return;
			} else if(args.length >= 3) {
				if(args[2].equalsIgnoreCase("confirm")) {
					updateFile(foundFile);
					return;
				}
			}
			PlayerChat.sendJSONCommandHover(
					"Successfully found a file that matches your input! Hover over for more info and click to confirm the update",
					PlayerChat.getChatColour("warning"),
					"/sku update " + foundFile.getHash() + " confirm",
					"Selected file to be updated: " + foundFile.getFile().getPath(),
					PlayerChat.getChatColour("warning"));
		} else {
			PlayerChat.warning("You must provide a file name or hash to update");
		}
	}
	
	public static void updateFile(SKUFile file) {
		try {
			File downloadHere = file.getFile();
			if(downloadHere.getName().equalsIgnoreCase("skUnity.jar")) {
				PlayerChat.warning("Unable to update the skUnity Plugin. Please download from https://skunity.com/plugin");
				return;
			}
			//Files.move(downloadHere, new File(Main.backupsFolder.getPath() + "/" + file.getFile().getName()));
			File newFile = null;
			if(file.getPath().endsWith(".sk")) {
				newFile = HttpDownloadUtility.downloadFile(file.getDownloadURL(), "./plugins/Skript/scripts/");
			} else {
				newFile = HttpDownloadUtility.downloadFile(file.getDownloadURL(), "./plugins/");
			}
			
			if(newFile.exists()) {
				PlayerChat.success("Successfully updated that file!");
			} else {
				Files.move(new File(Main.backupsFolder.getPath() + "/" + file.getFile().getName()), downloadHere);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
