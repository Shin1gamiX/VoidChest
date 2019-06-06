package me.shin1gamix.voidchest.listener;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidChestOption;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.events.VoidChestPlaceEvent;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.NBTEditorUtil;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public class VoidChestPlaceListener implements Listener {
	private final VoidChestPlugin core;

	public VoidChestPlaceListener(final VoidChestPlugin core) {
		this.core = core;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onPlace(final BlockPlaceEvent e) {

		final ItemStack item = e.getItemInHand();

		/* Is the item being hold a voidchest item? */
		if (!this.core.getVoidManager().isVoidItem(item)) {
			return;
		}

		final Player player = e.getPlayer();
		final PlayerData data = PlayerDataManager.getInstance().loadPlayerData(player);

		/* This is indeed a voidchest. */
		final String voidChestName = NBTEditorUtil.getItemTag(item, "voidKey").toString();
		final VoidStorage voidStorage = new VoidStorage(data, voidChestName, e.getBlock());

		/* Does the player have permission to place this voidchest? */
		if (!player.hasPermission(voidStorage.getPermissionPlace())) {
			e.setCancelled(true);
			MessagesUtil.NO_PERMISSION.msg(player);
			return;
		}

		/* Does the player have permission to place any further voidchests? */
		final int limit = FileManager.getInstance().getOptions().getFile().getInt("Player.voidchest.limit", 5);
		if (data.getVoidStorages().size() >= limit && !player.hasPermission("voidchest.limit.bypass")) {
			MessagesUtil.VOIDCHEST_LIMIT_REACHED.msg(player);
			e.setCancelled(true);
			return;
		}

		/* Setup options for the voidchest. */
		this.setupOptions(voidStorage);

		/* Let's call the place event on this voidchest. */
		final VoidChestPlaceEvent event = new VoidChestPlaceEvent(player, voidStorage);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			e.setCancelled(true);
			return;
		}

		/* Updating both the inventory and the hologram if available. */
		voidStorage.update();
		voidStorage.updateHologram();

		/* Add the voidchest to the playerdata. */
		data.getVoidStorages().add(voidStorage);

		/* Attempt to play the place sound. */
		final String soundInput = FileManager.getInstance().getOptions().getFile().getString("Sounds.voidchest-place",
				"LEVEL_UP");
		final Optional<Sound> soundOptional = SoundUtil.bukkitSound(soundInput);
		if (soundOptional.isPresent()) {
			player.playSound(player.getLocation(), soundOptional.get(), 1, 1);
		}

		/* Try and send the place message. */
		if (FileManager.getInstance().getOptions().getFile().getBoolean("Messages.voidchest-place.enabled", false)) {
			Utils.msg(player, FileManager.getInstance().getOptions().getFile(), "Messages.voidchest-place.message");
		}

	}

	private void setupOptions(final VoidStorage voidStorage) {
		voidStorage.setAutoSell(VoidChestOption.AUTOSELL.getDefault(voidStorage));
		voidStorage.setPurgeInvalidItems(VoidChestOption.PURGE.getDefault(voidStorage));
		voidStorage.setHologramActivated(VoidChestOption.HOLOGRAM.getDefault(voidStorage));
		voidStorage.setCreationTime(System.currentTimeMillis());
	}

}
