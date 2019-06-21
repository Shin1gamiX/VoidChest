package me.shin1gamix.voidchest.ecomanager.customeco.copy;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.events.VoidSellChestEvent;
import me.shin1gamix.voidchest.events.VoidSellItemEvent;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public abstract class CraftEconomyAbstract implements VoidEconomy {
	protected final VoidChestPlugin core;
	private final boolean vaultDependent;
	private final PluginDescriptionFile desc;

	protected CraftEconomyAbstract(final VoidChestPlugin core, final boolean vaultDependent,
			final PluginDescriptionFile desc) {
		this.core = core;
		this.vaultDependent = vaultDependent;
		this.desc = desc;
	}

	@Override
	public final void initiateSell(final PlayerData data) {

		final OfflinePlayer off = data.getOwner();

		List<VoidStorage> storageChests = data.getVoidStorages();

		if (storageChests.isEmpty()) {
			return;
		}

		double totalMoneyGained = 0;
		int totalItemsSold = 0;
		int totalItemsPurged = 0;
		for (VoidStorage voidStorage : storageChests) {

			if (!this.transferItems(voidStorage)) {
				continue;
			}

			VoidSellChestEvent chestSellEvent = new VoidSellChestEvent(voidStorage);
			Bukkit.getPluginManager().callEvent(chestSellEvent);
			if (chestSellEvent.isCancelled()) {
				continue;
			}

			boolean update = false;

			final Inventory inv = voidStorage.getCustomInventory();
			
			for (int i = 0; i < inv.getSize(); i++) {
				final ItemStack item = inv.getItem(i);
				if (item == null || item.getType() == Material.AIR) {
					continue;
				}

				final double eventProfit = this.getProfit(item) * voidStorage.getBooster() * data.getBooster();
				VoidSellItemEvent sellEvent = new VoidSellItemEvent(voidStorage, item, eventProfit);
				Bukkit.getPluginManager().callEvent(sellEvent);
				if (sellEvent.isCancelled()) {
					continue;
				}

				final double profit = sellEvent.getPrice();
				int itemAmount = item.getAmount();
				if (profit <= 0d) {
					if (voidStorage.isPurgeInvalidItems()) {
						totalItemsPurged += itemAmount;
						voidStorage.setItemsPurged(voidStorage.getItemsPurged() + itemAmount);
						inv.clear(i);
						if (!update)
							update = true;
					}
					continue;
				}
				if (voidStorage.isAutoSell()) {
					totalItemsSold += itemAmount;
					totalMoneyGained += profit;
					voidStorage.setItemsSold(voidStorage.getItemsSold() + itemAmount);
					voidStorage.setMoney(voidStorage.getMoney() + profit);
					inv.clear(i);
					if (!update)
						update = true;
				}
			}

			if (update) {
				voidStorage.update();
			}
		}

		if (totalItemsSold < 1 && totalItemsPurged < 1) {
			return;
		}

		this.core.getVault().getEconomy().depositPlayer(off, totalMoneyGained);
		if (!data.isSendMessage()) {
			return;
		}
		if (!off.isOnline()) {
			return;
		}
		final Player player = off.getPlayer();
		FileManager fm = FileManager.getInstance();

		String soundInput = fm.getOptions().getFile().getString("Sounds.voidchest-sell", "LEVEL_UP");
		SoundUtil.bukkitSound(soundInput).ifPresent(sound -> player.playSound(player.getLocation(), sound, 1, 1));

		final Map<String, String> map = Maps.newHashMap();
		map.put("%money%", Utils.formatNumber(totalMoneyGained));
		map.put("%itemssold%", Utils.formatNumber(totalItemsSold));
		map.put("%itemspurged%", Utils.formatNumber(totalItemsPurged));
		MessagesUtil.SELL_INVENTORY.msg(player, map, false);
	}

	@Override
	public final String getName() {
		final String simpleName = this.getClass().getSimpleName();
		return simpleName + "{name=" + desc.getName() + ", version=" + desc.getVersion() + '}';
	}

	@Override
	public final boolean isVaultDependent() {
		return vaultDependent;
	}

	private boolean transferItems(final VoidStorage storage) {
		final Location loc = storage.getLocation();

		if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
			return false;
		}

		if (storage.getBlock().getType() != Material.CHEST) {
			return false;
		}

		final Inventory blockInv = storage.getBlockInventory();
		if (blockInv == null) {
			return false;
		}

		final Inventory customInv = storage.getCustomInventory();

		for (int i = 0; i < blockInv.getSize(); i++) {
			ItemStack item = blockInv.getItem(i);
			if (item == null) {
				continue;
			}

			if (customInv.firstEmpty() == -1) {
				break;
			}

			customInv.addItem(item);
			blockInv.clear(i);

		}
		return true;

	}

}