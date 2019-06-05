package me.shin1gamix.voidchest.data.customchest.items;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.VoidChestOption;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.data.customchest.objects.VoidIconAutoPurgeToggle;
import me.shin1gamix.voidchest.data.customchest.objects.VoidIconAutoSellToggle;
import me.shin1gamix.voidchest.data.customchest.objects.VoidIconChestInventory;
import me.shin1gamix.voidchest.data.customchest.objects.VoidIconDecorate;
import me.shin1gamix.voidchest.data.customchest.objects.VoidIconHologramToggle;
import me.shin1gamix.voidchest.data.customchest.objects.VoidIconVoidInventory;
import me.shin1gamix.voidchest.utilities.MaterialUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public class VoidIconManager {

	private VoidIconManager() {
	}

	private static VoidIconManager instance;

	public static VoidIconManager getInstance() {
		return instance == null ? instance = new VoidIconManager() : instance;
	}

	public void loadItems(final VoidStorage voidStorage) {

		final Map<Integer, VoidIcon> voidStorageItems = voidStorage.getVoidItems();
		voidStorageItems.clear();
		voidStorage.getMenuInventory().clear();

		final PlayerData data = voidStorage.getPlayerData();
		final OfflinePlayer owner = data.getOwner();
		final Map<String, String> replace = Maps.newHashMap();

		for (VoidChestOption vca : VoidChestOption.values()) {
			final List<VoidIcon> items;
			switch (vca) {
			case DECORATION:
				replace.put("%money%", Utils.formatNumber(voidStorage.getMoney()));
				replace.put("%itemssold%", Utils.formatNumber(voidStorage.getItemsSold()));
				replace.put("%itemspurged%", Utils.formatNumber(voidStorage.getItemsPurged()));
				replace.put("%booster%", voidStorage.getBoosterString());
				replace.put("%owner%", owner.getName());
				replace.put("%voidchest%", voidStorage.getName());
				items = this.getItems(vca, voidStorage, replace);
				break;
			default:
				items = this.getItems(vca, voidStorage, null);
				break;
			}

			for (VoidIcon voidItem : items) {
				int slot = voidItem.getSlot();
				if (slot >= voidStorage.getMenuInventory().getSize()) {
					continue;
				}
				voidStorage.getMenuInventory().setItem(slot, voidItem.getItem());
				voidStorage.getVoidItems().put(slot, voidItem);
			}

		}

	}

	public List<VoidIcon> getItems(VoidChestOption ability, final VoidStorage voidStorage,
			Map<String, String> replace) {
		final FileManager fm = FileManager.getInstance();
		final List<VoidIcon> sellAll = Lists.newArrayList();

		final FileConfiguration file = fm.getVoidInventory().getFile();
		final ConfigurationSection sect = file
				.getConfigurationSection("VoidChests." + voidStorage.getName() + "." + ability.getPath() + ".items");

		if (sect == null) {
			return sellAll;
		}

		for (String path : sect.getKeys(false)) {

			final String slotPath = path;
			if (ability == VoidChestOption.AUTOSELL) {
				path += voidStorage.isAutoSell() ? ".enabled" : ".disabled";
			} else if (ability == VoidChestOption.PURGE) {
				path += voidStorage.isPurgeInvalidItems() ? ".enabled" : ".disabled";
			} else if (ability == VoidChestOption.HOLOGRAM) {
				path += voidStorage.isHologramActivated() ? ".enabled" : ".disabled";
			}

			final Optional<MaterialUtil> muOpt = MaterialUtil.fromString(sect.getString(path + ".material"));
			final ItemStack item = muOpt.isPresent() ? muOpt.get().parseItem() : MaterialUtil.BEDROCK.parseItem();

			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(
					Utils.placeHolder(Utils.colorize(sect.getString(path + ".name", "Default Name")), replace, false));
			meta.setLore(Utils.placeHolder(Utils.colorize(sect.getStringList(path + ".lore")), replace, false));

			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			if (sect.getBoolean(path + ".enchanted", false)) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addEnchant(Enchantment.DURABILITY, 1, false);
			}
			item.setItemMeta(meta);

			final boolean closeInv = sect.getBoolean(path + ".inventory-close", false);

			final Set<Integer> slots = this.getSlots(voidStorage, ability, slotPath);
			for (int slot : slots) {
				final VoidIcon sel;
				switch (ability) {
				case AUTOSELL:
					sel = new VoidIconAutoSellToggle(voidStorage, item, slot);
					break;
				case DECORATION:
					sel = new VoidIconDecorate(voidStorage, item, slot);
					break;
				case PURGE:
					sel = new VoidIconAutoPurgeToggle(voidStorage, item, slot);
					break;
				case VOIDCHEST:
					sel = new VoidIconVoidInventory(voidStorage, item, slot);
					break;
				case CHEST:
					sel = new VoidIconChestInventory(voidStorage, item, slot);
					break;
				case HOLOGRAM:
					sel = new VoidIconHologramToggle(voidStorage, item, slot);
					break;
				default:
					continue;
				}
				sel.setCloseInventory(closeInv);
				sellAll.add(sel);

			}

		}

		return sellAll;

	}

	public Set<Integer> getSlots(final VoidStorage voidStorage, VoidChestOption ability, String key) {
		Set<Integer> slots = Sets.newHashSet();
		final FileManager fm = FileManager.getInstance();
		final FileConfiguration file = fm.getVoidInventory().getFile();
		final ConfigurationSection sect = file.getConfigurationSection(
				"VoidChests." + voidStorage.getName() + "." + ability.getPath() + ".items." + key);
		final String slotsInput = sect.getString("slots", sect.getString("slot", "invalid")).replace(" ", "");
		if (slotsInput.equals("invalid")) {
			return slots;
		}

		if (slotsInput.equalsIgnoreCase("all")) {
			for (int i = 0; i < 54; i++) {
				slots.add(i);
			}
			return slots;
		}

		final String[] slotsSplit = slotsInput.split(Pattern.quote(","));
		for (String loop : slotsSplit) {
			if (Utils.isInt(loop)) {
				slots.add(Integer.parseInt(loop));
			}
		}
		return slots;
	}

}
