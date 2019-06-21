package me.shin1gamix.voidchest.data.customchest.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.data.customchest.items.VoidIcon;

public class VoidIconHologramToggle extends VoidIcon {

	public VoidIconHologramToggle(final VoidStorage voidStorage, final ItemStack item, final int slot) {
		super(voidStorage, item, slot);
	}

	@Override
	public void execute(Player player) {
		if (super.isCloseInventory()) {
			player.closeInventory();
		}
		final VoidStorage voidStorage = super.getVoidStorage();
		voidStorage.setHologramActivated(!voidStorage.isHologramActivated());
		voidStorage.update();
	}

}
