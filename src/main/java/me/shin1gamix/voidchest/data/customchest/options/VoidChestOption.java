package me.shin1gamix.voidchest.data.customchest.options;

import org.bukkit.configuration.file.FileConfiguration;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public enum VoidChestOption {

	DECORATION("decoration"),

	CHEST("chest"),

	VOIDCHEST("voidchest"),

	PURGE("purge-items"),

	AUTOSELL("auto-sell"),

	HOLOGRAM("hologram"),

	;

	private final String path;

	private VoidChestOption(final String path) {
		this.path = path;
	}

	public boolean getDefault(final VoidStorage cvc) {
		final FileConfiguration file = FileManager.getInstance().getVoidInventory().getFile();
		return file.getBoolean("VoidChests." + cvc.getName() + "." + this.path + ".default", false);

	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
}
