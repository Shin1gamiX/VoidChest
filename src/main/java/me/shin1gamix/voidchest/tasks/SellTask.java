package me.shin1gamix.voidchest.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.PlayerData;

public class SellTask extends BukkitRunnable {

	private final PlayerData data;

	public SellTask(final PlayerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		VoidChestPlugin.getInstance().getVoidEconomyManager().initiateSell(this.data);
	}

}
