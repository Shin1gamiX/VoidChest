package me.shin1gamix.voidchest.listener;

import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.data.customchest.options.VoidChestOption;
import me.shin1gamix.voidchest.events.VoidChestPlaceEvent;
import me.shin1gamix.voidchest.nbtapi.NBTItem;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;
import me.shin1gamix.voidchest.voidmanager.VoidItemManager;

public class VoidChestPlaceListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onPlace(final BlockPlaceEvent e) {

		final ItemStack item = e.getItemInHand();

		/* Is the item being hold a voidchest item? */
		if (!VoidItemManager.isVoidItem(item)) {
			return;
		}

		final Player player = e.getPlayer();
		final PlayerData data = PlayerDataManager.getInstance().loadPlayerData(player, true, false);

		/* This is indeed a voidchest. */
		final NBTItem nbti = NBTItem.of(item);
		final String voidChestName = nbti.getString("voidKey");
		final VoidStorage voidStorage = new VoidStorage(data, voidChestName, e.getBlock());

		/* Does the player have permission to place this voidchest? */
		if (!player.hasPermission(voidStorage.getPermissionPlace())) {
			e.setCancelled(true);
			Map<String, String> map = Maps.newHashMap();
			map.put("%voidchest%", voidStorage.getName());
			final FileManager fm = FileManager.getInstance();
			FileConfiguration voidFile = fm.getVoidInventory().getFile();
			Utils.msg(player, voidFile, "VoidChests." + voidStorage.getName() + ".Permissions.place.message", map,
					false);
			return;
		}

		/* Does the player have permission to place any further voidchests? */

		final int limit = this.getPlaceLimit(player);
		if (data.getVoidStorages().size() >= limit && !player.hasPermission("voidchest.limit.bypass")) {
			MessagesUtil.VOIDCHEST_LIMIT_REACHED.msg(player);
			e.setCancelled(true);
			return;
		}

		/* Setup options for the voidchest. */
		this.setupOptions(voidStorage);

		/* Let's call the place event on this voidchest. */
		final VoidChestPlaceEvent event = new VoidChestPlaceEvent(player, item, voidStorage);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			e.setCancelled(true);
			return;
		}

		/* Updating both the inventory and the hologram if available. */
		voidStorage.update();

		/* Add the voidchest to the playerdata. */
		data.getVoidStorages().add(voidStorage);

		/* Attempt to play the place sound. */
		final String soundInput = FileManager.getInstance().getOptions().getFile().getString("Sounds.voidchest-place",
				"LEVEL_UP");
		final Optional<Sound> soundOptional = SoundUtil.bukkitSound(soundInput);
		soundOptional.ifPresent(sound -> player.playSound(player.getLocation(), sound, 1, 1));

		/* Try and send the place message. */
		if (FileManager.getInstance().getOptions().getFile().getBoolean("Messages.voidchest-place.enabled", false)) {
			Utils.msg(player, FileManager.getInstance().getOptions().getFile(), "Messages.voidchest-place.message");
		}

	}

	private int getPlaceLimit(final Player player) {

		final ConfigurationSection sect = FileManager.getInstance().getOptions().getFile()
				.getConfigurationSection("Player.voidchest.limit");

		if (sect == null) {
			return 5;
		}

		boolean detected = false;

		int resultAmount = 0;

		for (String name : sect.getKeys(false)) {
			final int amount = sect.getInt(name);
			if (resultAmount >= amount) {
				continue;
			}

			if (name.equalsIgnoreCase("default")) {
				if (!detected) {
					resultAmount = amount;
					detected = true;
				}
				continue;
			}

			if (player.hasPermission("voidchest.limit." + name)) {
				resultAmount = amount;
			}
		}

		return resultAmount <= 0 ? 5 : resultAmount;

	}

	private void setupOptions(final VoidStorage voidStorage) {
		voidStorage.setAutoSell(VoidChestOption.AUTOSELL.getDefault(voidStorage));
		voidStorage.setPurgeInvalidItems(VoidChestOption.PURGE.getDefault(voidStorage));
		voidStorage.setHologramActivated(VoidChestOption.HOLOGRAM.getDefault(voidStorage));
		voidStorage.setCreationTime(System.currentTimeMillis());
	}

}
