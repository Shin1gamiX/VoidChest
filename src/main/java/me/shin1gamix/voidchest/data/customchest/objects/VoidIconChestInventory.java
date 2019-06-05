package me.shin1gamix.voidchest.data.customchest.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.data.customchest.items.VoidIcon;

public class VoidIconChestInventory extends VoidIcon {

	public VoidIconChestInventory(final VoidStorage voidStorage, final ItemStack item, final int slot) {
		super(voidStorage, item, slot);
	}

	@Override
	public void execute(Player player) {
		/*
		 * We will not be closing the player's inventory since we'll be opening another
		 * one. No reason to close it.
		 */
		final VoidStorage voidStorage = super.getVoidStorage();
		player.openInventory(voidStorage.getBlockInventory());
	}

}
