package com.skunity.plugin.utils;

public class SKUSubCommandInfo {

	public String command = null, description = null, permission = null, usage = null;
	public String[] aliases = null;
	public SKUSubCommandInfo() {
		
	}
	
	public SKUSubCommandInfo(String command, String description, String permission, String[] aliases, String usage) {
		this.command = command;
		this.description = description;
		this.permission = permission;
		this.aliases = aliases;
		this.usage = usage;
	}
	
	public Boolean isComplete() {
		if(command == null) {
			return false;
		}
		if(description == null) {
			return false;
		}
		if(permission == null) {
			return false;
		}
		if(aliases == null) {
			return false;
		}
		if(usage == null) {
			return false;
		}
		return true;
	}

}
