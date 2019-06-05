package me.shin1gamix.voidchest.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.updatechecker.UpdateChecker;
import me.shin1gamix.voidchest.updatechecker.UpdateChecker.UpdateReason;
import me.shin1gamix.voidchest.utilities.Utils;

public class UpdateCheckTask extends BukkitRunnable {

	private final VoidChestPlugin core;

	public UpdateCheckTask(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void run() {
		if (!FileManager.getInstance().getOptions().getFile().getBoolean("Auto-update", true)) {
			return;
		}

		final UpdateChecker updateChecker = UpdateChecker.init(this.core, 65576);

		updateChecker.requestUpdateCheck().whenComplete((result, exception) -> {
			if (result.requiresUpdate()) {
				Utils.debug(this.core, String.format(
						"An update is available! VoidChest %s may be downloaded on SpigotMC or MC-Market (depending where you've bought it from)",
						result.getNewestVersion()));
				return;
			}
			UpdateReason reason = result.getReason();
			if (reason == UpdateReason.UP_TO_DATE) {
				Utils.debug(this.core,
						String.format("Your version of VoidChest (%s) is up to date!", result.getNewestVersion()));
			} else if (reason == UpdateReason.UNRELEASED_VERSION) {
				Utils.debug(this.core, String.format(
						"Your version of VoidChest (%s) is more recent than the one publicly available. Are you on a development build?",
						result.getNewestVersion()));
			} else {
				Utils.debug(this.core, String.format("Could not check for a new version of VoidChest. Reason: %s", reason));
			}
		});
	}

}
