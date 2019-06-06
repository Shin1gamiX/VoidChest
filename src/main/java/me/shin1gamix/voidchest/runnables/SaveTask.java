package me.shin1gamix.voidchest.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;

public class SaveTask extends BukkitRunnable {

	public SaveTask() {
	}

	@Override
	public void run() {
		final PlayerDataManager pdm = PlayerDataManager.getInstance();
		for (final PlayerData data : pdm.getPlayerDatas().values()) {
			data.terminate();
		}
		FileManager.getInstance().getPlayerBase().saveFile();
	}

}
