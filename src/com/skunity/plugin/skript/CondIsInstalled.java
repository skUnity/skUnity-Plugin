package com.skunity.plugin.skript;

import javax.annotation.Nullable;

import org.bukkit.event.Event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skunity.plugin.Logger;
import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.PlayerChat;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

public class CondIsInstalled extends Condition {

	private Expression<Integer> integerID;
	private Expression<Integer> version;
	int matchType = 0;
	private String stringVersion = null;
	private String stringID = null;
	private Boolean checkVersion = false;
	
	static {
		Skript.registerCondition(CondIsInstalled.class, new String[]{
			"[(skunity|sku)] resource %integer% (1¦is|2¦does|3¦is not|4¦isn't|5¦does not|6¦doesn't) (installed|exist) [[at] version %integer%]",
			"[(skunity|sku)] script %integer% (1¦is|2¦does|3¦is not|4¦isn't|5¦does not|6¦doesn't) (installed|exist) [[at] version %integer%]",
			"[(skunity|sku)] plugin %integer% (1¦is|2¦does|3¦is not|4¦isn't|5¦does not|6¦doesn't) (installed|exist) [[at] version %integer%]",
		});
	}

	@Override
	public boolean check(final Event event) {
		stringID = String.valueOf(integerID.getSingle(event));
		if(checkVersion) {
			stringVersion = String.valueOf(version.getSingle(event));
		}
		boolean s = false;
		if(matchType == 0 || matchType == 1) {
			// scripts
			s = doCheck("./plugins/Skript/scripts/", true);
		}
		if(matchType == 0 || matchType == 2) {
			// plugin
			s = doCheck("./plugins/", false);
		}
		if(isNegated()) { 
			return !s; 
		} else { 
			return s;
		}
	}
	
	private boolean doCheck(String path, Boolean recursive) {
		CommandUtils CommandUtils = new CommandUtils((PlayerChat) null);
		JsonArray scripts = CommandUtils.runCheck(path, recursive);
		for(JsonElement jE : scripts) {
			JsonObject jO = jE.getAsJsonObject();
			if(jO.get("result").getAsString().equalsIgnoreCase("success")) {
				if(jO.get("resource_id").getAsString().equalsIgnoreCase(stringID)) {
					if(checkVersion) {
						if(jO.get("this_version").getAsString().equalsIgnoreCase(stringVersion)) {
							return true;
						} else {
							return false;
						}
					} else {
						Logger.info("Returned true");
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString(@Nullable Event event, boolean b) {
		return new String[]{
				"[(skunity|sku)] resource %integer% (1¦is|2¦does|3¦is not|4¦isn't|5¦does not|6¦doesn't) (installed|exist) [[at] version %integer%]",
				"[(skunity|sku)] script %integer% (1¦is|2¦does|3¦is not|4¦isn't|5¦does not|6¦doesn't) (installed|exist) [[at] version %integer%]",
				"[(skunity|sku)] plugin %integer% (1¦is|2¦does|3¦is not|4¦isn't|5¦does not|6¦doesn't) (installed|exist) [[at] version %integer%]",
			}.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parser) {
		integerID = (Expression<Integer>) exprs[0];
		if(Integer.valueOf(exprs[1].toString()) > 1) {
			version = (Expression<Integer>) exprs[1];
			checkVersion = true;
		}
		matchType = matchedPattern;
		if(parser.mark == 1 || parser.mark == 2) {
			setNegated(false);
		} else {
			setNegated(true);
		}
		return true;
	}
}