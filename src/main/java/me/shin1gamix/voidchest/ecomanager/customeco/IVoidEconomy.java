package me.shin1gamix.voidchest.ecomanager.customeco;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.ecomanager.customeco.copy.CraftEconomyAbstract;
import me.shin1gamix.voidchest.utilities.MaterialUtil;

public final class IVoidEconomy extends CraftEconomyAbstract {

	public IVoidEconomy(final VoidChestPlugin core) {
		super(core, true, core.getDescription());
	}

	@Override
	public double getProfit(ItemStack item) {

		FileManager fm = FileManager.getInstance();

		Preconditions.checkNotNull(item);
		final FileConfiguration file = fm.getShop().getFile();

		double price = 0d;

		for (String path : file.getConfigurationSection("Items").getKeys(false)) {
			final Optional<MaterialUtil> inputOpt = MaterialUtil.fromString(path);

			if (!inputOpt.isPresent()) {
				continue;
			}

			ItemStack stack = inputOpt.get().parseItem();

			final boolean ignoreMeta = fm.getOptions().getFile().getBoolean("Sell.VoidChest.ignore-item-meta", false);

			if (ignoreMeta) {
				item = new ItemStack(item);
				item.setItemMeta(Bukkit.getItemFactory().getItemMeta(item.getType()));
			}

			if (!item.isSimilar(stack)) {
				continue;
			}

			price = file.getDouble("Items." + path) * item.getAmount();
			break;
		}
		return price > 0 ? price : 0;
	}

}
