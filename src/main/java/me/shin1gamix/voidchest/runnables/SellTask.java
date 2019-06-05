package me.shin1gamix.voidchest.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.PlayerData;

public class SellTask extends BukkitRunnable {

	private final PlayerData data;
	private final long delay;

	public SellTask(final PlayerData data, long delay) {
		this.data = data;
		this.delay = delay;
		this.data.setAttemptSaleTime(System.currentTimeMillis() + (this.delay) * 1000);
	}

	@Override
	public void run() {
		this.data.setAttemptSaleTime(System.currentTimeMillis() + (this.delay) * 1000);
		VoidChestPlugin.getInstance().getVoidEconomyManager().initiateSell(this.data);
	}

}
