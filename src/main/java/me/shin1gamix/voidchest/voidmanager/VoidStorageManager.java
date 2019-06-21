package me.shin1gamix.voidchest.voidmanager;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class VoidStorageManager {

	@Nullable
	public VoidStorage getVoidStorage(final Location location) {
		if (location.getBlock().getType() != Material.CHEST) {
			return null;
		}
		for (final PlayerData playerData : PlayerDataManager.getInstance().getPlayerDatas().values()) {
			for (final VoidStorage voidStorage : playerData.getVoidStorages()) {
				if (voidStorage.getLocation().equals(location)) {
					return voidStorage;
				}
			}
		}
		return null;
	}

	@Nullable
	public VoidStorage getVoidStorage(final Block block) {
		return this.getVoidStorage(block.getLocation());
	}

}
