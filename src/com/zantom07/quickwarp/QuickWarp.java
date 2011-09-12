package com.zantom07.quickwarp;

import java.util.HashMap;
import java.util.logging.Logger;

import org.blockface.bukkitstats.CallHome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
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
import com.zantom07.quickwarp.commands.Command_Return;
import com.zantom07.quickwarp.commands.Command_Warphere;
import com.zantom07.quickwarp.commands.Command_Warpto;
import com.zantom07.quickwarp.commands.Command_Worldwarp;
import com.zantom07.quickwarp.listeners.Listener_Player;

public class QuickWarp extends JavaPlugin {
	public Server server;
	public static Configuration config;
	public static PermissionHandler permissionHandler;
	public final Logger logger = Logger.getLogger("Minecraft");
	private Listener_Player playerListener;
	private Config_Handler configHandler;
	public final HashMap<Player, Location> returnUsers = new HashMap<Player, Location>();

	@Override
	public void onEnable() {
		playerListener = new Listener_Player(this);
		configHandler = new Config_Handler(this);
		CallHome.load(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() + "] version ["
				+ pdfFile.getVersion() + "] is enabled!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener,
				Event.Priority.Normal, this);
		configHandler.checkConfig();
		config.load();
		setupPermissions();
		
		getCommand("warpto").setExecutor(new Command_Warpto(this));
		getCommand("warphere").setExecutor(new Command_Warphere(this));
		getCommand("worldwarp").setExecutor(new Command_Worldwarp(this));
		getCommand("return").setExecutor(new Command_Return(this));
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
		
		
		HashMap<String, Boolean> allItems = new HashMap<String, Boolean>();
		allItems.put("quickwarp.return", true);
		allItems.put("quickwarp.death", true);
		allItems.put("quickwarp.warpto", true);
		allItems.put("quickwarp.warphere", true);
		allItems.put("quickwarp.world", true);
		allItems.put("quickwarp.wwreturn", true);
		allItems.put("quickwarp.warpall", true);

		Permission perm = new Permission("quickwarp.*",
				"Permission for using all QuickWarp functionality.",
				PermissionDefault.OP, allItems);
		Bukkit.getServer().getPluginManager().addPermission(perm);
	}

	public boolean checkPermission(Player player, String node) {
		if (permissionHandler != null) {
			return permissionHandler.has(player, node);
		} else {
			return player.hasPermission(node);
		}
	}


	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() + "] version ["
				+ pdfFile.getVersion() + "] has been disabled!");
	}
}
