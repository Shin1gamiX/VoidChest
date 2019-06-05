package me.shin1gamix.voidchest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;

import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.ecomanager.VoidEconomyManager;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidItemManager;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidManager;

public final class VoidChestAPI {

	private static VoidChestAPI instance;

	private VoidChestAPI() {
	}

	public static VoidChestAPI getInstance() {
		return instance == null ? instance = new VoidChestAPI() : instance;
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

	public VoidEconomyManager getVoidEconomyManager() {
		return VoidChestPlugin.getInstance().getVoidEconomyManager();
	}

	public PlayerDataManager getPlayerDataManager() {
		return VoidChestPlugin.getInstance().getPlayerDataManager();
	}

	public VoidItemManager getVoidItemManager() {
		return VoidItemManager.getInstance();
	}

	public VoidManager getVoidManager() {
		return VoidChestPlugin.getInstance().getVoidManager();
	}

}
