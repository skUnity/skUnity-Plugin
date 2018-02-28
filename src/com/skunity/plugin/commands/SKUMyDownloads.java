package com.skunity.plugin.commands;

import org.bukkit.command.CommandSender;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.files.SKUFile;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUMyDownloads extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUMyDownloads() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "mydownloads";
		info.permission = "skunity.command.mydownloads";
		info.aliases = new String[]{"downloads"};
		info.description = "View the API key holders last 10 downloads and quickly access them resources again";
		info.usage = "/sku <mydownloads|downloads>";
		Main.getCommandRegistry().setInfo(this, info);
	}

	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		PlayerChat.info("Here's the latest content you've downloaded from skUnity");
		CommandUtils.logUpdateInfo(
				runCheck(),
				PlayerChat.getChatColour("warning"),
				"/sku get %resource_id%",
				"Click to download %title%",
				false
		);
	}

	private static JsonArray runCheck() {
		String json = CoreUtil.doAPIRequest("getLastDownloads", "");
		JsonArray jsonData = CommandUtils.parseJSON(json);
		for(JsonElement jE : jsonData) {
			JsonObject jO = jE.getAsJsonObject();
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				SKUFile script = CoreUtil.getFile(jO.get("hash").getAsString());
				if(script == null) {
					continue;
				}
				script.setJSONData(jO);
				CoreUtil.cacheFile(script.getHash(), script);
			}
		}
		return jsonData;
	}

}
