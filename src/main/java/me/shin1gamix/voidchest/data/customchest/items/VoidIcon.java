package me.shin1gamix.voidchest.data.customchest.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public abstract class VoidIcon implements Cloneable {

	private final ItemStack item;
	private final int slot;
	private boolean closeInventory;
	private final VoidStorage voidStorage;

	public VoidIcon(final VoidStorage voidStorage, final ItemStack item, final int slot) {
		this.voidStorage = voidStorage;
		this.item = item;
		this.slot = slot;
	}

	public abstract void execute(Player player);

	public ItemStack getItem() {
		return item;
	}

	public int getSlot() {
		return Math.abs(this.slot);
	}

	public boolean isCloseInventory() {
		return closeInventory;
	}

	public void setCloseInventory(boolean closeInventory) {
		this.closeInventory = closeInventory;
	}

	public VoidStorage getVoidStorage() {
		return this.voidStorage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.slot;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VoidIcon other = (VoidIcon) obj;
		return this.slot == other.slot;
	}

	@Override
	public VoidIcon clone() {
		try {
			return (VoidIcon) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}
