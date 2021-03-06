package me.shin1gamix.voidchest.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.listener.VoidChestBreakListener;

/**
 * 
 * This event is fired before a voidchest is fully removed by being broken by an
 * Entity. Entity can either be a player or something else such as an explosion.
 * 
 * @see VoidChestBreakListener
 * 
 */
public class VoidChestBreakEvent extends VoidEvent {

	private final Entity entity;

	public VoidChestBreakEvent(final Entity entity, final VoidStorage voidStorage) {
		super(voidStorage);
		this.entity = entity;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Entity getEntity() {
		return entity;
	}

}