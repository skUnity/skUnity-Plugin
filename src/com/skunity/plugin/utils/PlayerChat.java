package com.skunity.plugin.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.skunity.plugin.Logger;

public class PlayerChat {

	private Player p;
	private CommandSender s = null;
	private PlayerChatMode playerChatMode;
	public PlayerChat(Player player) {
		p = player;
		playerChatMode = PlayerChatMode.PLAYER;
	}
	
	public PlayerChat(CommandSender sender) {
		s = sender;
		if(sender instanceof Player) {
			setPlayer((Player) sender);
			playerChatMode = PlayerChatMode.BOTH;
		} else if(sender instanceof ConsoleCommandSender){
			playerChatMode = PlayerChatMode.LOGGER;
		} else {
			playerChatMode = PlayerChatMode.COMMAND_SENDER;
		}
	}
	
	public PlayerChat(Class<?> clazz) {
		if(clazz.equals(Logger.class)) {
			playerChatMode = PlayerChatMode.LOGGER;
		}
	}
	
	public void setPlayer(Player player) {
		p = player;
		if(s == null) {
			playerChatMode = PlayerChatMode.PLAYER;
		} else {
			playerChatMode = PlayerChatMode.BOTH;
		}
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public CommandSender getCommandSender() {
		return s;
	}
	
	public void sendHeader() {
		sendMessage("&4&m----------&r &fsk&c&lUnity &4&m----------------------------");
		sendMessage(" ");
	}
	
	public void sendBlank() {
		sendMessage(" ");
	}
	
	public void sendMessage(String... messageArray) {
		for(String message : messageArray) {
			if(playerChatMode == PlayerChatMode.PLAYER || playerChatMode == PlayerChatMode.BOTH) {
				p.sendMessage(format(message));
			} else if(playerChatMode == PlayerChatMode.COMMAND_SENDER) {
				s.sendMessage(format(message));
			} else if(playerChatMode == PlayerChatMode.LOGGER) {
				Logger.info(format(message));
			}
		}
	}
	
	public void title(String message) {
		sendMessage("&6&l" + message);
	}
	
	public void info(String message) {
		sendMessage("&7" + message);
	}
	
	public void warning(String message) {
		sendMessage("&c" + message);
	}
	
	public void severe(String message) {
		sendMessage("&4" + message);
	}
	
	public void success(String message) {
		sendMessage("&a" + message);
	}
		
	public String format(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public ChatColor getChatColour(String type) {
		if(type == "warning") {
			return ChatColor.RED;
		} else if(type == "severe") {
			return ChatColor.DARK_RED;
		} else if(type == "success") {
			return ChatColor.GREEN;
		} else if(type == "info") {
			return ChatColor.GRAY;
		}
		return ChatColor.GRAY;
	}
	
	public String getChatColourString(String type) {
		if(type == "warning") {
			return "&c";
		} else if(type == "severe") {
			return "&4";
		} else if(type == "success") {
			return "&a";
		} else if(type == "info") {
			return "&7";
		}
		return "&7";
	}
	
	public enum PlayerChatMode {
		PLAYER, COMMAND_SENDER, BOTH, LOGGER
	}
	
	public void sendJSONCommandHover(String playerMessage, ChatColor colour,
			String command,
			String hoverText, ChatColor hoverColour) {
		if(playerChatMode == PlayerChatMode.LOGGER) {
			sendMessage(new String[]{playerMessage,
					"[Command: " + command + "]",
					"[Hover: " + hoverText + "]"
			});
			return;		
		}
		TextComponent message = new TextComponent(playerMessage);
		message.setColor(colour);
		message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, command ) );
		
		ComponentBuilder hoverComponent = new ComponentBuilder(hoverText);
		hoverComponent.color(hoverColour);
		message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverComponent.create() ) );
		
		getPlayer().spigot().sendMessage(message);
	}
	
	public void sendJSONCommand(String playerMessage, ChatColor colour,
			String command) {
		if(playerChatMode == PlayerChatMode.LOGGER) {
			sendMessage(new String[]{playerMessage,
					"[Command: " + command + "]"
			});
			return;		
		}
		TextComponent message = new TextComponent(playerMessage);
		message.setColor(colour);
		message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, command ) );
				
		getPlayer().spigot().sendMessage(message);
	}
	
	public void sendJSONHover(String playerMessage, ChatColor colour,
			String hoverText, ChatColor hoverColour) {
		if(playerChatMode == PlayerChatMode.LOGGER) {
			sendMessage(new String[]{playerMessage,
					"[Hover: " + hoverText + "]"
			});
			return;		
		}
		TextComponent message = new TextComponent(playerMessage);
		message.setColor(colour);
		
		ComponentBuilder hoverComponent = new ComponentBuilder(hoverText);
		hoverComponent.color(hoverColour);
		message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverComponent.create() ) );
		
		getPlayer().spigot().sendMessage(message);
	}
	
	public void sendColonSplit(String pre, String suf, Boolean allowNoValue) {
		if(suf.equalsIgnoreCase("")) {
			if(allowNoValue) {
				sendMessage(getChatColourString("warning") + pre + ": " + getChatColourString("info") + suf);
			}
		} else {
			sendMessage(getChatColourString("warning") + pre + ": " + getChatColourString("info") + suf);
		}
	}
	
	public void sendColonSplit(String pre, String suf) {
		sendColonSplit(pre, suf, true);
	}
	
	public void sendJSONHoverColon(String pre, String suf, String hoverText) {
		if(playerChatMode == PlayerChatMode.LOGGER) {
			sendMessage(new String[]{pre + ": " + suf,
					"[Hover: " + hoverText + "]"
			});
			return;		
		}
		TextComponent message = new TextComponent(pre + ": ");
		message.setColor(getChatColour("warning"));
		
		TextComponent world = new TextComponent(suf);
		world.setColor(getChatColour("info"));
		ComponentBuilder hoverComponent = new ComponentBuilder(hoverText);
		hoverComponent.color(getChatColour("info"));
		world.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverComponent.create() ) );
		
		message.addExtra(world);
		getPlayer().spigot().sendMessage(message);
		
	}
	
	public void sendJSONHoverURLColon(String pre, String suf, String hoverText, String url) {
		if(playerChatMode == PlayerChatMode.LOGGER) {
			sendMessage(new String[]{pre + ": " + suf,
					"[Hover: " + hoverText + "]",
					"[URL: " + url + "]"
			});
			return;		
		}
		TextComponent message = new TextComponent(pre + ": ");
		message.setColor(getChatColour("warning"));

		TextComponent world = new TextComponent(suf);
		world.setColor(getChatColour("info"));
		world.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, url ) );
		
		ComponentBuilder hoverComponent = new ComponentBuilder(hoverText);
		hoverComponent.color(getChatColour("info"));
		world.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverComponent.create() ) );
		
		message.addExtra(world);
		getPlayer().spigot().sendMessage(message);
	}
	
	public void sendJSONHoverCommandColon(String pre, String suf, String hoverText, String command) {
		if(playerChatMode == PlayerChatMode.LOGGER) {
			sendMessage(new String[]{pre + ": " + suf,
					"[Hover: " + hoverText + "]",
					"[Command: " + command + "]"
			});
			return;		
		}
		TextComponent message = new TextComponent(pre + ": ");
		message.setColor(getChatColour("warning"));
		
		TextComponent world = new TextComponent(suf);
		world.setColor(getChatColour("info"));
		world.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, command ) );
		
		ComponentBuilder hoverComponent = new ComponentBuilder(hoverText);
		hoverComponent.color(getChatColour("info"));
		world.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hoverComponent.create() ) );
		
		message.addExtra(world);
		getPlayer().spigot().sendMessage(message);
		
	}
}
