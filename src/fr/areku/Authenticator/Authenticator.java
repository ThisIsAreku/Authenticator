package fr.areku.Authenticator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.areku.commons.UpdateChecker;

public class Authenticator extends JavaPlugin {
	private static Authenticator instance;
	private List<OfflineModeListener> listeners;
	private OfflineMode controller;
	public static boolean DEBUG = false;

	public static void log(Level level, String m) {
		instance.getLogger().log(level, m);
	}

	public static void d(Level level, String m) {
		if (!DEBUG)
			return;
		instance.getLogger().log(level, "[DEBUG] " + m);
	}

	public static void log(String m) {
		log(Level.INFO, m);
	}

	public static void d(String m) {
		d(Level.INFO, m);
	}

	@Override
	public void onLoad() {
		instance = this;
		listeners = new ArrayList<OfflineModeListener>();
		controller = new OfflineMode(this);
	}

	@Override
	public void onEnable() {
		controller.hookAuthPlugins();
		Bukkit.getServer()
				.getPluginManager()
				.registerEvents(new InternalPlayerListener(this.controller),
						this);
		startMetrics();
		startUpdate();
	}

	private void startMetrics() {

		try {
			log("Starting Metrics");
			Metrics metrics = new Metrics(this);
			Metrics.Graph fitersCount = metrics
					.createGraph("Offline-mode plugins");
			if (controller.isUsingOfflineModePlugin())
				fitersCount.addPlotter(new Metrics.Plotter(controller
						.getSelectedAuthPlugin().getName()) {

					@Override
					public int getValue() {
						return 1;
					}

				});
			metrics.start();
		} catch (IOException e) {
			log("Cannot start Metrics...");
		}
	}

	private void startUpdate() {
		try {
			UpdateChecker update = new UpdateChecker(this);
			update.start();
		} catch (MalformedURLException e) {
			log("Cannot start Plugin Updater...");
		}
	}

	public void notifyListeners(Player player) {
		List<Integer> nulled = new ArrayList<Integer>();
		int i = 0;
		for (OfflineModeListener l : instance.listeners) {
			if (l == null) {
				nulled.add(i);
			} else {
				l.onPlayerPluginLogin(player);
			}
			i++;
		}
		for (Integer index : nulled)
			instance.listeners.remove(index);
	}

	public static void registerOfflineModeListener(OfflineModeListener l) {
		instance.listeners.add(l);
	}

	public static void deregisterOfflineModeListener(OfflineModeListener l) {
		instance.listeners.remove(l);
	}

	public static boolean isPlayerLoggedIn(Player p) {
		return instance.controller.isPlayerLoggedIn(p);
	}

}
