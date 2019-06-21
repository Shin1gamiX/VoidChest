package me.shin1gamix.voidchest.ecomanager.customeco;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.ecomanager.customeco.copy.CraftEconomyAbstract;
import net.brcdev.shopgui.ShopGuiPlugin;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopItem;

public class IShopGUIPlusEconomy extends CraftEconomyAbstract {

	private final ShopGuiPlugin api = ShopGuiPlugin.getInstance();

	public IShopGUIPlusEconomy(final VoidChestPlugin core) {
		super(core, true, ShopGuiPlugin.getInstance().getDescription());
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

}
