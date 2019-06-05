package me.shin1gamix.voidchest.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.ecomanager.VoidMode;

public class MetricsHandler {

	private final VoidChestPlugin core;

	private static boolean registered = false;

	public MetricsHandler(final VoidChestPlugin core) {
		this.core = core;
	}

	private int getAmount(MetricOption mo) {
		return (int) core.getPlayerDataManager().getPlayerDatas().values().stream()
				.flatMap(data -> data.getVoidStorages().stream()).filter(chest -> {
					switch (mo) {
					case AUTO_SELL:
						return chest.isAutoSell();
					case HOLOGRAM:
						return chest.isHologramActivated();
					case PURGE:
						return chest.isPurgeInvalidItems();
					default:
						return false;
					}
				}).count();
	}

	private enum MetricOption {
		PURGE, AUTO_SELL, HOLOGRAM;
	}
	
	public void setupMetrics() {
		if (registered) {
			throw new IllegalArgumentException("Metrics can only be registered once!");
		}
		Metrics metrics = new Metrics(this.core);
		registered = true;
		metrics.addCustomChart(new Metrics.SimplePie("modes", () -> {
			final Optional<VoidMode> optional = VoidMode
					.getByName(FileManager.getInstance().getOptions().getFile().getString("Sell.mode"));
			if (optional.isPresent()) {
				return optional.get().getName();
			}
			return VoidMode.CRAFT_VOIDCHEST.getName();
		}));

		metrics.addCustomChart(new Metrics.AdvancedPie("chest_options", () -> {
			final Map<String, Integer> valueMap = new HashMap<>();
			valueMap.put("purge", this.getAmount(MetricOption.PURGE));
			valueMap.put("auto-sell", this.getAmount(MetricOption.AUTO_SELL));
			valueMap.put("holograms", this.getAmount(MetricOption.HOLOGRAM));
			return valueMap;
		}));
	}
}
