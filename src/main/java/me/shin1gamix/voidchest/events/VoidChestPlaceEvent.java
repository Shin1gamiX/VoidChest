package me.shin1gamix.voidchest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidChestPlaceEvent extends VoidEvent {

	private final Player player;

	public VoidChestPlaceEvent(final Player player, final VoidStorage voidStorage) {
		super(voidStorage);
		this.player = player;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Player getPlayer() {
		return player;
	}

}