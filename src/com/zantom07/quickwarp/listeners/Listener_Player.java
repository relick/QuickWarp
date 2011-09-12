package com.zantom07.quickwarp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.zantom07.quickwarp.QuickWarp;
import com.zantom07.quickwarp.features.Feature_Return;

public class Listener_Player extends PlayerListener {
	private QuickWarp plugin;
	private Feature_Return returnHandler;
	
	public Listener_Player(QuickWarp plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if(plugin.checkPermission(player, "quickwarp.death")) {
		returnHandler.startReturn(player);
		if ((Boolean) QuickWarp.config.getProperty("config.ShowDeathReturnText")) {
		player.sendMessage(ChatColor.BLUE+""+"Type /return or /r to return to where you died!");
		}
		}
	}

}
