package com.skunity.plugin.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import com.google.gson.JsonObject;
import com.skunity.plugin.Config;
import com.skunity.plugin.Main;
import com.skunity.plugin.SkunityCommand;
import com.skunity.plugin.files.SKUResourceData;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.CoreUtil;
import com.skunity.plugin.utils.PlayerChat;
import com.skunity.plugin.utils.SKUSubCommand;
import com.skunity.plugin.utils.SKUSubCommandInfo;

public class SKUGet extends SKUSubCommand {
	static PlayerChat PlayerChat;
	static CommandUtils CommandUtils;
	
	public SKUGet() {
		SKUSubCommandInfo info = new SKUSubCommandInfo();
		info.command = "get";
		info.permission = "skunity.command.get";
		info.aliases = new String[]{"g"};
		info.description = "Downloads a Resource directly from skUnity. Add \"confirm\" to force-download";
		info.usage = "/sku get <resource id> [confirm]";
		Main.getCommandRegistry().setInfo(this, info);
	}
	
	public void execute(SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		PlayerChat = skUnity.getPlayerChat();
		CommandUtils = skUnity.getCommandUtils();
		// /sku get <resource id> [confirm]
		//       0        1          2
		// len   1        2          3
		if(args.length >= 2) {
			SKUResourceData json = getData(args[1]);
			if(json == null) {
				//PlayerChat.warning("Something went wrong. Please try again later");
				return;
			}
			
			if(json.result.equalsIgnoreCase("success")) {
				if(args.length > 2 && args[2].equalsIgnoreCase("confirm")) {
					String downloadURL = String.format("https://forums.skunity.com/resources/%s/download?version=%s",
							json.resource_id,
							json.latest_version);
					String path = "";
					if(json.filename.endsWith(".sk")) {
						path = "./plugins/Skript/scripts/";
					} else {
						path = "./plugins/";
					}
					File downloadFile = CoreUtil.returndownloadFile(downloadURL,
							path,
							PlayerChat,
							"Successfully downloaded that Resource",
							"Something went wrong, please try again later"
					);
					Boolean gotIt = true;
					if(downloadFile == null) {
						gotIt = false;
					}
					if(gotIt) {
						if(json.filename.endsWith(".sk")) {
							if(Config.AUTO_RUN_SCRIPTS) {
								PlayerChat.info("Loading up " + json.filename);
								Bukkit.getServer().dispatchCommand(sender, "sk reload " + json.filename);
							} else {
								PlayerChat.info("The auto-run-scripts setting in the config is disabled, you'll need to reload " + json.filename + " yourself");
							}
						} else {
							if(Config.AUTO_RUN_PLUGINS) {
								Boolean alsR = CoreUtil.allowLateSkriptRegisteration(true);
								if(alsR) {
									try {
										Plugin p = Bukkit.getPluginManager().loadPlugin(downloadFile);
										if(p.isEnabled()) {
											PlayerChat.success("Successfully enabled " + json.filename);
										} else {
											PlayerChat.warning("It appears that " + json.filename + " failed to enable! You'll need to restart/reload your server for it to work");
										}
									} catch (UnknownDependencyException
											| InvalidPluginException
											| InvalidDescriptionException e) {
										PlayerChat.warning("Something went wrong while trying to load " + json.filename + ". You may need to reload or restart your server for it to work. If that doesn't work you'll need to re-install the plugin");
										e.printStackTrace();
									}
								} else {
									PlayerChat.warning("Something went wrong while trying to get Skript to allow late registerations (loading an addon after Skript is loaded), when loading " + json.filename + ". You may need to reload or restart your server for it to work. If that doesn't work you'll need to re-install the plugin");
								}
							} else {
								PlayerChat.info("The auto-run-plugins setting in the config is disabled, you'll need to reload/restart your server for " + json.filename + " to run");
							}
						}
					}
				} else {
					Integer resource_id = json.resource_id;
					PlayerChat.sendJSONCommandHover("Is this the right Resource?",
							PlayerChat.getChatColour("info"),
							"/sku get " + resource_id + " confirm",
							"Download this Resource from skUnity",
							PlayerChat.getChatColour("info")
					);
					String resource_title = json.title + " by " + json.author;
					PlayerChat.sendJSONCommandHover(resource_title,
							PlayerChat.getChatColour("info"),
							"/sku get " + resource_id + " confirm",
							"Download this Resource from skUnity",
							PlayerChat.getChatColour("info")
					);
				}
				
				
			} else {
				PlayerChat.warning((String) json.get("result"));
			}
		} else {
			PlayerChat.warning(Main.getCommandRegistry().getInfo().get(this).usage);
		}
	}
	
	
	private SKUResourceData getData(String resourceId) {		
		String json = CoreUtil.doAPIRequest("getResource", "&forcedhttp=200&resourceid=" + resourceId);
		if(json == null) {
			PlayerChat.warning("Someting went wrong... please try again");
			return null;
		}
		JsonObject jsonData = Main.commandUtils.getJsonObject(json);
		if(jsonData == null) {
			PlayerChat.warning("Someting went wrong... please try again");
			return null;
		}
		if(jsonData.get("response").getAsString().equalsIgnoreCase("failed")) {
			PlayerChat.warning(jsonData.get("result").getAsString());
			return null;
		} else {
			return new SKUResourceData(jsonData.get("result").getAsJsonObject());
		}
		/*JsonObject jsonObject = null;
		for(JsonElement jE : jsonData) {
			JsonObject jO = jE.getAsJsonObject();
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				jsonObject = jO;
				break;
			} else {
				PlayerChat.warning("Someting went wrong... please try again");
				return null;
			}
		}
		if(jsonObject == null) {
			PlayerChat.warning("Oopsy, something went wrong. Please try again");
			return null;
		} else {
			return new SKUResourceData(jsonObject);
		}*/
	}
}
