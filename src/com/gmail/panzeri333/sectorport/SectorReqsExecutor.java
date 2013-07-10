package com.gmail.panzeri333.sectorport;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Material;


public class SectorReqsExecutor implements CommandExecutor {
 
	private SectorPort plugin; // pointer to your main class, unrequired if you don't need methods from the main class
 
	public SectorReqsExecutor(SectorPort plugin) {
		this.plugin = plugin;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("sectorreqs")) {
			
			if (! (sender instanceof Player) ) {
				sender.sendMessage("[ERROR]This command can only be run by a player.");
			} 
			else {
				final Player player = (Player) sender;
				
				// Print out Guide to SectorReqs commandline
				if ( args.length == 0 ) {
						final String[] guide = { "---- SectorReqs Help ----", 
												 "/sectorreqs to show this guide",
												 "/sectorreqs <SectorName> set <ItemID|ItemName> to set requirement for a Sector",
												 "/sectorreqs <SectorName> reset  to remove requirements from a Sector",
												 "/sectorreqs <SectorName> list to list requirement for a Sector"
												};				
						
						player.sendMessage( guide );
				}
				
				//else if ( args.length == 1 && args[0].equalsIgnoreCase("list") ) {
				//	//Check for sectorport.sector.list
				//	if ( this.plugin.perms.has( player, "sectorport.sector.list" ) ) {
				//		final String sectorList = this.plugin.getConfig().getConfigurationSection("sectors").getKeys(false).toString();
				//		
				//		player.sendMessage( "Sectors: " + sectorList );
				//	}
				//	else {
				//		// Permission missing
				//		player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );	
				//	}
				//}
				
				else if ( args.length == 2 ) {
					// Check for arg[1] ( reset or list)
					if ( args[1].equalsIgnoreCase( "reset" ) ) {
						// Check for sectorport.command.reset
						if ( player.hasPermission("sectorport.requirements.reset") ) {

							if ( deleteRequirement( args[0] ) ) {
								player.sendMessage("[OK] Requirement for Sector " + args[0] +" removed.");
							}
							else {
								player.sendMessage("[ERROR] Requirement for Sector " + args[0] +" NOT removed.");
							}
							
						}
						else {
							// Permission missing
							player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );
						}
					}
					else if ( args[1].equalsIgnoreCase( "list" ) ) {
						// Check for sectorport.requirements.list
						if ( player.hasPermission("sectorport.requirements.list") ) {

							final String reqs = this.plugin.getConfig().getConfigurationSection("reqs").getIntegerList( args[0] ).toString();
							
							player.sendMessage("Requirements for " + args[0] + ": " + reqs);
							
						}
						else {
							// Permission missing
							player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );
						}
					} 
					else {
						// Error in command
						//player.sendMessage("[ERROR] No such subcommand in SectorReqs");
						return false;
					}
						
				}
				
				else if ( args.length == 3 ) {
					// Check for arg[1] ( set )
					if ( args[1].equalsIgnoreCase( "set" ) ) {
						// Check for sectorport.requirements.set
						if ( player.hasPermission("sectorport.requirements.set") ) {
							
							if( args[2].equalsIgnoreCase("this") ) {
								args[2] = String.valueOf( player.getItemInHand().getTypeId() ); 
							}
							
							// Check for ID validity
							Material m = null;
						    try {
						    	  final int id = Integer.parseInt(args[2]);
						           m = Material.getMaterial(id);
						    } catch (NumberFormatException e) {
						           m = Material.matchMaterial(args[2]);
						           args[2] = String.valueOf( m.getId() );
						    }
						    
						    if (m == null) {
						    	player.sendMessage("[ERROR] Unknown material " +args[2]);
						    } 
						    else {
						    	// Valid material -> go on setting requirement
						    	if ( setRequirement( args[0], Integer.parseInt(args[2])  ) ) {
						    		player.sendMessage("[OK] Requirement for Sector " + args[0] +" set to " + m.toString() + ".");
						    	}
						    	else {
						    		player.sendMessage("[ERROR] Requirement for Sector " + args[0] +" NOT set.");
						    	}
						    }
							
						}
						else {
							// Permission missing
							player.sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );
						}
						
					}
					else {
						// Error in command
						//player.sendMessage("[ERROR] No such subcommand in SectorReqs");
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
	
	private boolean setRequirement( final String sectorName, final int requestedID ) {
		
		try {
			// Add requirement to config.yml
			List<Integer> reqsArray = new ArrayList<Integer>();
			reqsArray.add( requestedID );
			this.plugin.getConfig().set("reqs." + sectorName, reqsArray );
			// Update config.yml file
			this.plugin.saveConfig();
		}
		catch ( Exception e ) {
			this.plugin.getLogger().log( Level.SEVERE, e.toString());
			this.plugin.getLogger().log( Level.SEVERE, "Error setting requirement to " + requestedID + " for sector " + sectorName + " to config.yml" );
			return false;
		}
		
		return true;
	}
	
	private boolean deleteRequirement( final String sectorName ) {
		try {
			// Remove requirement from config.yml
			this.plugin.getConfig().getConfigurationSection("reqs").set( sectorName, null );
			// Update config.yml file
			this.plugin.saveConfig();
		}
		catch ( Exception e ) {
			this.plugin.getLogger().log( Level.SEVERE, "Error removing requirements for sector " + sectorName + " from config.yml" );
			return false;
		}
		
		return true;
	}
	
}