package me.shin1gamix.voidchest.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.shin1gamix.voidchest.data.customchest.items.VoidIcon;

public class VoidItemMenuClickEvent extends Event implements Cancellable {
	private boolean cancel;

	private final VoidIcon voidItem;
	private final Player player;

	public VoidItemMenuClickEvent(final Player player, final VoidIcon voidItem) {
		this.voidItem = voidItem;
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

	@Override
	public boolean isCancelled() {
		return this.cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public VoidIcon getVoidItem() {
		return voidItem;
	}

	public Player getPlayer() {
		return player;
	}

}