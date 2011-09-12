package com.zantom07.quickwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zantom07.quickwarp.QuickWarp;
import com.zantom07.quickwarp.features.Feature_Return;

public class Command_Return implements CommandExecutor {
	private QuickWarp plugin;
	private Feature_Return returnHandler;
	
	public Command_Return(QuickWarp plugin) {
		this.plugin = plugin;
		this.returnHandler = new Feature_Return(plugin);
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.logger.info("[QuickWarp] Console cannot use warp commands!");
			return true;
		}

		String comm = cmd.getName();

		if (comm.equalsIgnoreCase("return")) {
			Player player = (Player) sender;
			if (plugin.checkPermission(player, "quickwarp.return")) {
				returnHandler.endReturn(player);
			} else if (plugin.checkPermission(player, "quickwarp.death")) {
				returnHandler.endReturn(player);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
			}

			return true;

		}
		return false;
	}
}
