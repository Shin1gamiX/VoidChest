package me.shin1gamix.voidchest.listener;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.events.VoidChestBreakEvent;

public class VoidChestExplodeListener implements Listener {
	private final VoidChestPlugin core;

	public VoidChestExplodeListener(final VoidChestPlugin core) {
		this.core = core;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onInteract(final EntityExplodeEvent e) {

		Iterator<Block> itr = e.blockList().iterator();
		while (itr.hasNext()) {
			Block next = itr.next();

			if (next.getType() != Material.CHEST) {
				continue;
			}

			final VoidStorage voidStorage = this.core.getVoidManager().getVoidStorage(next);
			if (voidStorage == null) {
				continue;
			}

			itr.remove();

			/* Let's call the break event on this voidchest. */
			final VoidChestBreakEvent event = new VoidChestBreakEvent(e.getEntity(), voidStorage);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				continue;
			}

			VoidStorage voidStorageEvent = event.getVoidStorage();
			next.setType(Material.AIR);
			/* Not sure if necessary. */
			next.getState().update(true, true);
			voidStorageEvent.setHologramActivated(false);
			voidStorageEvent.updateHologram();
			voidStorageEvent.closeInventories();
			voidStorageEvent.getPlayerData().getVoidStorages().remove(voidStorageEvent);
		}

	}

}
