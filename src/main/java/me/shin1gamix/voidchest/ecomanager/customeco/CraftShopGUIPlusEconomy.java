package me.shin1gamix.voidchest.ecomanager.customeco;

import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.events.VoidSellChestEvent;
import me.shin1gamix.voidchest.events.VoidSellEvent;
import me.shin1gamix.voidchest.utilities.MessagesX;
import me.shin1gamix.voidchest.utilities.SoundUtil;
import me.shin1gamix.voidchest.utilities.Utils;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopItem;

public class CraftShopGUIPlusEconomy implements VoidEconomy {
	private final VoidChestPlugin core;

	private final ShopGuiPlugin api = ShopGuiPlugin.getInstance();

	public CraftShopGUIPlusEconomy(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public void sellInventory(me.shin1gamix.voidchest.data.PlayerData data) {
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

				final double eventPrice = this.getProfit(item) * voidStorage.getBooster() * data.getBooster();
				final VoidSellEvent sellEvent = new VoidSellEvent(voidStorage, item, eventPrice);
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
					totalMoneyGained += profit;
					totalItemsSold += itemAmount;
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

		String soundInput = FileManager.getInstance().getOptions().getFile().getString("Sounds.voidchest-sell",
				"LEVEL_UP");
		final Optional<Sound> soundOptional = SoundUtil.bukkitSound(soundInput);
		if (soundOptional.isPresent()) {
			player.playSound(player.getLocation(), soundOptional.get(), 1, 1);
		}

		final Map<String, String> map = Maps.newHashMap();
		map.put("%money%", Utils.formatNumber(totalMoneyGained));
		map.put("%itemssold%", Utils.formatNumber(totalItemsSold));
		map.put("%itemspurged%", Utils.formatNumber(totalItemsPurged));
		MessagesX.SELL_INVENTORY.msg(player, map, false);

	}

	@Override
	public double getProfit(ItemStack item) {
		Preconditions.checkNotNull(item);

		double profit = 0d;

		shop: for (final Shop shop : api.getShopManager().shops.values()) {
			for (final ShopItem shopItem : shop.getShopItems()) {

				final ItemStack compare = this.resetItem(shopItem.getItem());

				final boolean ignoreMeta = FileManager.getInstance().getOptions().getFile()
						.getBoolean("Sell.ShopGUIPlus.ignore-item-meta", false);

				if (ignoreMeta) {
					item = this.resetItem(item);
				}

				if (!item.isSimilar(compare)) {
					continue;
				}

				profit = shopItem.getSellPriceForAmount(item.getAmount());
				break shop;
			}
		}

		return profit > 0 ? profit : 0;
	}

	@Override
	public String getName() {
		return "CraftShopGUIPlusEconomy{version=" + api.getDescription().getVersion() + ", name="
				+ api.getDescription().getName() + '}';
	}

	@Override
	public boolean isVaultDependent() {
		return true;
	}
}
