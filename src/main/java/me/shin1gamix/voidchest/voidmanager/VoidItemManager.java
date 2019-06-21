package me.shin1gamix.voidchest.voidmanager;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.nbtapi.NBTItem;
import me.shin1gamix.voidchest.utilities.MaterialUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public final class VoidItemManager {

	private VoidItemManager() {
	}

	private static VoidItemManager instance;

	public static VoidItemManager getInstance() {
		return instance == null ? instance = new VoidItemManager() : instance;
	}

	private final Map<String, VoidChestItemCache> itemCacheMap = Maps.newHashMap();

	public Map<String, VoidChestItemCache> getItemCacheMap() {
		return itemCacheMap;
	}

	public static boolean isVoidItem(final ItemStack item) {

		if (item == null || item.getType() != Material.CHEST) {
			return false;
		}

		final NBTItem nbti = NBTItem.of(item);

		if (!nbti.hasKey("voidKey")) {
			return false;
		}

		final String result = nbti.getString("voidKey");

		final VoidItemManager vim = VoidItemManager.getInstance();
		return vim.getCachedItem(result).isPresent();
	}

	public void cacheItems() {
		this.itemCacheMap.clear();
		final FileManager fm = FileManager.getInstance();
		final FileConfiguration voidInventory = fm.getVoidInventory().getFile();

		final ConfigurationSection sect = voidInventory.getConfigurationSection("VoidChests");
		if (sect == null) {
			return;
		}

		for (final String vcName : sect.getKeys(false)) {
			/* Duplicate names but with different capitalization? */
			if (this.getCachedItem(vcName).isPresent()) {
				continue;
			}
			this.itemCacheMap.put(vcName, new VoidChestItemCache(vcName));
		}

	}

	/*
	 * Returns a cached or attempts to cache a possible ItemCache object from a
	 * specific key (if input doesn't exist but is valid, it caches it).
	 */
	@Nonnull
	public Optional<VoidChestItemCache> getCachedItem(final String name) {
		VoidChestItemCache cache = itemCacheMap.get(name);
		if (cache != null) {
			return Optional.of(cache);
		}
		for (Entry<String, VoidChestItemCache> entry : itemCacheMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return Optional.of(entry.getValue());
			}
		}
		return Optional.empty();
	}

	public class VoidChestItemCache {

		private final String name;

		public VoidChestItemCache(final String name) {
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object object) {
			return (this == object) || (object instanceof VoidChestItemCache
					&& Objects.equals(name, ((VoidChestItemCache) object).name));
		}

		private ItemStack item = null;

		public ItemStack getVoidChestItem() {
			if (this.item == null) {
				final FileManager fm = FileManager.getInstance();
				final FileConfiguration voidInventory = fm.getVoidInventory().getFile();

				NBTItem nbti = NBTItem.of(MaterialUtil.CHEST.parseItem());
				nbti.setString("voidKey", this.name);
				item = nbti.getItem();

				final ItemMeta meta = item.getItemMeta();
				Map<String, String> map = Maps.newHashMap();
				map.put("%voidchest%", this.name);

				final String dname = Utils.colorize(
						voidInventory.getString("VoidChests." + this.name + ".Storage.name", "Default VoidChest"));
				meta.setDisplayName(Utils.placeHolder(dname, map, false));

				final List<String> lore = Utils
						.colorize(voidInventory.getStringList("VoidChests." + this.name + ".Storage.lore"));
				meta.setLore(Utils.placeHolder(lore, map, false));
				item.setItemMeta(meta);
			}
			return item.clone();
		}

		public ItemStack[] getVoidChestItem(int amount) {
			return Utils.getItems(this.getVoidChestItem(), amount);
		}

		public String getName() {
			return name;
		}

		private VoidItemManager getOuterType() {
			return VoidItemManager.this;
		}
	}

}
