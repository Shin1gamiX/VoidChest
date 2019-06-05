package me.shin1gamix.voidchest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.shin1gamix.voidchest.commands.VoidCommand;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.ecomanager.VoidEconomyManager;
import me.shin1gamix.voidchest.listener.InventoryInteractListener;
import me.shin1gamix.voidchest.listener.VoidChestBreakListener;
import me.shin1gamix.voidchest.listener.VoidChestExplodeListener;
import me.shin1gamix.voidchest.listener.VoidChestPlaceListener;
import me.shin1gamix.voidchest.listener.VoidEconomyRegisterListener;
import me.shin1gamix.voidchest.listener.VoidMenuClickListener;
import me.shin1gamix.voidchest.metrics.MetricsHandler;
import me.shin1gamix.voidchest.runnables.HologramTimerTask;
import me.shin1gamix.voidchest.runnables.PurgeTask;
import me.shin1gamix.voidchest.runnables.SaveTask;
import me.shin1gamix.voidchest.runnables.UpdateCheckTask;
import me.shin1gamix.voidchest.utilities.MessagesX;
import me.shin1gamix.voidchest.utilities.VaultAPI;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidItemManager;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidManager;

public class VoidChestPlugin extends JavaPlugin implements Listener {

	private static VoidChestPlugin plugin;

	public static VoidChestPlugin getInstance() {
		return plugin == null ? plugin = VoidChestPlugin.getPlugin(VoidChestPlugin.class) : plugin;
	}

	private final VaultAPI vault = new VaultAPI(this);
	private PlayerDataManager playerDataManager;
	private VoidManager voidManager;
	private final VoidEconomyManager voidEconomyManager = new VoidEconomyManager(this);

	private boolean hdSupport = false;

	private BukkitTask savingTask = null;
	private BukkitTask purgingTask = null;

	@Override
	public void onEnable() {

		FileManager fmanager = FileManager.getInstance();
		fmanager.loadFiles(this);
		MessagesX.repairPaths(fmanager.getMessages());

		this.voidManager = new VoidManager(this);
		this.playerDataManager = new PlayerDataManager();

		final Listener[] listeners = new Listener[] { this.playerDataManager, new VoidChestBreakListener(this),
				new VoidChestPlaceListener(this), new InventoryInteractListener(this), new VoidMenuClickListener(this),
				new VoidEconomyRegisterListener(this), new VoidChestExplodeListener(this) };

		final PluginManager pm = Bukkit.getPluginManager();
		for (Listener listener : listeners) {
			pm.registerEvents(listener, this);
		}

		final Plugin pl = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
		if (pl != null && pl.isEnabled()) {
			this.hdSupport = true;
		}

		this.getCommand("voidchest").setExecutor(new VoidCommand(this));

		new HologramTimerTask(this).runTaskTimer(this, 0l, 3l);
		VoidItemManager.getInstance().cacheItems();

		this.attemptStartSaving();
		this.attemptStartPurging();

		new MetricsHandler(this).setupMetrics();

		new UpdateCheckTask(this).runTaskTimer(this, 20 * 10, (20 * 60) * 60 * 2);

		Bukkit.getScheduler().runTaskLater(this, () -> {
			this.playerDataManager.loadPlayerDatas();
			this.voidEconomyManager.hookVoidEcon();
		}, 1);

		
	}

	public void attemptStartSaving() {
		if (this.savingTask != null) {
			this.savingTask.cancel();
			this.savingTask = null;
		}
		FileConfiguration options = FileManager.getInstance().getOptions().getFile();
		if (!options.getBoolean("Saving.enabled", false)) {
			return;
		}

		final long interval = options.getLong("Saving.interval", 30);
		final SaveTask task = new SaveTask(this);
		this.savingTask = task.runTaskTimer(this, 100, interval * 20);
	}

	public void attemptStartPurging() {
		if (this.purgingTask != null) {
			this.purgingTask.cancel();
			this.purgingTask = null;
		}
		FileConfiguration options = FileManager.getInstance().getOptions().getFile();
		if (!options.getBoolean("Purging.enabled", true)) {
			return;
		}

		final long interval = options.getLong("Purging.interval", 100);
		final PurgeTask task = new PurgeTask(this);
		this.purgingTask = options.getBoolean("Purging.task-async", false)
				? task.runTaskTimerAsynchronously(this, 100, interval)
				: task.runTaskTimer(this, 100, interval);

	}

	@Override
	public void onDisable() {
		if (this.savingTask != null) {
			this.savingTask.cancel();
			this.savingTask = null;
		}

		if (this.purgingTask != null) {
			this.purgingTask.cancel();
			this.purgingTask = null;
		}

		this.playerDataManager.savePlayerDatas(true, true, false);
		FileManager.getInstance().getPlayerBase().saveFile();
		Bukkit.getServicesManager().unregisterAll(this);
		if (this.hdSupport) {
			HologramsAPI.getHolograms(this).forEach(Hologram::delete);
		}
	}

	/**
	 * @return the pdm
	 */
	public PlayerDataManager getPlayerDataManager() {
		return this.playerDataManager;
	}

	/**
	 * @return the vault
	 */
	public VaultAPI getVault() {
		return this.vault;
	}

	/**
	 * @return the vm
	 */
	public VoidManager getVoidManager() {
		return this.voidManager;
	}

	/**
	 * @return the voidEconomyManager
	 */
	public VoidEconomyManager getVoidEconomyManager() {
		return voidEconomyManager;
	}

	public boolean isHdSupport() {
		return hdSupport;
	}

	public void setHdSupport(boolean hdSupport) {
		this.hdSupport = hdSupport;
	}

}
