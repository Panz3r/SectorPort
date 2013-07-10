package com.gmail.panzeri333.sectorport;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class SectorPortListener implements Listener {

	public SectorPort plugin = null;
	
	public SectorPortListener ( SectorPort plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignHit( PlayerInteractEvent evt ) {
		
		// Check for block		
		if( evt.getClickedBlock() != null && evt.getClickedBlock().getState() instanceof Sign ) { 
			final Sign sgn = (Sign) evt.getClickedBlock().getState();
					
			// Check STS block
			if( ! sgn.getLine(0).equalsIgnoreCase("[STS]") ) {
				return ;
			}
			
			// Check Action
			if ( evt.getAction() == Action.LEFT_CLICK_BLOCK  ) {
				// Check Destruction permission
				if(	! evt.getPlayer().hasPermission("sectorport.sign.destroy") && ! sgn.getLine(1).isEmpty()  ) {
					// Preserve sign from destruction
					evt.setCancelled(true);
					return;
				}				
			}
			else if ( evt.getAction() == Action.RIGHT_CLICK_BLOCK ) {
				// If user can use SectorPort sign
				if ( evt.getPlayer().hasPermission("sectorport.sign.use") ) {
					// Handle STS request
					Location dst = null;
				
					// Teleport with requirements
					if( !sgn.getLine(1).isEmpty() ) {
						
						final boolean isOK = meetsRequirements( evt.getPlayer(), sgn.getLine(1) );
						
						if( isOK || evt.getPlayer().hasPermission("sectorport.sign.forfree") ) {
							// Player meets requirements or has permission "For Free"
							dst = getDestination( sgn.getLine(1) );
						}
						else {
							// Warn Player
							evt.getPlayer().sendMessage(this.plugin.getConfig().getString("messages.errRequirements"));
						}
						
					}
					else {
						// not a valid STS sign
						return ;
					}
					
					
					// Move to location if exist
					if( dst != null ) {
						evt.getPlayer().teleport( dst, TeleportCause.PLUGIN );
					}
					else {
						// Warn player
						evt.getPlayer().sendMessage( this.plugin.getConfig().getString("messages.errLocation") );
					}
					
				}
				else {
					// Warn player
					evt.getPlayer().sendMessage( this.plugin.getConfig().getString("messages.errPermissions") );
				}
				
				// Cancel event so that STS Sign cannot be damaged
				evt.setCancelled( true );
			} // Right Click Action Check End
			
			else {
				// Cancel event so that STS Sign cannot be damaged
				evt.setCancelled( true );
			}
		
		} // Sign Hit Check End
		
	}
	
	
	// Check Requirements to meet for a given Destination (if any)
	private boolean meetsRequirements(Player player, String line) {
		
		List<Integer> reqs;
		
		try {
			reqs = this.plugin.getConfig().getIntegerList( "reqs." + line );
		}
		catch (Exception e) {
			return true;
		}
		
		int helmetID = 0, 
			chestID = 0,
			bootsID = 0,
			legsID = 0;
		
		if ( player.getInventory().getHelmet() != null ) {
			helmetID = player.getInventory().getHelmet().getTypeId();
		}

		if ( player.getInventory().getChestplate() != null ) {
			chestID = player.getInventory().getChestplate().getTypeId();
		}

		if ( player.getInventory().getLeggings() != null ) {
			legsID = player.getInventory().getLeggings().getTypeId();
		}
		
		if ( player.getInventory().getBoots() != null ) {
			bootsID = player.getInventory().getBoots().getTypeId();
		}
		
		for(int r : reqs ) {
			
			// player.sendMessage("");
			
			if( ! player.getInventory().contains( r ) && 
				 ! player.getEnderChest().contains( r ) &&
				  helmetID != r && chestID != r && bootsID != r && legsID != r ) 
			{
				return false;
			}
			
		}
		
		return true;
	}

	
	// Get location based on tag if exist
	private Location getDestination( final String tag ) {

		Set<String> sgns;
		
		try {
			sgns = this.plugin.getConfig().getConfigurationSection("sectors").getKeys(false);
		}
		catch (Exception e) {
			return null;
		}
		
		if ( sgns.contains(tag) ) {
			try {
				// Get Sector location from Config file (if exist)
				final World world = this.plugin.getServer().getWorld( this.plugin.getConfig().getString("sectors."+ tag +".w") );
				final Location loc = this.plugin.getConfig().getVector("sectors."+ tag +".l").toLocation(world);
				return loc;
			}
			catch (Exception e) {
				this.plugin.getLogger().log(Level.WARNING, "Error with tag " + tag);
				return null;
			}
		}
		else {
			return null;
		}
		
	}
	
	
}
