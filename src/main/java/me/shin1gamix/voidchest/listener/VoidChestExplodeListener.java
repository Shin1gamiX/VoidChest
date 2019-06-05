package me.shin1gamix.voidchest.listener;

import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.google.common.collect.Sets;

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

		Set<Block> blocksRemovall = Sets.newHashSet();
		for (Block block : e.blockList()) {

			if (!this.core.getVoidManager().isChest(block)) {
				continue;
			}
			blocksRemovall.add(block);
		}

		for (final Block block : blocksRemovall) {

			final Optional<VoidStorage> voidStorageOpt = this.core.getVoidManager().getVoidStorage(block);
			if (!voidStorageOpt.isPresent()) {
				continue;
			}

			e.blockList().remove(block);

			/* Let's call the break event on this voidchest. */
			final VoidChestBreakEvent event = new VoidChestBreakEvent(e.getEntity(), voidStorageOpt.get());
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				continue;
			}

			VoidStorage voidStorage = event.getVoidStorage();
			block.setType(Material.AIR);
			/* Not sure if necessary. */
			block.getState().update(true, true);
			voidStorage.setHologramActivated(false);
			voidStorage.updateHologram();
			voidStorage.closeInventories();
			voidStorage.getPlayerData().getVoidStorages().remove(voidStorage);

		}

	}

}
