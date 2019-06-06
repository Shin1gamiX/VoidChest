package me.shin1gamix.voidchest.events;

import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidSellChestEvent extends VoidEvent {

	public VoidSellChestEvent(final VoidStorage voidStorage) {
		super(voidStorage);
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}