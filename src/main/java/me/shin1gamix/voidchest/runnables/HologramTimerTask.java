package me.shin1gamix.voidchest.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public class HologramTimerTask extends BukkitRunnable {

	private final VoidChestPlugin core;

	public HologramTimerTask(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void run() {
		if (!this.core.isHdSupport()) {
			this.cancel();
			return;
		}

		for (PlayerData data : this.core.getPlayerDataManager().getPlayerDatas().values()) {
			data.getVoidStorages().forEach(VoidStorage::updateHologram);
		}

	}

}
