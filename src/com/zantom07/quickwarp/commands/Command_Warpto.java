package com.zantom07.quickwarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zantom07.quickwarp.QuickWarp;
import com.zantom07.quickwarp.features.Feature_Return;

public class Command_Warpto implements CommandExecutor {
	private QuickWarp plugin;
	private Feature_Return returnHandler;
	
	public Command_Warpto(QuickWarp plugin) {
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

		if (comm.equalsIgnoreCase("warpto")) {
			Player player = (Player) sender;
			if (plugin.checkPermission(player, "quickwarp.warpto")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED
							+ "You must specify a player!");
					return true;
				}

				Player warpto = plugin.getServer().getPlayer(args[0]);

				List<Player> players = plugin.getServer().matchPlayer(args[0]);

				if (players.size() == 0) {
					player.sendMessage(ChatColor.GREEN + "" + args[0] + ""
							+ ChatColor.RED + " is not online!");
					return true;
				} else if (players.size() != 1) {
					player.sendMessage(ChatColor.RED
							+ "Too many matches! You must be more specific.");
					return true;
				} else {
					Location loc = warpto.getLocation();
					if (plugin.checkPermission(player, "quickwarp.return")) {
						returnHandler.startReturn(player);
					}
					player.teleport(loc);
					if ((Boolean) QuickWarp.config
							.getProperty("config.ShowWarpToText")) {
						player.sendMessage(ChatColor.BLUE
								+ "You have been teleported to "
								+ ChatColor.GREEN + "" + warpto.getName() + ""
								+ ChatColor.BLUE + ".");
					}
					if (plugin.checkPermission(player, "quickwarp.return")) {
						if ((Boolean) QuickWarp.config
								.getProperty("config.ShowReturnText")) {
							player.sendMessage(ChatColor.BLUE
									+ "Type /return or /r to return to your previous location.");
						}
					}
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
				return true;
			}
		}
		return false;
	}
}
