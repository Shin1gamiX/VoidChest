package me.shin1gamix.voidchest.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class HologramTimerTask extends BukkitRunnable {

	private final VoidChestPlugin core;

	public HologramTimerTask(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void run() {
		if (!this.core.isHolographicDisplaysSupport()) {
			this.cancel();
			return;
		}

		for (PlayerData data : PlayerDataManager.getInstance().getPlayerDatas().values()) {
			data.getVoidStorages().forEach(VoidStorage::updateHologram);
		}

	}

}
