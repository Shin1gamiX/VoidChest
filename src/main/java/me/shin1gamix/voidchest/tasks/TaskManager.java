package me.shin1gamix.voidchest.tasks;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;

public class TaskManager {
	private final VoidChestPlugin core;

	public TaskManager(final VoidChestPlugin core) {
		this.core = core;
	}

	private BukkitTask savingTask = null;
	private BukkitTask purgingTask = null;
	private BukkitTask hologramUpdateTask = null;

	public void attemptStartSaving() {
		if (this.savingTask != null) {
			this.savingTask.cancel();
			this.savingTask = null;
		}
		FileConfiguration options = FileManager.getInstance().getOptions().getFile();
		if (!options.getBoolean("Saving.enabled", false)) {
			return;
		}

		final long interval = options.getLong("Saving.interval", 30);

		this.savingTask = new SaveTask(core).runTaskTimer(this.core, 100, interval * 20);
	}

	public void attemptStartHologram() {
		if (this.hologramUpdateTask != null) {
			this.hologramUpdateTask.cancel();
			this.hologramUpdateTask = null;
		}

		FileConfiguration options = FileManager.getInstance().getOptions().getFile();
		if (!options.getBoolean("Hologram.enabled", true)) {
			return;
		}

		final long interval = options.getLong("Hologram.interval", 5);
		this.hologramUpdateTask = new HologramTimerTask(this.core).runTaskTimer(this.core, 20l, interval);
	}

	public void attemptStartPurging() {
		if (this.purgingTask != null) {
			this.purgingTask.cancel();
			this.purgingTask = null;
		}

		FileConfiguration options = FileManager.getInstance().getOptions().getFile();
		if (!options.getBoolean("Purging.enabled", true)) {
			return;
		}

		final long interval = options.getLong("Purging.interval", 100);
		this.purgingTask = new PurgeTask(this.core).runTaskTimer(this.core, 100, interval);

	}

	public BukkitTask getSavingTask() {
		return savingTask;
	}

	public void setSavingTask(BukkitTask savingTask) {
		this.savingTask = savingTask;
	}

	public BukkitTask getPurgingTask() {
		return purgingTask;
	}

	public void setPurgingTask(BukkitTask purgingTask) {
		this.purgingTask = purgingTask;
	}

	public BukkitTask getHologramUpdateTask() {
		return hologramUpdateTask;
	}

	public void setHologramUpdateTask(BukkitTask hologramUpdateTask) {
		this.hologramUpdateTask = hologramUpdateTask;
	}

	public void disableAll() {
		if (this.hologramUpdateTask != null) {
			this.hologramUpdateTask.cancel();
			this.hologramUpdateTask = null;
		}

		if (this.purgingTask != null) {
			this.purgingTask.cancel();
			this.purgingTask = null;
		}

		if (this.savingTask != null) {
			this.savingTask.cancel();
			this.savingTask = null;
		}
	}

}
