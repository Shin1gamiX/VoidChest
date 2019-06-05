package me.shin1gamix.voidchest.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;

public class SaveTask extends BukkitRunnable {

	private final VoidChestPlugin core;

	public SaveTask(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void run() {
		for (final PlayerData data : this.core.getPlayerDataManager().getPlayerDatas().values()) {
			data.terminate(false);
		}
		FileManager.getInstance().getPlayerBase().saveFile();
	}

}
