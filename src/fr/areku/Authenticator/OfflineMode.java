package fr.areku.Authenticator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class OfflineMode implements Runnable {

	private OfflineModePluginAuthenticator selectedAuthPlugin;
	private List<String> watchedPlayers;
	private Authenticator parent;
	private BukkitTask timerId = null;
	private boolean timerEnabled = false;
	private String CLASS_PREFIX = "";

	public OfflineMode(Authenticator main) {
		parent = main;
		watchedPlayers = Collections.synchronizedList(new ArrayList<String>());
		CLASS_PREFIX = (this.getClass().getPackage().getName() + ".plugins")
				.replace('.', '/');
		Authenticator.d(CLASS_PREFIX);
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
						Authenticator.d("Found new OfflineModePlugin:"
								+ authenticator.getName());

						Plugin p = Bukkit.getServer().getPluginManager()
								.getPlugin(authenticator.getName());
						if (p != null) {
							selectedAuthPlugin = authenticator;
							selectedAuthPlugin.initialize(p);
							Authenticator.log("Selected "
									+ selectedAuthPlugin.getName()
									+ " as offline mode plugin");
							if (!selectedAuthPlugin.getRecommendedVersion()
									.equals(p.getDescription().getVersion()
											.trim())) {
								Authenticator
										.log(Level.WARNING,
												selectedAuthPlugin.getName()
														+ " version is '"
														+ p.getDescription()
																.getVersion()
														+ "' while Authenticator is designed for '"
														+ selectedAuthPlugin
																.getRecommendedVersion()
														+ "'");
								Authenticator
										.log(Level.WARNING,
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
				Authenticator.log("No compatible offline mode plugin found");
			} else {
				// enableTimer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enableTimer() {
		if (!isTimerEnabled()) {
			Authenticator.d("Timer : enabling..");
			timerId = Bukkit.getScheduler().runTaskTimer(parent, this, 2, 20);
			timerEnabled = true;
		}
	}

	public void disableTimer() {
		if (isTimerEnabled()) {
			Authenticator.d("Timer : disabling..");
			timerId.cancel();
			timerEnabled = false;
		}
	}

	public boolean isTimerEnabled() {
		return timerEnabled;
	}

	public OfflineModePluginAuthenticator getSelectedAuthPlugin() {
		return selectedAuthPlugin;
	}

	public boolean isUsingOfflineModePlugin() {
		return (selectedAuthPlugin != null);
	}

	public void watchPlayerLogin(String player) {
		synchronized (watchedPlayers) {
			watchedPlayers.add(player);
			enableTimer();
		}
	}

	public void removeWatchedPlayer(String player) {
		synchronized (watchedPlayers) {
			watchedPlayers.remove(player);
		}
	}

	public boolean isWatchingPlayer(String player) {
		return watchedPlayers.contains(player);
	}

	public boolean isPlayerLoggedIn(Player player) {
		try {
			return selectedAuthPlugin.isPlayerLoggedIn(player);
		} catch (Exception e) {
			Authenticator.log(Level.WARNING, "Cannot get player status with "
					+ selectedAuthPlugin.getName() + ": ");
			Authenticator.log(Level.WARNING, e.getLocalizedMessage());
		}
		return false;
	}

	@Override
	public void run() {
		if (!isTimerEnabled())
			return;
		Authenticator.d("watching " + watchedPlayers.size() + " player(s)");
		if (watchedPlayers.isEmpty()) {
			Authenticator.d("watchedPlayer list is empty, stopping timer");
			disableTimer();
			return;
		}
		List<String> toRemove = new ArrayList<String>();
		Player pl = null;
		synchronized (watchedPlayers) {
			for (String p : watchedPlayers) {
				pl = Bukkit.getPlayerExact(p);
				if (pl != null) {
					if (isPlayerLoggedIn(pl)) {
						Authenticator.d("Player " + p + " is now auth");
						toRemove.add(p);
						parent.notifyPlayerLogin(pl);
					}
				}
				pl = null;
			}
			watchedPlayers.removeAll(toRemove);
		}
	}
}
