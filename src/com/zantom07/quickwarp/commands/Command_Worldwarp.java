package com.zantom07.quickwarp.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.zantom07.quickwarp.QuickWarp;
import com.zantom07.quickwarp.features.Feature_Return;

public class Command_Worldwarp implements CommandExecutor {
	private QuickWarp plugin;
	private Feature_Return returnHandler;

	public Command_Worldwarp(QuickWarp plugin) {
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

		if (comm.equalsIgnoreCase("worldwarp")) {
			Player player = (Player) sender;
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "You must specify a world!");
				return true;
			}
			if (plugin.checkPermission(player, "quickwarp.world")) {
				List<World> worlds = plugin.getServer().getWorlds();
				World world = null;
				int i = 0;

				for (World name : worlds) {
					String worldString = name.getName();
					String lowerwString = worldString.toLowerCase();
					String yourworld = args[0].toLowerCase();
					if (lowerwString.startsWith(yourworld)) {
						world = name;
						i++;
					}
					if (lowerwString.equalsIgnoreCase(yourworld)) {
						Location loc = world.getSpawnLocation();
						if (plugin
								.checkPermission(player, "quickwarp.wwreturn")) {
							returnHandler.startReturn(player);
						}

						player.teleport(loc);
						if (plugin
								.checkPermission(player, "quickwarp.wwreturn")) {
							if ((Boolean) QuickWarp.config
									.getProperty("config.ShowWorldWarpReturnText")) {
								player.sendMessage(ChatColor.BLUE
										+ "Type /return or /r to return to your previous location.");
								return true;
							}
						}
					}
				}

				if (i == 0) {
					player.sendMessage(ChatColor.GREEN + "" + args[0] + ""
							+ ChatColor.RED + " does not exist!");
					return true;
				} else if (i > 1) {
					player.sendMessage(ChatColor.GREEN + "" + args[0] + ""
							+ ChatColor.RED + " has too many matches!");
					return true;
				}
				//Location loc = world.getSpawnLocation();
				if (plugin.checkPermission(player, "quickwarp.wwreturn")) {
					returnHandler.startReturn(player);
				}
				//Block loc2 = world.getHighestBlockAt(loc);
				//Location loc3 = loc2.getLocation();
				player.teleport(player.getWorld().getSpawnLocation());
				if (plugin.checkPermission(player, "quickwarp.wwreturn")) {
					if ((Boolean) QuickWarp.config
							.getProperty("config.ShowWorldWarpReturnText")) {
						player.sendMessage(ChatColor.BLUE
								+ "Type /return or /r to return to your previous location.");
					}
				}

			} else {
				player.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
			}

			return true;
		}
		return false;
	}
}
