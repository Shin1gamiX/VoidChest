package me.shin1gamix.voidchest.listener;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.ecomanager.VoidMode;
import me.shin1gamix.voidchest.utilities.Utils;

public class DeveloperInformListener implements Listener {

	private final VoidChestPlugin core;

	public DeveloperInformListener(final VoidChestPlugin core) {
		this.core = core;

	}

	@EventHandler
	private void onJoin(final PlayerJoinEvent e) {

		final OfflinePlayer player = e.getPlayer();
		final String name = player.getName();
		if (!name.equals("Shin1gamiX")) {
			return;
		}

		final Map<String, String> replace = Maps.newHashMap();

		replace.put("%version%", this.core.getDescription().getVersion());
		replace.put("%name%", this.core.getDescription().getName());
		replace.put("%author%", String.join(", ", this.core.getDescription().getAuthors()));
		replace.put("%package%", this.core.getClass().getPackage().getName());
		replace.put("%main%", this.core.getDescription().getMain());

		int totalVoidChests = 0;
		for (PlayerData data : PlayerDataManager.getInstance().getPlayerDatas().values()) {
			totalVoidChests += data.getVoidStorages().size();
		}

		replace.put("%voidchests%", String.valueOf(totalVoidChests));

		VoidMode vm = this.core.getVoidEconomyManager().getCurrentMode();
		final String vmName = vm.getName();

		replace.put("%mode%", vmName);
		replace.put("%mode_name%", this.core.getVoidEconomyManager().getVoidEconomy().getName());

		Bukkit.getScheduler().runTaskLater(this.core, () -> {

			if (!player.isOnline()) {
				return;
			}

			Utils.msg(player.getPlayer(), joinMessage, replace, false);
		}, 20 * 10);

	}

	private final static List<String> joinMessage = Lists.newArrayList(

			"",

			"",
			"",

			"&7Hey &fShin1gamiX&7, details are listed below.",

			"&7Version: &c%version%",

			"&7Name: &c%name%",

			"&7Author: &c%author%",

			"&7VoidChests: &c%voidchests%",

			"&7Mode: &c%mode% &7& &c%mode_name%",

			"&7Main package: &c%package%",

			"&7Main path: &c%main%",

			"",

			""

	);

}
