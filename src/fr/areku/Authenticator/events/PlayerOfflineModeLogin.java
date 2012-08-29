package fr.areku.Authenticator.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.areku.Authenticator.OfflineModePluginAuthenticator;

public class PlayerOfflineModeLogin extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private OfflineModePluginAuthenticator plugin;

	public PlayerOfflineModeLogin(Player player, OfflineModePluginAuthenticator plugin) {
		this.player = player;
		this.plugin = plugin;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public OfflineModePluginAuthenticator getPlugin() {
		return plugin;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
