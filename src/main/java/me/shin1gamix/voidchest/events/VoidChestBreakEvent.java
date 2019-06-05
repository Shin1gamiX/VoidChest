package me.shin1gamix.voidchest.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidChestBreakEvent extends Event implements Cancellable {

	private final VoidStorage cvc;
	private final Entity entity;
	private boolean cancel;

	public VoidChestBreakEvent(final Entity entity, final VoidStorage cvc) {
		this.entity = entity;
		this.cvc = cvc;
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
		return this.cvc;
	}

	public Entity getEntity() {
		return entity;
	}

}