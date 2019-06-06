package me.shin1gamix.voidchest.ecomanager;

import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import com.google.common.collect.Lists;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.VoidChestAPI;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.customeco.CraftEssentialsEconomy;
import me.shin1gamix.voidchest.ecomanager.customeco.CraftShopGUIPlusEconomy;
import me.shin1gamix.voidchest.ecomanager.customeco.CraftVoidEconomy;
import me.shin1gamix.voidchest.utilities.DebugUtil;
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

		final VoidEconomy econ = this.voidEconomy;

		if (econ == null) {
			Utils.debug(this.core, "It seems as if VoidEconomy wasn't initialized yet and a sell attempt was made!",
					"If this message persists being sent, contact Shin1gamiX");
			return;
		}

		/* Attempt to gather all items from chest to voidchest. */
		for (VoidStorage chest : data.getVoidStorages()) {

			if (!this.core.getVoidManager().isChest(chest.getBlock())) {
				continue;
			}

			final Inventory blockInv = chest.getBlockInventory();
			if (blockInv == null) {
				continue;
			}

			for (int i = 0; i < blockInv.getSize(); i++) {
				ItemStack item = blockInv.getItem(i);
				if (item == null) {
					continue;
				}

				if (chest.getCustomInventory().firstEmpty() == -1) {
					break;
				}

				chest.getCustomInventory().addItem(item);
				blockInv.clear(i);

			}
		}

		econ.sellInventory(data);
	}

	public VoidMode getCurrentMode() {
		final VoidEconomy ve = this.getVoidEconomy();
		if (ve instanceof CraftVoidEconomy) {
			return VoidMode.CRAFT_VOIDCHEST;
		} else if (ve instanceof CraftShopGUIPlusEconomy) {
			return VoidMode.CRAFT_SHOPGUIPLUS;
		} else if (ve instanceof CraftEssentialsEconomy) {
			return VoidMode.CRAFT_ESSENTIALS;
		}
		return VoidMode.CUSTOM;

	}

	public void hookVoidEcon() {

		final boolean debug = DebugUtil.isDebugEnabled();

		List<String> debugging = Lists.newArrayList();
		if (debug) {
			debugging.add("An attempt to hook into an available mode has been started..");
			debugging.add("");
		}

		final String input = FileManager.getInstance().getOptions().getFile().getString("Sell.mode");
		final Optional<VoidMode> mode = VoidMode.getByName(input);

		final VoidEconomy voidChestEconomy = new CraftVoidEconomy(this.core);

		final VoidChestAPI api = VoidChestAPI.getInstance();

		if (!mode.isPresent()) {
			VoidChestAPI.getInstance().hookVoidEconomy(voidChestEconomy, ServicePriority.Highest, this.core);
			if (debug) {
				debugging.add("No available mode found, defaulting to voidchest.");
				Utils.debug(this.core, debugging);
			}
			return;
		}

		final VoidMode voidMode = mode.get();

		if (debug) {
			debugging.add("Found an available mode! Hooking as: " + voidMode.getName());
			debugging.add("Details about the hook:");
		}

		VoidEconomy vec = null;

		switch (voidMode) {
		case CRAFT_VOIDCHEST:

			vec = voidChestEconomy;
			debugging.add("Name: " + vec.getName());
			api.hookVoidEconomy(voidChestEconomy, ServicePriority.Highest, this.core);

			return;
		case CRAFT_SHOPGUIPLUS:

			if (isPluginEnabled("ShopGUIPlus")) {
				vec = new CraftShopGUIPlusEconomy(this.core);
				debugging.add("Name: " + vec.getName());
				api.hookVoidEconomy(vec, ServicePriority.Highest, this.core);
			} else {
				debugging.add("Failed to hook to the plugin! Is it enabled?");
			}

			break;

		case CRAFT_ESSENTIALS:

			if (isPluginEnabled("Essentials")) {
				vec = new CraftEssentialsEconomy(this.core);
				debugging.add("Name: " + vec.getName());
				api.hookVoidEconomy(vec, ServicePriority.Highest, this.core);
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

		api.hookVoidEconomy(voidChestEconomy, ServicePriority.Low, this.core);

	}

	private boolean isPluginEnabled(final String pluginName) {
		final Plugin essp = Bukkit.getPluginManager().getPlugin(pluginName);
		return essp != null && essp.isEnabled();
	}

}
