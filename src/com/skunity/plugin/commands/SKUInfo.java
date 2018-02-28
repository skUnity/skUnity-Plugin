package com.skunity.plugin.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;

import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.files.SKUFile;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.ListMessage;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUInfo extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUInfo() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "info";
		info.permission = "skunity.command.info";
		info.aliases = new String[]{""};
		info.description = "Get info on a specific script or plugin. List all your content by doing /sku info";
		info.usage = "/sku info [scripts|plugins|{all}]";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public static void listMessageHandler(Integer soFar, String msg) {
		TextComponent message = new TextComponent(TextComponent.fromLegacyText(msg));
		message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "sku info" ) );
		
		ComponentBuilder hoverComponent = new ComponentBuilder("Click for more information");
		hoverComponent.color(PlayerChat.getChatColour("warning"));
		message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverComponent.create() ) );
		
		PlayerChat.getPlayer().spigot().sendMessage(message);
		ListMessage.sendButtons(PlayerChat, soFar, 1);
		
	}
	
	@Override
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		// /sku info <hash|file name|all>
		//        0        1
		// length 1        2
		// all arg returns a list of scripts and jars
		// scripts/skripts arg returns a list of scripts
		// jars/plugins arg returns a list of plugins
		String out_of_date = "Here's all of your content";
		if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("all"))) {
			PlayerChat.info(out_of_date);
			PlayerChat.info("Scripts:");
			CommandUtils.logUpdateInfo(
					CommandUtils.checkScripts(),
					PlayerChat.getChatColour("warning"),
					"/sku info %hash%",
					"Click for more information about %title%",
					false
			);
	    	PlayerChat.info("Plugins:");
	    	CommandUtils.logUpdateInfo(
	    			CommandUtils.checkPlugins(),
					PlayerChat.getChatColour("warning"),
					"/sku info %hash%",
					"Click for more information about %title%",
					false
			);
	    	return;
		} else if(args[1].equalsIgnoreCase("scripts")) {
			PlayerChat.info(out_of_date);
			PlayerChat.info("Scripts:");
			CommandUtils.logUpdateInfo(
					CommandUtils.checkScripts(),
					PlayerChat.getChatColour("warning"),
					"/sku info %hash%",
					"Click for more information about %title%",
					false
			);
			return;
		} else if(args[1].equalsIgnoreCase("plugins")) {
			PlayerChat.info(out_of_date);
			PlayerChat.info("Plugins:");
			CommandUtils.logUpdateInfo(
					CommandUtils.checkPlugins(),
					PlayerChat.getChatColour("warning"),
					"/sku info %hash%",
					"Click for more information about %title%",
					false
			);
			return;
		}
		if(args.length >= 2) {
			SKUFile foundFile = CoreUtil.resolveFile(args[1]);
			if(foundFile == null) {
				PlayerChat.warning("Unable to find any script or addon with the name/hash of: " + args[1]);
			} else if(args.length >= 2) {
				// Name: <script whatever>
				PlayerChat.sendColonSplit("Name", foundFile.getFile().getName());

				// Path: <short path>
				//		^ hover for full
				PlayerChat.sendJSONHoverColon("Path", foundFile.getFile().getPath(), foundFile.getFile().getAbsolutePath());
				
				if(foundFile.isJSONReady()) {
					// on skUnity: true/false
					// 		[ ONLY IF ON SKUNITY IS TRUE ]
					PlayerChat.sendColonSplit("On skUnity", CoreUtil.booleanToString(foundFile.onSkUnity()));
					
					// Author: <author name>
					//		^ hover for author url click
					PlayerChat.sendJSONHoverURLColon("Author", foundFile.getResourceData().author, "View the authors skUnity Resources profile", foundFile.getURL("author"));
					
					// Resource Title: <resource title>
					//		^ hover for resource url click
					PlayerChat.sendJSONHoverURLColon("Resource Title", foundFile.getResourceData().title, "View this Resources page on skUnity", foundFile.getURL("page"));
					
					if(foundFile.getResourceData().is_latest) {
						PlayerChat.sendJSONHoverColon("Is Latest", CoreUtil.booleanToString(foundFile.getResourceData().is_latest), "You're running the latest version (" + foundFile.getResourceData().version_string + ")");
					} else {
						PlayerChat.sendJSONHoverCommandColon("Is Latest", CoreUtil.booleanToString(foundFile.getResourceData().is_latest), "Click here to update", "/sku update " + foundFile.getHash());
					}
				}
			}
		} else {
			PlayerChat.warning("You must provide a file name or hash to see the information about it");
		}
	}
}
