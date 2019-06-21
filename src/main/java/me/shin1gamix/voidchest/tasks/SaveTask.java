package me.shin1gamix.voidchest.tasks;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class SaveTask extends BukkitRunnable {
	public Map<String, BukkitTask> getPartitionTasks() {
		return partitionTasks;
	}

	private final VoidChestPlugin core;

	public SaveTask(final VoidChestPlugin core) {
		this.core = core;
	}

	private Map<String, BukkitTask> partitionTasks = Maps.newHashMap();

	private final Set<PlayerData> saving = Sets.newHashSet();

	@Override
	public void run() {

		final PlayerDataManager pdm = PlayerDataManager.getInstance();
		FileConfiguration file = FileManager.getInstance().getPlayerBase().getFile();

		List<PlayerData> copyData = Lists.newArrayList(pdm.getPlayerDatas().values());

		final int maxSize = copyData.size();

		for (final PlayerData data : copyData) {

			if (!saving.add(data)) {
				continue;
			}

			String name = data.getName();

			file.set("Players." + name, null); // Entirely delete the specific path.

			ConfigurationSection sect = file.createSection("Players." + name);

			sect.set("uuid", data.getOwner().getUniqueId().toString());
			sect.set("booster.multiplier", data.getBooster());
			sect.set("booster.time", data.getBoostTime());

			/* Copying the voidstorages colletion and passing it into batches. */
			List<List<VoidStorage>> batches = Lists.partition(data.getVoidStorages(), 20);
			this.saveChestsPartially(data, batches, file, maxSize);

		}

	}

	private int count = 0;

	private void saveChestsPartially(PlayerData data, List<List<VoidStorage>> batches, final FileConfiguration file,
			final int maxSize) {

		final String name = data.getName();

		BukkitTask task = new BukkitRunnable() {
			int i = 0;
			int element = 0;

			@Override
			public void run() {

				if (element == batches.size()) {

					if (++count == maxSize) {
						count = 0;
						FileManager.getInstance().getPlayerBase().saveFile();
						saving.clear();
					}

					if (VoidChestPlugin.isDebugEnabled()) {
						System.out.println("Finished saving for: " + name);
					}
					partitionTasks.remove(name);
					this.cancel();
					return;
				}

				final List<VoidStorage> toSave = batches.get(element++);
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
		}.runTaskTimer(this.core, 1l, 4l);

		partitionTasks.put(name, task);

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
