package fr.areku.Authenticator.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.authdb.AuthDB;

import fr.areku.Authenticator.OfflineModePluginAuthenticator;

public class AuthDBPlugin implements OfflineModePluginAuthenticator {
	//private AuthDB thePlugin;

	@Override
	public String getName() {
		return "AuthDB";
	}

	@Override
	public boolean isPlayerLoggedIn(Player player) {
		return AuthDB.isAuthorized(player);
	}

	@Override
	public void initialize(Plugin p) {
		//thePlugin = (AuthDB) p;
	}

	@Override
	public String getRecommendedVersion() {
		return "2.3.6.242";
	}

}
