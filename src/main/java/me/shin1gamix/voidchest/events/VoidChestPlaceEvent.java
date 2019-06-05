package me.shin1gamix.voidchest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidChestPlaceEvent extends Event implements Cancellable {

	private final VoidStorage voidStorage;
	private final Player player;
	private boolean cancel;

	public VoidChestPlaceEvent(final Player player, final VoidStorage voidStorage) {
		this.player = player;
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

	public Player getPlayer() {
		return player;
	}

}