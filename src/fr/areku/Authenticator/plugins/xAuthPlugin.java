package fr.areku.Authenticator.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.cypherx.xauth.xAuth;

import fr.areku.Authenticator.OfflineModePluginAuthenticator;


public class xAuthPlugin implements OfflineModePluginAuthenticator {
	private xAuth thePlugin;

	@Override
	public String getName() {
		return "xAuth";
	}

	@Override
	public boolean isPlayerLoggedIn(Player player) {
		return (thePlugin.getPlayerManager().checkSession(thePlugin.getPlayerManager().getPlayer(player)));
	}

	@Override
	public void initialize(Plugin p) {
		thePlugin = (xAuth) p;
	}

	@Override
	public String getRecommendedVersion() {
		return "2.0.26";
	}

}
