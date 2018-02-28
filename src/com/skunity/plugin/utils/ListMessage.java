package com.skunity.plugin.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class ListMessage {

	private List<String> messages = new ArrayList<String>();
	private Class<?> subCommand;
	public ListMessage(Class<?> subCommandClass) {
		subCommand = subCommandClass;
	}

	public void addMessage(String message) {
		messages.add(message);
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public void send(Integer start, Integer limit) {
		Integer soFar = -1;
		for(String msg : messages) {
			soFar++;
			if(soFar >= start) {
				try {
					Method execute;
					execute = subCommand.getMethod("listMessageHandler", Integer.class, String.class);
					execute.setAccessible(true);
					execute.invoke(Class.class, soFar, msg);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// &c[ &7BACK &c] - (&71&c/&710&c) - [ &7NEXT &c]
	public static void sendButtons(PlayerChat PlayerChat, Integer soFar, Integer total) {
		PlayerChat.sendBlank();
		
		ComponentBuilder blankHover = new ComponentBuilder("");
		
		ComponentBuilder backPageHover = new ComponentBuilder(" « Go back a page « ");
		backPageHover.color(PlayerChat.getChatColour("warning"));
		
		ComponentBuilder forwardPageHover = new ComponentBuilder(" » Go forward a page » ");
		forwardPageHover.color(PlayerChat.getChatColour("warning"));
		
		BaseComponent[] cb = 
				new ComponentBuilder("          [").color( ChatColor.RED )
				.append( " BACK " ).color( ChatColor.GRAY ).event(new HoverEvent( HoverEvent.Action.SHOW_TEXT, backPageHover.create()))
				.append( "] - (" ).color( ChatColor.RED ).event(new HoverEvent( HoverEvent.Action.SHOW_TEXT, blankHover.create()))
				.append( soFar.toString() ).color( ChatColor.GRAY )
				.append( "/" ).color( ChatColor.RED )
				.append( total.toString() ).color( ChatColor.GRAY )
				.append( ") - [" ).color( ChatColor.RED )
				.append( " NEXT " ).color( ChatColor.GRAY ).event(new HoverEvent( HoverEvent.Action.SHOW_TEXT, forwardPageHover.create()))
				.append( "]" ).color( ChatColor.RED )
				.create();
		PlayerChat.getPlayer().spigot().sendMessage(cb);
		
	}
}
