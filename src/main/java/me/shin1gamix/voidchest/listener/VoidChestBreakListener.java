package me.shin1gamix.voidchest.listener;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.events.VoidChestBreakEvent;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidItemManager;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidItemManager.VoidChestItemCache;

public class VoidChestBreakListener implements Listener {
	private final VoidChestPlugin core;

	public VoidChestBreakListener(final VoidChestPlugin core) {
		this.core = core;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onBreak(final BlockBreakEvent e) {

		final Block block = e.getBlock();

		/* Not a chest? Then why even bother? */
		if (!this.core.getVoidManager().isChest(block)) {
			return;
		}

		/* Is this chest a voidchest? */
		final Optional<VoidStorage> voidStorageOptional = this.core.getVoidManager().getVoidStorage(block);
		if (!voidStorageOptional.isPresent()) {
			return;
		}

		/* This is indeed a voidchest so let's stop the block from being broken. */
		e.setCancelled(true);

		final VoidStorage voidStorage = voidStorageOptional.get();

		/* Does the player have the right to break this voidchest? */
		final Player player = e.getPlayer();
		if (!player.hasPermission(voidStorage.getPermissionBreak())) {
			MessagesUtil.NO_PERMISSION.msg(player);
			return;
		}

		final Map<String, String> map = Maps.newHashMap();

		/* Does this voidchest not belong to the one breaking it? */
		if (!voidStorage.getPlayerData().getName().equals(e.getPlayer().getName())
				&& !player.hasPermission("voidchest.break.bypass")) {

			/* Let's send them a message saying they can't break this voidchest. */
			if (FileManager.getInstance().getOptions().getFile().getBoolean("Messages.voidchest-break-fail.enabled",
					false)) {
				map.put("%player%", voidStorage.getPlayerData().getName());
				Utils.msg(player, FileManager.getInstance().getOptions().getFile(),
						"Messages.voidchest-break-fail.message", map, false);
			}

			return;
		}

		/* Let's call the break event on this voidchest. */
		final VoidChestBreakEvent event = new VoidChestBreakEvent(player, voidStorage);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		/* Let's remove data related to this voidchest. */
		this.removeVoidChest(player, voidStorage);

	}

	private void removeVoidChest(Player player, VoidStorage voidStorage) {

		final PlayerData data = voidStorage.getPlayerData();

		data.getVoidStorages().remove(voidStorage);

		final Inventory blockInventory = voidStorage.getBlockInventory();
		final Inventory customInventory = voidStorage.getCustomInventory();
		final ItemStack[] blockItems = blockInventory.getContents();
		final ItemStack[] customItems = customInventory.getContents();
		blockInventory.clear();
		customInventory.clear();
		Arrays.stream(customItems).filter(item -> item != null && item.getType() != Material.AIR).forEach(item -> {
			voidStorage.getLocation().getWorld().dropItemNaturally(voidStorage.getLocation(), item);
		});
		Arrays.stream(blockItems).filter(item -> item != null && item.getType() != Material.AIR).forEach(item -> {
			voidStorage.getLocation().getWorld().dropItemNaturally(voidStorage.getLocation(), item);
		});

		final Block blockbr = voidStorage.getBlock();
		blockbr.setType(Material.AIR);

		/* Not sure if necessary. */
		blockbr.getState().update(true, true);

		/*
		 * Let's drop this voidchest item in the ground if the player is not in creative
		 * mode.
		 */
		if (player.getGameMode() != GameMode.CREATIVE) {
			final Location loc = voidStorage.getLocation().add(0.5, 0, 0.5);
			final Optional<VoidChestItemCache> cacheOpt = VoidItemManager.getInstance()
					.getCachedItem(voidStorage.getName());
			cacheOpt.ifPresent(cache -> loc.getWorld().dropItemNaturally(loc, cache.getVoidChestItem()));
		}

		voidStorage.closeInventories();
		String soundInput = FileManager.getInstance().getOptions().getFile().getString("Sounds.voidchest-break",
				"ANVIL_BREAK");

		final Optional<Sound> soundOptional = SoundUtil.bukkitSound(soundInput);
		soundOptional.ifPresent(sound -> player.playSound(player.getLocation(), sound, 1, 1));

		if (FileManager.getInstance().getOptions().getFile().getBoolean("Messages.voidchest-break.enabled", false)) {
			Utils.msg(player, FileManager.getInstance().getOptions().getFile(), "Messages.voidchest-break.message");
		}

		voidStorage.setHologramActivated(false);
		voidStorage.updateHologram();

	}
}
