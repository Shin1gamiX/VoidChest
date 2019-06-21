package me.shin1gamix.voidchest.events;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidSellItemEvent extends VoidEvent {
	private final ItemStack item;

	private double price;
	private boolean cancel;

	public VoidSellItemEvent(final VoidStorage voidStorage, final ItemStack item, final double price) {
		super(voidStorage);
		this.item = item;
		this.price = price;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public ItemStack getItem() {
		return item;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}