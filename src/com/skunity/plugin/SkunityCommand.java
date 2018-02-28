package com.skunity.plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import com.skunity.plugin.utils.CommandUtils;
import com.skunity.plugin.utils.PlayerChat;

public class SkunityCommand extends BukkitCommand {
	public PlayerChat PlayerChat;
	public CommandUtils CommandUtils;
	
    public SkunityCommand(String name) {
        super(name);
        this.description = "Main skUnity command";
        this.usageMessage = "/sku help";
        this.setPermission("skunity.commands.main");
        this.setAliases(new ArrayList<>(Arrays.asList("skunity")));
    }
    
    public PlayerChat getPlayerChat() {
    	return PlayerChat;
    }
    
    public CommandUtils getCommandUtils() {
    	return CommandUtils;
    }

	@Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
		PlayerChat = new PlayerChat(sender);   
		CommandUtils = new CommandUtils(PlayerChat);
    	PlayerChat.sendHeader();
    	if (!sender.hasPermission(this.getPermission()) && !(!Config.OP_OVERRIDE && !sender.isOp())) {
            PlayerChat.severe("You don't have permission to execute this command");
            return true;
        }
    	if(args.length == 0) {
    		Main.getCommandRegistry().execute("help", this, sender, alias, args);
    	} else if(args.length >= 1 && Main.getCommandRegistry().subCommandExists(args[0])) {
    		Main.getCommandRegistry().execute(args[0], this, sender, alias, args);
    	} else {
    		PlayerChat.warning("That isn't a valid sub-command");
    	}
    	PlayerChat.sendBlank();
        return true;
    }
    
}