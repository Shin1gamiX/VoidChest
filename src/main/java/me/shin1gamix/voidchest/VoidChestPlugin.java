package me.shin1gamix.voidchest;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.commands.VoidCommand;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.ecomanager.VoidEconomyManager;
import me.shin1gamix.voidchest.listener.DeveloperInformListener;
import me.shin1gamix.voidchest.listener.InventoryInteractListener;
import me.shin1gamix.voidchest.listener.VoidChestBreakListener;
import me.shin1gamix.voidchest.listener.VoidChestExplodeListener;
import me.shin1gamix.voidchest.listener.VoidChestPlaceListener;
import me.shin1gamix.voidchest.listener.VoidEconomyRegisterListener;
import me.shin1gamix.voidchest.listener.VoidMenuClickListener;
import me.shin1gamix.voidchest.metrics.MetricsHandler;
import me.shin1gamix.voidchest.tasks.TaskManager;
import me.shin1gamix.voidchest.tasks.UpdateCheckTask;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.Utils;
import me.shin1gamix.voidchest.vaultapi.VaultAPI;
import me.shin1gamix.voidchest.voidmanager.VoidItemManager;
import me.shin1gamix.voidchest.voidmanager.VoidStorageManager;

public class VoidChestPlugin extends JavaPlugin {

	private static VoidChestPlugin plugin;

	public static VoidChestPlugin getInstance() {
		return plugin == null ? plugin = VoidChestPlugin.getPlugin(VoidChestPlugin.class) : plugin;
	}

	private final VaultAPI vault = new VaultAPI(this);
	private VoidStorageManager voidStorageManager = new VoidStorageManager();
	private final VoidEconomyManager voidEconomyManager = new VoidEconomyManager(this);
	private final TaskManager taskManager = new TaskManager(this);

	private boolean holographicDisplaysSupport = false;

	@Override
	public void onEnable() {

		long startTime = System.currentTimeMillis();
		Map<String, String> map = Maps.newHashMap();
		map.put("%version%", this.getDescription().getVersion());
		System.out.println(Utils.placeHolder("[VoidChest-%version%] Loading plugin and related data...", map, false));

		FileManager fmanager = FileManager.getInstance();
		fmanager.loadFiles(this);
		MessagesUtil.repairPaths(fmanager.getMessages());

		final PlayerDataManager pdm = PlayerDataManager.getInstance();

		final Listener[] listeners = new Listener[] { pdm, new VoidChestBreakListener(this),
				new VoidChestPlaceListener(), new InventoryInteractListener(this), new VoidMenuClickListener(),
				new VoidEconomyRegisterListener(this), new VoidChestExplodeListener(this),
				new DeveloperInformListener(this) };

		final PluginManager pm = Bukkit.getPluginManager();
		for (Listener listener : listeners) {
			pm.registerEvents(listener, this);
		}

		final Plugin pl = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
		if (pl != null && pl.isEnabled()) {
			this.holographicDisplaysSupport = true;
		}

		this.getCommand("voidchest").setExecutor(new VoidCommand(this));

		VoidItemManager.getInstance().cacheItems();

		this.taskManager.attemptStartSaving();
		this.taskManager.attemptStartPurging();
		this.taskManager.attemptStartHologram();
		new MetricsHandler(this).setupMetrics();
		UpdateCheckTask.startTask(this);

		Bukkit.getScheduler().runTaskLater(this, () -> {
			pdm.loadPlayerDatas();
			this.voidEconomyManager.hookVoidEcon();
		}, 1l);

		final String result = (System.currentTimeMillis() - startTime) + "ms";
		map.put("%result%", result);
		Bukkit.getScheduler().runTaskLater(this, () -> {
			System.out.println(" ");
			System.out.println(" ");
			System.out.println(Utils.placeHolder("[VoidChest-%version%] Plugin loaded in %result%", map, false));
			System.out.println(" ");
			System.out.println(" ");
		}, 20l * 5);

	}

	@Override
	public void onDisable() {
		this.taskManager.disableAll();

		PlayerDataManager.getInstance().savePlayerDatas(true, true);
		FileManager.getInstance().getPlayerBase().saveFile();
		Bukkit.getServicesManager().unregisterAll(this);
		if (this.holographicDisplaysSupport) {
			HologramsAPI.getHolograms(this).forEach(Hologram::delete);
		}
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
	public VoidStorageManager getVoidManager() {
		return this.voidStorageManager;
	}

	/**
	 * @return the voidEconomyManager
	 */
	public VoidEconomyManager getVoidEconomyManager() {
		return voidEconomyManager;
	}

	public boolean isHolographicDisplaysSupport() {
		return holographicDisplaysSupport;
	}

	public void setHolographicDisplaysSupport(boolean holographicDisplaysSupport) {
		this.holographicDisplaysSupport = holographicDisplaysSupport;
	}

	public boolean hookVoidEconomy(VoidEconomy instance, final ServicePriority priority, final JavaPlugin plugin) {
		Preconditions.checkNotNull(instance, "The VoidEconomy instance provided may not be null.");
		Preconditions.checkNotNull(instance.getName(), "The VoidEconomy instance's name provided may not be null.");
		Preconditions.checkNotNull(priority, "The ServicePriority instance may not be null.");
		Preconditions.checkNotNull(plugin, "The JavaPlugin instance can't be null!");
		final VoidChestPlugin vc = VoidChestPlugin.getInstance();

		if (instance.isVaultDependent()) {
			if (!vc.getVault().setupEconomy()) {
				Bukkit.getPluginManager().disablePlugin(vc);
				throw new RuntimeException(
						"The current VoidEconomy instance is vault dependent and no vault instance has been located. Disabling VoidChest...");
			}
		}

		final ServicesManager sm = vc.getServer().getServicesManager();
		sm.register(VoidEconomy.class, instance, vc, priority);
		return true;
	}

	public static boolean isDebugEnabled() {
		return FileManager.getInstance().getOptions().getFile().getBoolean("Debugging", false);
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

}
