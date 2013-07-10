package com.gmail.panzeri333.sectorport;

import org.bukkit.plugin.java.JavaPlugin;

public class SectorPort extends JavaPlugin {
	
    public void onEnable(){
    	
    	getServer().getPluginManager().registerEvents(new SectorPortListener(this), this);
    	
    	getCommand("sectorport").setExecutor(new SectorPortExecutor(this));
    	getCommand("sectorreqs").setExecutor(new SectorReqsExecutor(this));
    	
    	saveDefaultConfig();
    	
    	getLogger().info("STS is UP ;)");
    }

    public void onDisable() {
    	
    	//updateSignListFileConfig();
    	
    	getLogger().info("STS is DOWN :(");
    }
    
}