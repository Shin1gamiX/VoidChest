package me.shin1gamix.voidchest.data;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;

public final class PlayerDataManager implements Listener {

	private static PlayerDataManager instance;

	public static PlayerDataManager getInstance() {
		return instance == null ? instance = new PlayerDataManager() : instance;
	}

	private final Map<UUID, PlayerData> playerDataMap = Maps.newHashMap();

	public Map<UUID, PlayerData> getPlayerDatas() {
		return this.playerDataMap;
	}

	private PlayerDataManager() {
	}

	public void loadPlayerDatas() {

		this.playerDataMap.values().forEach(PlayerData::attemptStopSellTask);
		this.playerDataMap.clear();

		Bukkit.getOnlinePlayers().forEach(online -> {
			final PlayerData data = this.loadPlayerData(online, false, false);
			data.attemptStartSellTask();
		});

		final FileConfiguration file = FileManager.getInstance().getPlayerBase().getFile();
		final ConfigurationSection sect = file.getConfigurationSection("Players");
		if (sect != null) {
			for (final String key : sect.getKeys(false)) {
				final PlayerData data = this.loadPlayerData(UUID.fromString(sect.getString(key + ".uuid")), key, false,
						false);
				data.attemptStartSellTask();
			}
		}

		FileManager.getInstance().getPlayerBase().saveFile();
	}

	public void savePlayerDatas(final boolean stopTask, boolean closeInventories) {
		for (final PlayerData data : this.playerDataMap.values()) {
			if (stopTask) {
				data.attemptStopSellTask();
			}
			if (closeInventories) {
				data.closeVoidStorageInventories();
			}

			data.loadToFile();
		}
	}

	public PlayerData loadPlayerData(final UUID uuid, final String name, final boolean saveFile,
			final boolean deletePath) {
		PlayerData data = this.playerDataMap.get(uuid);
		if (data == null) {
			if (name == null) {
				throw new IllegalArgumentException("The user with UUID: " + uuid.toString()
						+ " seems to have an invalid name. Is the player database corrupt?");
			}
			data = new PlayerData(uuid, name);
			data.init(saveFile, deletePath);
			data.setAttemptSaleTime(System.currentTimeMillis()
					+ (FileManager.getInstance().getOptions().getFile().getLong("Sell.interval", 15)) * 1000);
			playerDataMap.put(uuid, data);
		}
		return data;
	}

	public PlayerData loadPlayerData(final OfflinePlayer offlinePlayer, final boolean saveFile,
			final boolean deletePath) {
		return this.loadPlayerData(offlinePlayer.getUniqueId(), offlinePlayer.getName(), saveFile, deletePath);
	}

	public PlayerData loadPlayerData(final OfflinePlayer offlinePlayer) {
		return this.loadPlayerData(offlinePlayer, false, false);
	}

	public PlayerData getPlayerData(final VoidStorage chest) {
		return chest.getPlayerData();
	}

	@EventHandler
	private void onJoin(final PlayerJoinEvent e) {
		final PlayerData data = this.loadPlayerData(e.getPlayer().getUniqueId(), e.getPlayer().getName(), true, true);
		data.recalculateOwner();
		data.attemptStartSellTask();
	}

}
