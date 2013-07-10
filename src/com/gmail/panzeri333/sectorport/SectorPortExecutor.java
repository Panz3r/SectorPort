package com.gmail.panzeri333.sectorport;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


public class SectorPortExecutor implements CommandExecutor {
 
	private SectorPort plugin; // pointer to your main class, unrequired if you don't need methods from the main class
 
	public SectorPortExecutor(SectorPort plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("sectorport")) {
			
			if (! (sender instanceof Player) ) {
				sender.sendMessage("[ERROR]This command can only be run by a player.");
			} 
			else {
				final Player player = (Player) sender;
				
				// Print out Guide to SectorPort commandline
				if ( args.length == 0 ) {
						final String[] guide = { "---- SectorPort Help ----", 
												 "/sectorport to show this guide",
												 "/sectorport new <SectorName> to create a new Sector",
												 "/sectorport delete <SectorName> to remove a Sector",
												 "/sectorport list to get a list of all Sectors"
												};				
						
						player.sendMessage( guide );
				}
				
				else if ( args.length == 1 && args[0].equalsIgnoreCase("list") ) {
					//Check for sectorport.sector.list
					if ( player.hasPermission("sectorport.sector.list") ) {
						final String sectorList = this.plugin.getConfig().getConfigurationSection("sectors").getKeys(false).toString();
						
						player.sendMessage( "Sectors: " + sectorList );
					}
					else {
						// Permission missing
						player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );	
					}
				}
				
				else if ( args.length == 2 ) {
					// Check for arg[0] ( new or delete )
					if ( args[0].equalsIgnoreCase( "new" ) ) {
						// Check for sectorport.command.set
						if ( player.hasPermission("sectorport.sector.set") ) {
							
							if ( addSector( args[1], player.getLocation()  ) ) {
								player.sendMessage("[OK] Sector " + args[1] +" added.");
							}
							else {
								player.sendMessage("[ERROR] Sector " + args[1] +" NOT added.");
							}
							
						}
						else {
							// Permission missing
							player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );
						}
						
					}
					else if ( args[0].equalsIgnoreCase( "del" ) ) {
						// Check for sectorport.command.unset
						if ( player.hasPermission("sectorport.sector.unset") ) {

							if ( deleteSector( args[1] ) ) {
								player.sendMessage("[OK] Sector " + args[1] +" removed.");
							}
							else {
								player.sendMessage("[ERROR] Sector " + args[1] +" NOT removed.");
							}
							
						}
						else {
							// Permission missing
							player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );
						}
					}
					else {
						// Error in command
						//player.sendMessage("[ERROR] No such subcommand in SectorPort");
						return false;
					}
						
				}
				
				else {
					//player.sendMessage("[ERROR] Wrong Arguments Number");
					return false;
				}
				
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean addSector( final String name, final Location loc ) {
		
		final Vector v = loc.toVector();
		final String w = loc.getWorld().getName();
		
		try {
			// Add Sector to config.yml
			this.plugin.getConfig().set("sectors." + name + ".w", w );
			this.plugin.getConfig().set("sectors." + name + ".l", v );
			// Update config.yml file
			this.plugin.saveConfig();
		}
		catch ( Exception e ) {
			this.plugin.getLogger().log( Level.SEVERE, "Error adding " + name + " sector to config.yml" );
			return false;
		}
		
		return true;
	}
	
	private boolean deleteSector( final String name ) {
		try {
			// Add Sector to config.yml
			this.plugin.getConfig().getConfigurationSection("sectors").set( name, null );
			// Update config.yml file
			this.plugin.saveConfig();
		}
		catch ( Exception e ) {
			this.plugin.getLogger().log( Level.SEVERE, "Error removing " + name + " sector to config.yml" );
			return false;
		}
		
		return true;
	}

}