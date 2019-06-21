package me.shin1gamix.voidchest.ecomanager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import com.google.common.collect.Lists;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.ecomanager.customeco.IEssentialsEconomy;
import me.shin1gamix.voidchest.ecomanager.customeco.IShopGUIPlusEconomy;
import me.shin1gamix.voidchest.ecomanager.customeco.IVoidEconomy;
import me.shin1gamix.voidchest.exceptions.VoidEconomyNotInitialized;
import me.shin1gamix.voidchest.utilities.Utils;

public class VoidEconomyManager {
	private final VoidChestPlugin core;

	public VoidEconomyManager(VoidChestPlugin core) {
		this.core = core;
	}

	private VoidEconomy voidEconomy;

	/**
	 * @return the voidEconomy
	 */
	public VoidEconomy getVoidEconomy() {
		return this.voidEconomy;
	}

	/**
	 * @param voidEconomy
	 *            the voidEconomy to set
	 */
	public void setVoidEconomy(VoidEconomy voidEconomy) {
		this.voidEconomy = voidEconomy;
	}

	public void initiateSell(final PlayerData data) {

		if (this.voidEconomy == null) {
			new VoidEconomyNotInitialized("It seems as if VoidEconomy wasn't initialized yet and a sell attempt was made!",
					"If this message persists being sent, contact Shin1gamiX").printStackTrace();
			return;
		}

		data.setAttemptSaleTime(System.currentTimeMillis()
				+ (FileManager.getInstance().getOptions().getFile().getLong("Sell.interval", 15)) * 1000);
		this.voidEconomy.initiateSell(data);
	}

	public VoidMode getCurrentMode() {
		final VoidEconomy ve = this.getVoidEconomy();

		if (ve instanceof IVoidEconomy) {
			return VoidMode.VOIDCHEST;
		} else if (ve instanceof IShopGUIPlusEconomy) {
			return VoidMode.SHOPGUIPLUS;
		} else if (ve instanceof IEssentialsEconomy) {
			return VoidMode.ESSENTIALS;
		}

		return VoidMode.CUSTOM;

	}

	public void hookVoidEcon() {

		final boolean debug = VoidChestPlugin.isDebugEnabled();
		List<String> debugging = Lists.newArrayList();
		if (debug) {
			debugging.add("An attempt to hook into an available mode has been started..");
			debugging.add("");
		}

		final String input = FileManager.getInstance().getOptions().getFile().getString("Sell.mode");
		final VoidMode mode = VoidMode.getByName(input);

		final VoidEconomy voidChestEconomy = new IVoidEconomy(this.core);

		if (mode == null) {
			this.core.hookVoidEconomy(voidChestEconomy, ServicePriority.Highest, this.core);
			if (debug) {
				debugging.add("No available mode found, defaulting to voidchest.");
				Utils.debug(this.core, debugging);
			}
			return;
		}

		if (debug) {
			debugging.add("Found an available mode! Hooking as: " + mode.getName());
			debugging.add("Details about the hook:");
		}
		VoidEconomy vec = null;

		switch (mode) {
		case VOIDCHEST:
			vec = voidChestEconomy;
			if (debug) {
				debugging.add("Name: " + vec.getName());
				Utils.debug(this.core, debugging);
			}
			this.core.hookVoidEconomy(voidChestEconomy, ServicePriority.Highest, this.core);

			return;
		case SHOPGUIPLUS:

			if (isPluginEnabled("ShopGUIPlus")) {
				vec = new IShopGUIPlusEconomy(this.core);
				debugging.add("Name: " + vec.getName());
				this.core.hookVoidEconomy(vec, ServicePriority.Highest, this.core);
			} else {
				debugging.add("Failed to hook to the plugin! Is it enabled?");
			}

			break;

		case ESSENTIALS:

			if (isPluginEnabled("Essentials")) {
				vec = new IEssentialsEconomy(this.core);
				debugging.add("Name: " + vec.getName());
				this.core.hookVoidEconomy(vec, ServicePriority.Highest, this.core);
			} else {
				debugging.add("Failed to hook to the plugin! Is it enabled?");
			}

			break;
		case CUSTOM:
			if (debug) {
				debugging.add("A custom mode is to be added, can't share any details yet.");
			}
			break;
		}

		if (debug) {
			Utils.debug(this.core, debugging);
		}

		this.core.hookVoidEconomy(voidChestEconomy, ServicePriority.Low, this.core);

	}

	private boolean isPluginEnabled(final String pluginName) {
		final Plugin essp = Bukkit.getPluginManager().getPlugin(pluginName);
		return essp != null && essp.isEnabled();
	}

}
