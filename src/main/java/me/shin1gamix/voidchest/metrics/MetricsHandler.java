package me.shin1gamix.voidchest.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.VoidMode;

public class MetricsHandler {

	private final VoidChestPlugin core;

	private static boolean registered = false;

	public MetricsHandler(final VoidChestPlugin core) {
		this.core = core;
	}

	public void setupMetrics() {
		if (registered) {
			throw new IllegalArgumentException("Metrics can only be registered once!");
		}

		registered = true;

		final Metrics metrics = new Metrics(this.core);

		metrics.addCustomChart(new Metrics.SimplePie("modes", () -> {
			final Optional<VoidMode> optional = VoidMode
					.getByName(FileManager.getInstance().getOptions().getFile().getString("Sell.mode"));
			if (optional.isPresent()) {
				return optional.get().getName();
			}
			return VoidMode.CRAFT_VOIDCHEST.getName();
		}));

		int purges = 0;
		int sell = 0;
		int holo = 0;
		for (final PlayerData data : PlayerDataManager.getInstance().getPlayerDatas().values()) {
			for (final VoidStorage storage : data.getVoidStorages()) {
				if (storage.isAutoSell())
					++sell;
				if (storage.isHologramActivated())
					++holo;
				if (storage.isPurgeInvalidItems())
					++purges;
			}
		}

		final int purgeRes = purges;
		final int sellRes = sell;
		final int holoRes = holo;
		metrics.addCustomChart(new Metrics.AdvancedPie("chest_options", () -> {
			final Map<String, Integer> valueMap = new HashMap<>();
			valueMap.put("purge", purgeRes);
			valueMap.put("auto-sell", sellRes);
			valueMap.put("holograms", holoRes);
			return valueMap;
		}));
	}

}
