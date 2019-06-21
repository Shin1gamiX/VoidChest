package me.shin1gamix.voidchest.tasks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class SaveTask extends BukkitRunnable {
	private final VoidChestPlugin core;

	public SaveTask(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void run() {
		final PlayerDataManager pdm = PlayerDataManager.getInstance();
		FileConfiguration file = FileManager.getInstance().getPlayerBase().getFile();

		List<PlayerData> copyData = Lists.newArrayList(pdm.getPlayerDatas().values());

		for (final PlayerData data : copyData) {

			String name = data.getName();

			file.set("Players." + name, null); // Entirely delete the specific path.

			ConfigurationSection sect = file.createSection("Players." + name);

			sect.set("Players." + name + ".uuid", data.getOwner().getUniqueId().toString());
			sect.set("booster.multiplier", data.getBooster());
			sect.set("booster.time", data.getBoostTime());

			/* Copying the voidstorages colletion and passing it into batches. */
			List<List<VoidStorage>> batches = Lists.partition(Lists.newArrayList(data.getVoidStorages()), 40);
			this.saveChestsPartially(name, batches, file);

		}

	}

	private void saveFileAsync() {
		Bukkit.getScheduler().runTaskAsynchronously(this.core, FileManager.getInstance().getPlayerBase()::saveFile);
	}

	private void saveChestsPartially(String name, List<List<VoidStorage>> batches, final FileConfiguration file) {

		new BukkitRunnable() {
			int i = 0;

			@Override
			public void run() {

				if (batches.isEmpty()) {
					FileManager.getInstance().getPlayerBase().saveFile();
					this.cancel();
					return;
				}

				final List<VoidStorage> toSave = batches.get(0);
				for (VoidStorage chest : toSave) {

					if (!file.isSet("Players." + name + ".chests." + i)) {
						file.createSection("Players." + name + ".chests." + i);
					}

					final ConfigurationSection sect = file.getConfigurationSection("Players." + name + ".chests." + i);
					sect.set("name", chest.getName());
					sect.set("location", chest.getLocation());
					sect.set("money", chest.getMoney());
					sect.set("items-sold-amount", chest.getItemsSold());
					sect.set("items-purged-amount", chest.getItemsPurged());
					sect.set("autosell", chest.isAutoSell());
					sect.set("purge-items", chest.isPurgeInvalidItems());
					sect.set("hologram", chest.isHologramActivated());
					sect.set("creation-time", chest.getCreationTime());
					saveChestsInFile(chest, sect);
					++i;
				}

			}
		}.runTaskTimer(this.core, 2l, 2l);
	}

	private void saveChestsInFile(final VoidStorage chest, final ConfigurationSection sect) {
		final Inventory custInv = chest.getCustomInventory();
		for (int x = 0; x < custInv.getSize(); x++) {
			final ItemStack item = custInv.getItem(x);
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			sect.set("items." + x, item);
		}
	}
}
