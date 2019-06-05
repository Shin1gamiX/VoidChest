package me.shin1gamix.voidchest.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidSellEvent extends Event implements Cancellable {
	private final ItemStack item;
	private final VoidStorage voidStorage;
	private double price;
	private boolean cancel;

	public VoidSellEvent(final VoidStorage voidStorage, final ItemStack item, final double price) {
		this.item = item;
		this.voidStorage = voidStorage;
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

	public VoidStorage getVoidStorage() {
		return voidStorage;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}