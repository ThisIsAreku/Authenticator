package fr.areku.Authenticator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class OfflineMode extends TimerTask {

	// private AuthDB AuthDBPlugin;
	private OfflineModePluginAuthenticator selectedAuthPlugin;
	private List<String> watchedPlayers;
	private Main parent;
	private Timer t = null;
	private String CLASS_PREFIX = "";

	public OfflineMode(Main main) {
		parent = main;
		watchedPlayers = Collections.synchronizedList(new ArrayList<String>());
		CLASS_PREFIX = (this.getClass().getPackage().getName() + ".plugins")
				.replace('.', '/');
		Main.d(CLASS_PREFIX);
	}

	private String getClassFromPath(String pathname) {
		String classname = pathname.replace('/', '.').trim();
		classname = classname.substring(0, classname.length() - 6);
		return classname;
	}

	@SuppressWarnings("unused")
	public void hookAuthPlugins() {
		try {
			JarInputStream jarFile = new JarInputStream(this.getClass()
					.getProtectionDomain().getCodeSource().getLocation()
					.openStream());
			JarEntry entry = null;
			OfflineModePluginAuthenticator authenticator = null;
			while ((entry = jarFile.getNextJarEntry()) != null) {
				try {
					if (entry == null)
						continue;
					if (entry.getName().startsWith(CLASS_PREFIX)
							&& !entry.isDirectory()) {
						String classname = getClassFromPath(entry.getName());
						authenticator = (OfflineModePluginAuthenticator) this
								.getClass().getClassLoader()
								.loadClass(classname).newInstance();
						Main.d("Found new OfflineModePlugin:"
								+ authenticator.getName());
						Plugin p = Bukkit.getServer().getPluginManager()
								.getPlugin(authenticator.getName());
						if (p != null) {
							selectedAuthPlugin = authenticator;
							selectedAuthPlugin.initialize(p);
							Main.log("Selected " + selectedAuthPlugin.getName()
									+ " as offline mode plugin");
							if (!selectedAuthPlugin.getRecommendedVersion()
									.equals(p.getDescription().getVersion()
											.trim())) {
								Main.log(
										Level.WARNING,
										selectedAuthPlugin.getName()
												+ " version is '"
												+ p.getDescription()
														.getVersion()
												+ "' while Authenticator is designed for '"
												+ selectedAuthPlugin
														.getRecommendedVersion()
												+ "'");
								Main.log(Level.WARNING,
										"It might not work as expected. Check or ask for an update !");
							}
							break;
						}
					}
				} catch (Exception e) {
					// Main.logException(e,
					// "Error while initializing OfflineMode");
				}
			}
			jarFile.close();

			if (selectedAuthPlugin == null) {
				Main.log("No compatible offline mode plugin found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enableTimer() {
		if (!isTimerEnabled()) {
			Main.log("Timer : enabling..");
			this.t = new Timer();
			this.t.schedule(this, 0, 2000);
		}
	}

	public void disableTimer() {
		if (isTimerEnabled()) {
			Main.log("Timer : disabling..");
			this.t.cancel();
			this.t = null;
		}
	}

	public boolean isTimerEnabled() {
		return (this.t != null);
	}
	
	public OfflineModePluginAuthenticator getSelectedAuthPlugin(){
		return selectedAuthPlugin;
	}

	public boolean isUsingOfflineModePlugin() {
		return (selectedAuthPlugin != null);
	}

	public void watchPlayerLogin(String player) {
		synchronized (watchedPlayers) {
			watchedPlayers.add(player);
		}
	}

	public boolean isPlayerLoggedIn(Player player) {
		try {
			return selectedAuthPlugin.isPlayerLoggedIn(player);
		} catch (Exception e) {
			Main.log(Level.WARNING, "Cannot get player status with "
					+ selectedAuthPlugin.getName() + ": ");
			Main.log(Level.WARNING, e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	public void run() {
		Main.d("watching " + watchedPlayers.size() + " player(s)");
		if (watchedPlayers.isEmpty())
			return;
		List<String> toRemove = new ArrayList<String>();
		Player pl = null;
		synchronized (watchedPlayers) {
			for (String p : watchedPlayers) {
				pl = Bukkit.getPlayerExact(p);
				if (pl != null) {
					if (isPlayerLoggedIn(pl)) {
						Main.d("Player " + p + " is now auth");
						toRemove.add(p);
						parent.notifyListeners(pl);
					}
				}
				pl = null;
			}
			watchedPlayers.removeAll(toRemove);
			if (watchedPlayers.isEmpty()) {
				Main.d("watchedPlayer list is empty, stopping timer");
				disableTimer();
			}
		}
	}
}
