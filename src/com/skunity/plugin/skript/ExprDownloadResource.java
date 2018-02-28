package com.skunity.plugin.skript;

import java.io.File;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

import com.skunity.plugin.Config;
import com.skunity.plugin.Logger;
import com.skunity.plugin.files.SKUResourceData;
import com.skunity.plugin.utils.CoreUtil;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprDownloadResource extends SimpleExpression<Boolean>{

	private Expression<Integer> integerID;
	private Expression<Integer> version;
	private Boolean useLocalVersion = false;
	
	static {
		Skript.registerExpression(ExprDownloadResource.class, Boolean.class, ExpressionType.SIMPLE, new String[]{
			"[(skunity|sku)] (download|get) resource with [id] %integer% [[at] version %integer%]"
		});
	}


	@Override
	protected Boolean[] get(Event event) {
		String resourceid = String.valueOf(integerID.getSingle(event));
		SKUResourceData json = CoreUtil.getResourceData(resourceid);
		if(json == null) {
			Logger.skriptError("Attempted to get resource data for " + resourceid + ", but it doesn't exist");
			return new Boolean[]{false};
		}
		String downloadURL;
		if(useLocalVersion) {
			downloadURL = String.format("https://forums.skunity.com/resources/%s/download?version=%s",
					json.resource_id,
					version.getSingle(event));
		} else {
			downloadURL = String.format("https://forums.skunity.com/resources/%s/download?version=%s",
					json.resource_id,
					json.latest_version);
		}
		
		String path = "";
		if(json.filename.endsWith(".sk")) {
			path = "./plugins/Skript/scripts/";
		} else {
			path = "./plugins/";
		}
		File downloadFile = CoreUtil.justReturndownloadFile(downloadURL,
				path
		);
		Boolean gotIt = true;
		if(downloadFile == null) {
			gotIt = false;
		}
		if(gotIt) {
			if(json.filename.endsWith(".sk")) {
				if(Config.AUTO_RUN_SCRIPTS) {
					Logger.info("Loading up " + json.filename);
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "sk reload " + json.filename);
				} else {
					Logger.info("The auto-run-scripts setting in the config is disabled, you'll need to reload " + json.filename + " yourself");
				}
			} else {
				if(Config.AUTO_RUN_PLUGINS) {
					Boolean alsR = CoreUtil.allowLateSkriptRegisteration(true);
					if(alsR) {
						try {
							Plugin p = Bukkit.getPluginManager().loadPlugin(downloadFile);
							if(p.isEnabled()) {
								Logger.info("Successfully enabled " + json.filename);
							} else {
								Logger.warning("It appears that " + json.filename + " failed to enable! You'll need to restart/reload your server for it to work");
							}
						} catch (UnknownDependencyException
								| InvalidPluginException
								| InvalidDescriptionException e) {
							Logger.warning("Something went wrong while trying to load " + json.filename + ". You may need to reload or restart your server for it to work. If that doesn't work you'll need to re-install the plugin");
							e.printStackTrace();
						}
					} else {
						Logger.warning("Something went wrong while trying to get Skript to allow late registerations (loading an addon after Skript is loaded), when loading " + json.filename + ". You may need to reload or restart your server for it to work. If that doesn't work you'll need to re-install the plugin");
					}
				} else {
					Logger.info("The auto-run-plugins setting in the config is disabled, you'll need to reload/restart your server for " + json.filename + " to run");
				}
			}
		}
		return new Boolean[]{gotIt};
	}	
	
	@Override
	public String toString(@Nullable Event event, boolean b) {
		return new String[]{
				"[(skunity|sku)] (download|get) resource with [id] %string%"
			}.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
		integerID = (Expression<Integer>) exprs[0];
		if(Integer.valueOf(exprs[1].toString()) > 1) {
			version = (Expression<Integer>) exprs[1];
			useLocalVersion = true;
		}
		return true;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}
}