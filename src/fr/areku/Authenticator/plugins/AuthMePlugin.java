package fr.areku.Authenticator.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import uk.org.whoami.authme.api.API;

import fr.areku.Authenticator.OfflineModePluginAuthenticator;

public class AuthMePlugin implements OfflineModePluginAuthenticator {
	// private AuthMe thePlugin;

	@Override
	public String getName() {
		return "AuthMe";
	}

	@Override
	public boolean isPlayerLoggedIn(Player player) {
		return API.isAuthenticated(player);
	}

	@Override
	public void initialize(Plugin p) {
		// thePlugin = (AuthMe) p;
	}

	@Override
	public String getRecommendedVersion() {
		return "2.7.9";
	}

}
