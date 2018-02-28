package com.skunity.plugin.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import com.skunity.plugin.Logger;
import com.skunity.plugin.SkunityCommand;

public class CommandRegistry {

	private Field bukkitCommandMap;
	private CommandMap commandMap;
	private HashMap<String, SKUSubCommand> subCommands = new HashMap<String, SKUSubCommand>();
	private HashMap<SKUSubCommand, SKUSubCommandInfo> subCommandsInfo = new HashMap<SKUSubCommand, SKUSubCommandInfo>();
	public CommandRegistry() {
		try {
			   bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

			   bukkitCommandMap.setAccessible(true);
			   commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());			   
			} catch(Exception e) {
			   e.printStackTrace();
			}
	}
	
	public void register(String name, Command commandClass) {
		commandMap.register(name, commandClass);
	}
	
	public void registerSubCommand(SKUSubCommand commandClass) {
		SKUSubCommandInfo info = subCommandsInfo.get(commandClass);
		Logger.debug("A command has been registered: " + info.command);
		subCommands.put(info.command, commandClass);
		for(String a : info.aliases) {
			if(a != "" || a != " ") { 
				subCommands.put(a, commandClass);
			}
		}
	}
		
	public Boolean subCommandExists(String command) {
		return subCommands.containsKey(command);
	}
	
	public HashMap<String, SKUSubCommand> getSubCommands() {
		return subCommands;
	}
	
	public void setInfo(SKUSubCommand sub, SKUSubCommandInfo info) {
		subCommandsInfo.put(sub, info);
	}
	
	public HashMap<SKUSubCommand, SKUSubCommandInfo> getInfo() {
		return subCommandsInfo;
	}
	
	public void execute(String command, SkunityCommand skUnity, CommandSender sender, String alias, String[] args) {
		if(subCommandExists(command)) {
			SKUSubCommand cmd = subCommands.get(command);
			SKUSubCommandInfo cmdInfo = subCommandsInfo.get(cmd);
			if(cmdInfo.permission == null || sender.hasPermission(cmdInfo.permission)) {
				subCommands.get(command).execute(skUnity, sender, alias, args);;
			} else {
				new PlayerChat(sender).severe("You do not have permission to access that sub-command");
			}
		} else {
			Logger.severe("There was an attempt to run the command " + command + ", but it doesn't exist!");
		}
	}

}
