package me.shin1gamix.voidchest.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class InventoryInteractListener implements Listener {
	private final VoidChestPlugin core;

	public InventoryInteractListener(final VoidChestPlugin core) {
		this.core = core;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onInteract(final PlayerInteractEvent e) {

		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		final Player p = e.getPlayer();

		final Block block = e.getClickedBlock();
		if (block.getType() != Material.CHEST) {
			return;
		}

		final VoidStorage voidStorage = this.core.getVoidManager().getVoidStorage(block);
		if (voidStorage == null) {
			return;
		}

		/* The player is sneaking, allow them to open the chest's inventory? */
		if (p.isSneaking()) {

			final ItemStack item = e.getItem();
			if (item != null && item.getType().isBlock()) {
				return;
			}

			if (!FileManager.getInstance().getVoidInventory().getFile()
					.getBoolean("VoidChests." + voidStorage.getName() + ".Shift-click-open-chest", true)) {
				e.setCancelled(true);
			}

			return;
		}

		e.setCancelled(true);
		final Inventory inv = voidStorage.getMenuInventory();
		p.openInventory(inv);

	}

}
