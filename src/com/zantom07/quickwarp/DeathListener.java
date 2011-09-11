package com.zantom07.quickwarp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener extends PlayerListener {
	private QuickWarp plugin;
	
	public DeathListener(QuickWarp plugin) {
		super();
		this.plugin = plugin;
	}
	
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if(plugin.checkPermission(player, "quickwarp.death")) {
		plugin.startReturn(player);
		if ((Boolean) QuickWarp.config.getProperty("config.ShowDeathReturnText")) {
		player.sendMessage(ChatColor.BLUE+""+"Type /return or /r to return to where you died!");
		}
		}
	}

}
