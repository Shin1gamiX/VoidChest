package me.shin1gamix.voidchest.runnables;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.utilities.DebugUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public class PurgeTask extends BukkitRunnable {

	private final VoidChestPlugin core;

	public PurgeTask(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void run() {

		final List<TempCache> temp = Lists.newArrayList();

		for (final PlayerData data : core.getPlayerDataManager().getPlayerDatas().values()) {

			/* Creating a temporary cache for storing data so as to remove things later. */
			final TempCache tempCache = new TempCache(data);

			for (final VoidStorage storage : data.getVoidStorages()) {

				/* Getting all storages, checking if not a chest and adding for removal. */
				if (!core.getVoidManager().isChest(storage.getBlock())) {
					tempCache.getStorage().add(storage);
				}
			}

			/* No voidStorage for removall. */
			if (tempCache.getStorage().isEmpty()) {
				continue;
			}

			temp.add(tempCache);
		}

		/* No removal will be happening. */
		if (temp.isEmpty()) {
			return;
		}

		final List<String> debugging = Lists.newArrayList();

		final boolean debug = DebugUtil.isDebugEnabled();

		if (debug) {
			debugging.add("A list of purged void storages is listed below.");
			debugging.add(" ");
		}

		temp.forEach(tempx -> {

			final Set<String> locations = Sets.newHashSet();
			tempx.getStorage().forEach(storage -> {

				if (debug) {
					final Location loc = storage.getLocation();
					locations.add(loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":"
							+ loc.getBlockZ());
				}

				if (this.core.isHdSupport()) {
					storage.getHolo().delete();
				}

			});

			if (debug) {
				debugging.add("Username: " + tempx.getData().getName());
				debugging.add("UUID: " + tempx.getData().getOwner().getUniqueId());
				debugging.add("Purged voidstorages " + locations.size() + ": "
						+ locations.stream().collect(Collectors.joining(" | ")));
				debugging.add(" ");
			}

			tempx.getData().getVoidStorages().removeAll(tempx.getStorage());
		});

		if (debug) {
			Utils.debug(this.core, debugging);
		}

	}

	private class TempCache {

		private final PlayerData data;
		private final Set<VoidStorage> storage = Sets.newHashSet();

		public TempCache(final PlayerData data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TempCache other = (TempCache) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (data == null) {
				if (other.data != null)
					return false;
			} else if (!data.equals(other.data))
				return false;
			return true;
		}

		public Set<VoidStorage> getStorage() {
			return storage;
		}

		public PlayerData getData() {
			return data;
		}

		private PurgeTask getOuterType() {
			return PurgeTask.this;
		}
	}
}
