package me.shin1gamix.voidchest.ecomanager.customeco;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.events.VoidSellChestEvent;
import me.shin1gamix.voidchest.events.VoidSellEvent;
import me.shin1gamix.voidchest.utilities.MaterialUtil;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public class CraftVoidEconomy implements VoidEconomy {
	private final VoidChestPlugin core;

	public CraftVoidEconomy(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public double getProfit(ItemStack item) {

		FileManager fm = FileManager.getInstance();

		Preconditions.checkNotNull(item);
		final FileConfiguration file = fm.getShop().getFile();

		double price = 0d;

		for (String path : file.getConfigurationSection("Items").getKeys(false)) {
			final Optional<MaterialUtil> inputOpt = MaterialUtil.fromString(path);

			if (!inputOpt.isPresent()) {
				continue;
			}

			ItemStack stack = inputOpt.get().parseItem();

			final boolean ignoreMeta = fm.getOptions().getFile().getBoolean("Sell.VoidChest.ignore-item-meta", false);

			if (ignoreMeta) {
				item = new ItemStack(item);
				item.setItemMeta(Bukkit.getItemFactory().getItemMeta(item.getType()));
			}

			if (!item.isSimilar(stack)) {
				continue;
			}

			price = file.getDouble("Items." + path) * item.getAmount();
			break;
		}
		return price > 0 ? price : 0;
	}

	@Override
	public void sellInventory(final PlayerData data) {

		final OfflinePlayer off = data.getOwner();

		Set<VoidStorage> storageChests = data.getVoidStorages();

		if (storageChests.isEmpty()) {
			return;
		}

		double totalMoneyGained = 0;
		int totalItemsSold = 0;
		int totalItemsPurged = 0;

		for (VoidStorage voidStorage : storageChests) {

			VoidSellChestEvent chestSellEvent = new VoidSellChestEvent(voidStorage);
			Bukkit.getPluginManager().callEvent(chestSellEvent);
			if (chestSellEvent.isCancelled()) {
				continue;
			}

			final Inventory inv = voidStorage.getCustomInventory();
			for (int i = 0; i < inv.getSize(); i++) {

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

				final double profit = sellEvent.getPrice();
				
				int itemAmount = item.getAmount();
				
				if (profit <= 0) {
					if (voidStorage.isPurgeInvalidItems()) {					
						totalItemsPurged += itemAmount;
						voidStorage.setItemsPurged(voidStorage.getItemsPurged() + itemAmount);
						inv.clear(i);
					}
					continue;
				}

				if (voidStorage.isAutoSell()) {
					totalItemsSold += itemAmount;
					totalMoneyGained += profit;
					voidStorage.setItemsSold(voidStorage.getItemsSold() + itemAmount);
					voidStorage.setMoney(voidStorage.getMoney() + profit);
					inv.setItem(i, null);
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
		FileManager fm = FileManager.getInstance();
		String soundInput = fm.getOptions().getFile().getString("Sounds.voidchest-sell", "LEVEL_UP");

		final Optional<Sound> soundOptional = SoundUtil.bukkitSound(soundInput);
		if (soundOptional.isPresent()) {
			player.playSound(player.getLocation(), soundOptional.get(), 1, 1);
		}

		final Map<String, String> map = Maps.newHashMap();
		map.put("%money%", Utils.formatNumber(totalMoneyGained));
		map.put("%itemssold%", Utils.formatNumber(totalItemsSold));
		map.put("%itemspurged%", Utils.formatNumber(totalItemsPurged));
		MessagesUtil.SELL_INVENTORY.msg(player, map, false);
	}

	@Override
	public String getName() {
		return "CraftVoidEconomy{version=" + this.core.getDescription().getVersion() + ", name="
				+ this.core.getDescription().getName() + '}';
	}

	@Override
	public boolean isVaultDependent() {
		return true;
	}

}
