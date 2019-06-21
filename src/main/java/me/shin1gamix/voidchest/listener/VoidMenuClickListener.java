package me.shin1gamix.voidchest.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.data.customchest.items.VoidIcon;
import me.shin1gamix.voidchest.data.customchest.options.VoidStorageMenu;
import me.shin1gamix.voidchest.events.VoidItemMenuClickEvent;

public class VoidMenuClickListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onClick(final InventoryClickEvent e) {

		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}

		final ItemStack item = e.getCurrentItem();

		if (item == null || item.getType() == Material.AIR) {
			return;
		}

		final Inventory inv = e.getInventory();

		/* Is the inventory not a voidchest menu inventory? */
		if (!(inv.getHolder() instanceof VoidStorageMenu)) {
			return;
		}

		/* Let's cache the inventory and use that. */
		final VoidStorageMenu voidMenu = (VoidStorageMenu) inv.getHolder();
		final VoidStorage voidStorage = voidMenu.getStorage();

		e.setCancelled(true);
		final Player player = (Player) e.getWhoClicked();

		/* Get the appropriate void icon and execute it's task if available. */
		final VoidIcon clicked = voidStorage.getVoidItems().get(e.getRawSlot());
		if (clicked == null) {
			return;
		}

		final VoidItemMenuClickEvent event = new VoidItemMenuClickEvent(player, clicked.clone());
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		event.getVoidItem().execute(player);
	}

}
