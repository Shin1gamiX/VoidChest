package me.shin1gamix.voidchest.ecomanager.customeco;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.Essentials;
import com.google.common.base.Preconditions;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.ecomanager.customeco.copy.CraftEconomyAbstract;

public class IEssentialsEconomy extends CraftEconomyAbstract {

	private final Essentials api;

	public IEssentialsEconomy(final VoidChestPlugin core) {
		super(core, true, Bukkit.getPluginManager().getPlugin("Essentials").getDescription());
		this.api = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	@Override
	public double getProfit(ItemStack item) {
		Preconditions.checkNotNull(item);

		final boolean ignoreMeta = FileManager.getInstance().getOptions().getFile()
				.getBoolean("Sell.Essentials.ignore-item-meta", false);

		if (!ignoreMeta && item.hasItemMeta()) {
			return 0;
		}

		BigDecimal resultPrice = this.api.getWorth().getPrice(this.api, item);
		if (resultPrice == null) {
			return 0;
		}

		double price = resultPrice.doubleValue() * item.getAmount();
		return price > 0 ? price : 0;
	}

}
