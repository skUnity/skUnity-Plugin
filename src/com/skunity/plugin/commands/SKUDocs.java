package com.skunity.plugin.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.bukkit.command.CommandSender;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUDocs extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUDocs() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "docs";
		info.permission = "skunity.command.docs";
		info.aliases = new String[]{"d"};
		info.description = "Access the skUnity Docs directly in-game (can only search them)";
		info.usage = "/sku docs search <search query>";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		// sku docs search
		//      0     1
		if(args.length > 2) {
			if(args[1].equalsIgnoreCase("search")) {			
				args[0] = "";
				args[1] = "";
				String argString = String.join(" ", Arrays.asList(args));
				argString = argString.substring(2, argString.length());
				try {
					argString = URLEncoder.encode(argString, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					return;
				}
				String data = CoreUtil.doDocsAPIRequest("doSearch", "&query=" + argString);
				
				JsonObject response = CommandUtils.getJsonObject(data);
				JsonObject result = response.getAsJsonObject("result");
				JsonObject info = result.getAsJsonObject("info");
				JsonArray records = result.getAsJsonArray("records");
				if(records.size() == 0) {
					PlayerChat.info("Sorry, but no results came back from that search");
				} else if(records.size() == 1) {
					JsonObject element = records.get(0).getAsJsonObject();
					PlayerChat.sendColonSplit("Name", element.get("name").getAsString() + " (" + element.get("id").getAsString() + ")");
					PlayerChat.sendColonSplit("Type", element.get("doc").getAsString());
					PlayerChat.sendColonSplit("Description", element.get("desc").getAsString().replace("\r", ""));
					PlayerChat.sendColonSplit("Addon", element.get("addon").getAsString() + " (" + element.get("version").getAsString() + ")");
					PlayerChat.sendColonSplit("Pattern", element.get("pattern").getAsString().replace("\r", ""));
					PlayerChat.sendColonSplit("Plugin Required", element.get("plugin").getAsString(), false);
					PlayerChat.sendColonSplit("Examples", "Sorry! Example's currently aren't viewable");
					if(element.get("info").getAsJsonObject().get("status").getAsString().equalsIgnoreCase("exists")) {
						JsonObject element_info = element.get("info").getAsJsonObject();
						String message = element_info.get("message").getAsString();
						message = message.replace("[url=\'", "").replace("\']", " - ").replace("[/url]", "");
						if(element_info.get("type").getAsString().equalsIgnoreCase("warning")) {
							PlayerChat.warning(message);
						} else if(element_info.get("type").getAsString().equalsIgnoreCase("success")) {
							PlayerChat.success(message);
						} else {
							PlayerChat.info(message);
						}
					}
				} else {
					PlayerChat.info("Found a total of " + info.get("returned").getAsInt() + " out of " + info.get("totalRecords").getAsInt() + " records");
					CommandUtils.logUpdateInfo(
							records,
							PlayerChat.getChatColour("warning"),
							"/sku docs search id:%id%",
							"Click to view all infomration about %name-32%",
							false,
							"[%addon%] %name-32%"
					);
					/*PlayerChat.info("Total records: " + CommandUtils.jsonArrayGet(info, "returned"));*/
				}
			} else {
				PlayerChat.warning("Oops! Unknown docs command");
			}
		} else {
			PlayerChat.warning("Oops! Looks like you're missing a few arguments, double check the command and try again");
		}
	}
}
