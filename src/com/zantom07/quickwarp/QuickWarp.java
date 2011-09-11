package com.zantom07.quickwarp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.blockface.bukkitstats.CallHome;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class QuickWarp extends JavaPlugin {
	public Server server;
	public static Configuration config;
	public static PermissionHandler permissionHandler;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final HashMap<Player, Location> returnUsers = new HashMap<Player, Location>();
	private DeathListener playerListener;

	@Override
	public void onEnable() {
		playerListener = new DeathListener(this);
		CallHome.load(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() + "] version ["
				+ pdfFile.getVersion() + "] is enabled!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
				Event.Priority.Normal, this);
		setupPermissions();
		
		String file = getDataFolder().toString() + "/config.yml";
		File yml = new File(file);
		if (!yml.exists()) {
			new File(getDataFolder().toString()).mkdir();
			try {
				yml.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = getConfiguration();
		if (config.getProperty("config.ShowReturnText") == null) {
			config.setProperty("config.ShowReturnAcknowledgedText", true);
			config.setProperty("config.ShowNewPositionText", true);
			config.setProperty("config.ShowReturnText", true);
			config.setProperty("config.ShowDeathReturnText", true);
			config.setProperty("config.ShowWorldWarpReturnText", true);
			config.setProperty("config.ShowWarpHereTextToWarpee", true);
			config.setProperty("config.ShowWarpHereTextToCommandSender", true);
			config.setProperty("config.ShowWarpToText", true);
			config.save();
			this.logger.info("[QuickWarp] Default configuration created.");
		}
		config.load();

		HashMap<String, Boolean> allItems = new HashMap<String, Boolean>();
		allItems.put("quickwarp.return", true);
		allItems.put("quickwarp.death", true);
		allItems.put("quickwarp.warpto", true);
		allItems.put("quickwarp.warphere", true);
		allItems.put("quickwarp.world", true);
		allItems.put("quickwarp.wwreturn", true);
		allItems.put("quickwarp.warphereall", true);

		Permission perm = new Permission("quickwarp.*",
				"Permission for using all QuickWarp functionality.",
				PermissionDefault.OP, allItems);
		Bukkit.getServer().getPluginManager().addPermission(perm);
	}

	public boolean enabled(Player player) {
		return this.returnUsers.containsKey(player);
	}

	public void endReturn(Player player) {
		if (enabled(player)) {
			Location returnto = this.returnUsers.get(player);
			player.teleport(returnto);
			this.returnUsers.remove(player);
			if ((Boolean) QuickWarp.config
					.getProperty("config.ShowReturnAcknowledgedText")) {
				player.sendMessage(ChatColor.BLUE
						+ "Returned to previous position.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "Nowhere to return to!");
		}
	}

	public void startReturn(Player player) {
		if (enabled(player)) {
			if ((Boolean) QuickWarp.config
					.getProperty("config.ShowNewPositionText")) {
				player.sendMessage(ChatColor.AQUA
						+ "New position given to /return.");
			}
			this.returnUsers.put(player, player.getLocation());
		} else {
			this.returnUsers.put(player, player.getLocation());
		}
	}

	private void setupPermissions() {
		if (permissionHandler != null) {
			return;
		}

		Plugin permissionsPlugin = this.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (permissionsPlugin == null) {
			this.logger
					.info("[QuickWarp] Permissions 3 not detected, defaulting to SuperPerms / OP.");
			return;
		}

		permissionHandler = ((Permissions) permissionsPlugin).getHandler();
		this.logger.info("[QuickWarp] Found and will use "
				+ ((Permissions) permissionsPlugin).getDescription()
						.getFullName());
	}

	public boolean checkPermission(Player player, String node) {
		if (permissionHandler != null) {
			return permissionHandler.has(player, node);
		} else {
			return player.hasPermission(node);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			this.logger.info("[QuickWarp] Console cannot use warp commands!");
			return true;
		}

		String comm = cmd.getName();

		if (comm.equalsIgnoreCase("warpto")) {
			Player player = (Player) sender;
			if (checkPermission(player, "quickwarp.warpto")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED
							+ "You must specify a player!");
					return true;
				}

				Player warpto = getServer().getPlayer(args[0]);

				List<Player> players = getServer().matchPlayer(args[0]);

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
					if (checkPermission(player, "quickwarp.return")) {
						startReturn(player);
					}
					player.teleport(loc);
					if ((Boolean) QuickWarp.config
							.getProperty("config.ShowWarpToText")) {
						player.sendMessage(ChatColor.BLUE
								+ "You have been teleported to "
								+ ChatColor.GREEN + "" + warpto.getName() + ""
								+ ChatColor.BLUE + ".");
					}
					if (checkPermission(player, "quickwarp.return")) {
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
		} else if (comm.equalsIgnoreCase("warphere")) {
			Player player = (Player) sender;
			if (checkPermission(player, "quickwarp.warphere")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED
							+ "You must specify a player!");
					return true;
				}

				Player warphere = getServer().getPlayer(args[0]);

				List<Player> players = getServer().matchPlayer(args[0]);

				if (players.size() == 0) {
					player.sendMessage(ChatColor.GREEN + "" + args[0] + ""
							+ ChatColor.RED + " is not online!");
					return true;
				} else if (players.size() != 1) {
					player.sendMessage(ChatColor.RED
							+ "Too many matches! You must be more specific.");
					return true;
				} else {

					Location loc = player.getLocation();
					if (checkPermission(player, "quickwarp.return")) {
						startReturn(warphere);
					}
					warphere.teleport(loc);
					if ((Boolean) QuickWarp.config
							.getProperty("config.ShowWarpHereTextToCommandSender")) {
						player.sendMessage(ChatColor.GREEN + ""
								+ warphere.getName() + "" + ChatColor.BLUE
								+ " has been teleported to you.");
					}
					if ((Boolean) QuickWarp.config
							.getProperty("config.ShowWarpHereTextToWarpee")) {
						warphere.sendMessage(ChatColor.BLUE
								+ "You have been teleported to "
								+ ChatColor.GREEN + "" + player.getName() + ""
								+ ChatColor.BLUE + ".");
					}
					if (checkPermission(player, "quickwarp.return")) {
						if ((Boolean) QuickWarp.config
								.getProperty("config.ShowReturnText")) {
							warphere.sendMessage(ChatColor.BLUE
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
		} else if (comm.equalsIgnoreCase("return")) {
			Player player = (Player) sender;
			if (checkPermission(player, "quickwarp.return")) {
				endReturn(player);
			} else if (checkPermission(player, "quickwarp.death")) {
				endReturn(player);
			} else {
				player.sendMessage(ChatColor.RED
						+ "You don't have permission to do that!");
			}

			return true;

		} else if (comm.equalsIgnoreCase("worldwarp")) {
			Player player = (Player) sender;
			if (args.length == 0) {
				player.sendMessage(ChatColor.RED + "You must specify a world!");
				return true;
			}
			if (checkPermission(player, "quickwarp.world")) {
				List<World> worlds = getServer().getWorlds();
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
						if (checkPermission(player, "quickwarp.wwreturn")) {
							startReturn(player);
						}
						Block loc2 = world.getHighestBlockAt(loc);
						Location loc3 = loc2.getLocation();
						player.teleport(loc3);
						if (checkPermission(player, "quickwarp.wwreturn")) {
							if ((Boolean) QuickWarp.config
									.getProperty("config.ShowWorldWarpReturnText")) {
								player.sendMessage(ChatColor.BLUE
										+ "Type /return or /r to return to your previous location.");
							}
						}
						return true;
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
				Location loc = world.getSpawnLocation();
				if (checkPermission(player, "quickwarp.wwreturn")) {
					startReturn(player);
				}
				Block loc2 = world.getHighestBlockAt(loc);
				Location loc3 = loc2.getLocation();
				player.teleport(loc3);
				if (checkPermission(player, "quickwarp.wwreturn")) {
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

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() + "] version ["
				+ pdfFile.getVersion() + "] has been disabled!");
	}
}
