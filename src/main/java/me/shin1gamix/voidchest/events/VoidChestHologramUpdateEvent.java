package me.shin1gamix.voidchest.events;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

/**
 * 
 * This event is fired before a voidchest's hologram is replaced. Modifying the
 * lines through this event may cause unexpected behavior.
 * 
 * @see VoidStorage#updateHologram()
 * 
 */
public class VoidChestHologramUpdateEvent extends Event {

	private final VoidStorage voidStorage;
	private final List<String> lines;

	public VoidChestHologramUpdateEvent(final VoidStorage voidStorage, final List<String> lines) {
		this.voidStorage = voidStorage;
		this.lines = lines;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public VoidStorage getVoidStorage() {
		return voidStorage;
	}

	public List<String> getLines() {
		return lines;
	}

}