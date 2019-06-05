package me.shin1gamix.voidchest.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidSellChestEvent extends Event implements Cancellable {
	private final VoidStorage voidStorage;
	private boolean cancel;

	public VoidSellChestEvent(final VoidStorage voidStorage) {
		this.voidStorage = voidStorage;
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

	public VoidStorage getVoidStorage() {
		return voidStorage;
	}

}