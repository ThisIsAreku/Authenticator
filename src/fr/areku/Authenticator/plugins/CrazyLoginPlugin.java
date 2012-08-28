package fr.areku.Authenticator.plugins;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.st_ddt.crazylogin.CrazyLogin;
import fr.areku.Authenticator.OfflineModePluginAuthenticator;

public class CrazyLoginPlugin implements OfflineModePluginAuthenticator {
	private CrazyLogin thePlugin;

	@Override
	public String getName() {
		return "CrazyLogin";
	}

	@Override
	public boolean isPlayerLoggedIn(Player player) {
		return thePlugin.isLoggedIn(player);
	}

	@Override
	public void initialize(Plugin p) {
		thePlugin = (CrazyLogin) p;
	}

	@Override
	public String getRecommendedVersion() {
		// TODO Auto-generated method stub
		return "6.5";
	}

}
