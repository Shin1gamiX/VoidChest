package me.shin1gamix.voidchest.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public final class FileManager {

	private static FileManager instance;

	public static FileManager getInstance() {
		return instance == null ? instance = new FileManager() : instance;
	}

	private CFG playerBase;
	private CFG messages;
	private CFG options;
	private CFG shop;
	private CFG voidInventory;

	private FileManager() {
	}

	public void loadFiles(final JavaPlugin voidChest) {
		playerBase = new CFG(voidChest, "playerbase", false);
		messages = new CFG(voidChest, "messages", false);
		options = new CFG(voidChest, "options", true);
		shop = new CFG(voidChest, "shop", true);
		voidInventory = new CFG(voidChest, "voidchest-inventory", true);
	}

	public CFG getPlayerBase() {
		return playerBase;
	}

	public CFG getMessages() {
		return messages;
	}

	public CFG getOptions() {
		return options;
	}

	public CFG getShop() {
		return shop;
	}

	public CFG getVoidInventory() {
		return voidInventory;
	}

}
