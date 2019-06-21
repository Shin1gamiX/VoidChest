package me.shin1gamix.voidchest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidChestPlaceEvent extends VoidEvent {

	private final Player player;
	private final ItemStack item;

	public VoidChestPlaceEvent(final Player player, final ItemStack item, final VoidStorage voidStorage) {
		super(voidStorage);
		this.player = player;
		this.item = item;
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

	public ItemStack getItem() {
		return item;
	}

}