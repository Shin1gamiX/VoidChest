package me.shin1gamix.voidchest.ecomanager.customeco;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.Essentials;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.events.VoidSellChestEvent;
import me.shin1gamix.voidchest.events.VoidSellEvent;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public class CraftEssentialsEconomy implements VoidEconomy {

	private final VoidChestPlugin core;
	private final Essentials api;

	public CraftEssentialsEconomy(final VoidChestPlugin core) {
		this.core = core;
		api = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	@Override
	public double getProfit(ItemStack item) {
		Preconditions.checkNotNull(item);

		final boolean ignoreMeta = FileManager.getInstance().getOptions().getFile()
				.getBoolean("Sell.Essentials.ignore-item-meta", false);

		if (!ignoreMeta && item.hasItemMeta()) {
			return 0;
		}

		BigDecimal resultPrice = this.api.getWorth().getPrice(this.api, item);
		if (resultPrice == null) {
			return 0;
		}

		double price = resultPrice.doubleValue() * item.getAmount();
		return price > 0 ? price : 0;
	}

	@Override
	public void sellInventory(final PlayerData data) {

		final OfflinePlayer off = data.getOwner();

		double totalMoneyGained = 0;
		int totalItemsSold = 0;
		int totalItemsPurged = 0;

		for (VoidStorage voidStorage : data.getVoidStorages()) {

			VoidSellChestEvent chestSellEvent = new VoidSellChestEvent(voidStorage);
			Bukkit.getPluginManager().callEvent(chestSellEvent);
			if (chestSellEvent.isCancelled()) {
				continue;
			}

			final Inventory inv = voidStorage.getCustomInventory();

			for (int i = 0; i < inv.getContents().length; i++) {

				final ItemStack item = inv.getItem(i);

				if (item == null || item.getType() == Material.AIR) {
					continue;
				}

				final double eventProfit = this.getProfit(item) * voidStorage.getBooster() * data.getBooster();
				VoidSellEvent sellEvent = new VoidSellEvent(voidStorage, item, eventProfit);
				Bukkit.getPluginManager().callEvent(sellEvent);

				if (sellEvent.isCancelled()) {
					continue;
				}

				final int itemAmount = item.getAmount();

				final double profit = sellEvent.getPrice();
				if (profit <= 0) {
					if (voidStorage.isPurgeInvalidItems()) {
						totalItemsPurged += itemAmount;
						voidStorage.setItemsPurged(voidStorage.getItemsPurged() + itemAmount);
						inv.clear(i);
					}
					continue;
				}

				if (voidStorage.isAutoSell()) {
					totalMoneyGained += profit;
					voidStorage.setMoney(voidStorage.getMoney() + profit);
					voidStorage.setItemsSold(voidStorage.getItemsSold() + itemAmount);
					totalItemsSold += itemAmount;
					inv.clear(i);
				}
			}

			voidStorage.update();
		}

		if (totalItemsSold < 1) {
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

		String soundInput = FileManager.getInstance().getOptions().getFile().getString("Sounds.voidchest-sell",
				"LEVEL_UP");
		final Optional<Sound> soundOptional = SoundUtil.bukkitSound(soundInput);
		if (soundOptional.isPresent()) {
			player.playSound(player.getLocation(), soundOptional.get(), 1, 1);
		}

		final Map<String, String> map = Maps.newHashMap();
		map.put("%money%", Utils.formatNumber(totalMoneyGained));
		map.put("%itemspurged%", Utils.formatNumber(totalItemsPurged));
		map.put("%itemssold%", Utils.formatNumber(totalItemsSold));
		MessagesUtil.SELL_INVENTORY.msg(player, map, false);
	}

	@Override
	public String getName() {
		return "CraftEssentialsEconomy{version=" + api.getDescription().getVersion() + ", name="
				+ api.getDescription().getName() + '}';
	}

	@Override
	public boolean isVaultDependent() {
		return true;
	}
}
