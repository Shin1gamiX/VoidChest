package me.shin1gamix.voidchest.utilities.voidmanager;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.utilities.NBTEditorUtil;

public class VoidManager {

	public VoidManager() {
	}

	public boolean isVoidItem(final ItemStack item) {

		if (item == null || item.getType() != Material.CHEST) {
			return false;
		}

		final Object obj = NBTEditorUtil.getItemTag(item, "voidKey");
		if (!(obj instanceof String)) {
			return false;
		}
		final VoidItemManager vim = VoidItemManager.getInstance();
		return vim.getCachedItem(obj.toString()).isPresent();
	}

	public boolean isChest(final Block block) {
		return block.getType() == Material.CHEST && block.getState() instanceof Chest;
	}

	public boolean isChest(final Location location) {
		return this.isChest(location.getBlock());
	}

	public Optional<VoidStorage> getVoidStorage(final Location location) {
		if (!this.isChest(location)) {
			return Optional.empty();
		}
		for (final PlayerData playerData : PlayerDataManager.getInstance().getPlayerDatas().values()) {

			for (final VoidStorage voidStorage : playerData.getVoidStorages()) {

				if (voidStorage.getLocation().equals(location)) {
					return Optional.of(voidStorage);
				}

			}

		}
		return Optional.empty();
	}

	public Optional<VoidStorage> getVoidStorage(final Block block) {
		return this.getVoidStorage(block.getLocation());
	}

	public Optional<VoidStorage> getVoidStorage(final VoidInventoryType type, final Inventory toCompare) {
		Preconditions.checkNotNull(type, "The VoidInventoryType type can't be null");

		for (final PlayerData playerData : PlayerDataManager.getInstance().getPlayerDatas().values()) {

			for (final VoidStorage voidStorage : playerData.getVoidStorages()) {
				final Inventory comparedWith;
				switch (type) {
				case CHEST:
					comparedWith = voidStorage.getBlockInventory();
					break;
				case MENU:
					comparedWith = voidStorage.getMenuInventory();
					break;
				case VOIDCHEST:
					comparedWith = voidStorage.getCustomInventory();
					break;
				default:
					continue;
				}
				if (comparedWith.equals(toCompare)) {
					return Optional.of(voidStorage);
				}
			}
		}
		return Optional.empty();
	}

	public enum VoidInventoryType {
		VOIDCHEST, CHEST, MENU,;
		private VoidInventoryType() {
		}
	}
}
