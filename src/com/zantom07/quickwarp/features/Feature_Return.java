package com.zantom07.quickwarp.features;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.zantom07.quickwarp.QuickWarp;

public class Feature_Return {
	private QuickWarp plugin;
	
	public Feature_Return(QuickWarp plugin) {
		this.plugin = plugin;
	}
	
	public boolean enabled(Player player) {
		return plugin.returnUsers.containsKey(player);
	}
	
	public void endReturn(Player player) {
		if (enabled(player)) {
			Location returnto = plugin.returnUsers.get(player);
			player.teleport(returnto);
			plugin.returnUsers.remove(player);
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
			plugin.returnUsers.put(player, player.getLocation());
		} else {
			plugin.returnUsers.put(player, player.getLocation());
		}
	}
}
