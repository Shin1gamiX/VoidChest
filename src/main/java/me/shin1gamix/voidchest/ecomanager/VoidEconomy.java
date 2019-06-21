package me.shin1gamix.voidchest.ecomanager;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.PlayerData;

public interface VoidEconomy {

	void initiateSell(final PlayerData data);

	double getProfit(final ItemStack item);

	String getName();

	boolean isVaultDependent();

	public default ItemStack resetItem(final ItemStack item) {
		final ItemStack copy = new ItemStack(item);
		copy.setItemMeta(Bukkit.getItemFactory().getItemMeta(copy.getType()));
		return copy;
	}
}
