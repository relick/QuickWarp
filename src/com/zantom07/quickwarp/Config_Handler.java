package com.zantom07.quickwarp;

import java.io.File;
import java.io.IOException;


public class Config_Handler {
	private QuickWarp plugin;
	
	public Config_Handler(QuickWarp plugin) {
		this.plugin = plugin;
	}
	
	public void checkConfig() {
		String file = plugin.getDataFolder().toString() + "/config.yml";
		File yml = new File(file);
		if (!yml.exists()) {
			new File(plugin.getDataFolder().toString()).mkdir();
			try {
				yml.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		QuickWarp.config = plugin.getConfiguration();
		if (QuickWarp.config.getProperty("config.ShowReturnText") == null) {
			QuickWarp.config.setProperty("config.ShowReturnAcknowledgedText", true);
			QuickWarp.config.setProperty("config.ShowNewPositionText", true);
			QuickWarp.config.setProperty("config.ShowReturnText", true);
			QuickWarp.config.setProperty("config.ShowDeathReturnText", true);
			QuickWarp.config.setProperty("config.ShowWorldWarpReturnText", true);
			QuickWarp.config.setProperty("config.ShowWarpHereTextToWarpee", true);
			QuickWarp.config.setProperty("config.ShowWarpHereTextToCommandSender", true);
			QuickWarp.config.setProperty("config.ShowWarpToText", true);
			QuickWarp.config.save();
			plugin.logger.info("[QuickWarp] Default configuration created.");
		}
	}
}
