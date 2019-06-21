package me.shin1gamix.voidchest.data.customchest.items;

import java.util.Objects;

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

	protected ItemStack getItem() {
		return item;
	}

	protected int getSlot() {
		return Math.abs(this.slot);
	}

	protected boolean isCloseInventory() {
		return closeInventory;
	}

	protected void setCloseInventory(boolean closeInventory) {
		this.closeInventory = closeInventory;
	}

	protected VoidStorage getVoidStorage() {
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
		return this == obj || (obj instanceof VoidIcon && Objects.equals(this.slot, ((VoidIcon) obj).getSlot()));
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
