package me.shin1gamix.voidchest.data.customchest.options;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.utilities.Utils;

public class VoidStorageMenu implements InventoryHolder {

	private final VoidStorage storage;
	private final Inventory inventory;

	public VoidStorageMenu(final VoidStorage storage) {
		this.storage = storage;
		final String name = storage.getName();

		final Map<String, String> map = Maps.newHashMap();
		map.put("%owner%", storage.getPlayerData().getOwner().getName());
		map.put("%voidchest%", name);

		final FileConfiguration voidInventory = FileManager.getInstance().getVoidInventory().getFile();
		int menuSize = Math.min(Math.abs(voidInventory.getInt("VoidChests." + name + ".Menu-Inventory.rows", 4)), 6);
		menuSize = menuSize == 0 ? 6 : menuSize;
		final String menuInvName = voidInventory.getString("VoidChests." + name + ".Menu-Inventory.name",
				"%owner%'s VoidChest");

		this.inventory = Bukkit.createInventory(this, menuSize * 9,
				Utils.colorize(Utils.placeHolder(menuInvName, map, false)));

	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	public VoidStorage getStorage() {
		return storage;
	}

}
