package me.shin1gamix.voidchest.listener;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.data.customchest.items.VoidIcon;
import me.shin1gamix.voidchest.events.VoidItemMenuClickEvent;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidManager.VoidInventoryType;

public class VoidMenuClickListener implements Listener {
	private final VoidChestPlugin core;

	public VoidMenuClickListener(final VoidChestPlugin core) {
		this.core = core;
	}

	@EventHandler(ignoreCancelled = true)
	private void onClick(final InventoryClickEvent e) {

		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}

		final ItemStack item = e.getCurrentItem();

		if (item == null) {
			return;
		}

		final Optional<VoidStorage> opt = this.core.getVoidManager().getVoidStorage(VoidInventoryType.MENU,
				e.getInventory());

		if (!opt.isPresent()) {
			return;
		}

		e.setCancelled(true);

		final VoidStorage chest = opt.get();

		final Player player = (Player) e.getWhoClicked();
		final VoidIcon clicked = chest.getVoidItems().get(e.getRawSlot());
		
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
