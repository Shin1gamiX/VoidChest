package me.shin1gamix.voidchest.data.customchest;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.customchest.items.VoidIcon;
import me.shin1gamix.voidchest.data.customchest.items.VoidIconManager;
import me.shin1gamix.voidchest.events.VoidChestHologramUpdateEvent;
import me.shin1gamix.voidchest.utilities.Utils;

public class VoidStorage {

	private final String name;

	private final Location location;
	private final PlayerData data;

	private final Inventory customInventory;
	private final Inventory menuInventory;

	private double money = 0.0;
	private long itemsSold = 0;
	private long itemsPurged = 0;

	private long creationTime;

	private boolean autoSell;
	private boolean purgeInvalidItems;
	private boolean hologram;

	private Hologram holo = null;

	private final Map<Integer, VoidIcon> voidItems = Maps.newHashMap();

	public VoidStorage(final PlayerData data, final String name, final Block block) {
		Preconditions.checkArgument(block.getType() == Material.CHEST, "Only chest types can be void chests!");

		this.name = name;
		this.data = data;

		final FileConfiguration voidInventory = FileManager.getInstance().getVoidInventory().getFile();

		int customSize = Math.min(Math.abs(voidInventory.getInt("VoidChests." + name + ".Storage.inventory.rows", 3)),
				6);
		customSize = customSize == 0 ? 6 : customSize;

		final Map<String, String> map = Maps.newHashMap();
		map.put("%owner%", this.data.getOwner().getName());
		map.put("%voidchest%", name);

		final String customInvName = voidInventory.getString("VoidChests." + name + ".Storage.inventory.name",
				"%owner%'s VoidChest");
		this.customInventory = Bukkit.createInventory(null, customSize * 9,
				Utils.colorize(Utils.placeHolder(customInvName, map, false)));

		int menuSize = Math.min(Math.abs(voidInventory.getInt("VoidChests." + name + ".Menu-Inventory.rows", 4)), 6);
		menuSize = menuSize == 0 ? 6 : menuSize;
		final String menuInvName = voidInventory.getString("VoidChests." + name + ".Menu-Inventory.name",
				"%owner%'s VoidChest");

		this.menuInventory = Bukkit.createInventory(null, menuSize * 9,
				Utils.colorize(Utils.placeHolder(menuInvName, map, false)));

		this.location = block.getLocation();
	}

	public String getBoosterString() {
		double booster = this.getBooster();
		return booster % 1d == 0 ? ((int) booster) + "" : booster + "";
	}

	/**
	 * @return the inv
	 */
	public Inventory getCustomInventory() {
		return this.customInventory;
	}

	private void deleteHologram() {
		if (this.holo != null) {
			this.holo.delete();
			this.holo = null;
		}
	}

	public void updateHologram() {
		final VoidChestPlugin vc = VoidChestPlugin.getInstance();
		if (!vc.isHdSupport()) {
			return;
		}

		if (!FileManager.getInstance().getVoidInventory().getFile()
				.getBoolean("VoidChests." + this.name + ".Storage.hologram.enabled", false)) {
			return;
		}

		if (!this.hologram) {
			this.deleteHologram();
			return;
		}

		final Map<String, String> map = Maps.newHashMap();
		map.put("%money%", Utils.formatNumber(this.money));
		map.put("%itemssold%", Utils.formatNumber(this.itemsSold));
		map.put("%itemspurged%", Utils.formatNumber(this.itemsPurged));
		map.put("%owner%", this.data.getName());
		map.put("%voidchest%", this.name);
		map.put("%booster%", this.getBoosterString());

		final long cooldown = this.data.getAttemptSaleTime() - System.currentTimeMillis();
		long result = (long) Math.ceil(cooldown / 1000 + 1);
		if (result > 0) {
			map.put("%timeleft%", Utils.convertSeconds(result));
		} else {
			map.put("%timeleft%", "invalid time");
		}

		final FileManager fm = FileManager.getInstance();
		FileConfiguration voidFile = fm.getVoidInventory().getFile();

		final VoidChestHologramUpdateEvent event = new VoidChestHologramUpdateEvent(this,
				voidFile.getStringList("VoidChests." + this.name + ".Storage.hologram.text"));
		Bukkit.getPluginManager().callEvent(event);
		if (event.getLines().isEmpty()) {
			this.setHologramActivated(false);
			this.update();
			this.updateHologram();
			return;
		}

		if (this.holo == null) {
			this.holo = HologramsAPI.createHologram(vc, this.getLocation().add(0.5,
					voidFile.getDouble("VoidChests." + this.name + ".Storage.hologram.height", 3.25), 0.5));
			for (final String key : event.getLines()) {
				this.holo.appendTextLine(Utils.placeHolder(Utils.colorize(key), map, false));
			}
		}

		/* We will be setting the lines using this "hacky way". */
		int i = 0;
		for (final String key : event.getLines()) {
			for (String placeholder : map.keySet()) {
				if (key.contains(placeholder)) {
					TextLine line = (TextLine) this.holo.getLine(i);
					line.setText(Utils.placeHolder(Utils.colorize(key), map, false));
					break;
				}
			}
			++i;
		}
	}

	public void closeInventories() {
		List<HumanEntity> custInvView = this.customInventory.getViewers();
		List<HumanEntity> copy = Lists.newArrayList(custInvView);
		custInvView.removeAll(copy);
		copy.forEach(HumanEntity::closeInventory);

		List<HumanEntity> menuInvView = this.menuInventory.getViewers();
		copy = Lists.newArrayList(menuInvView);
		menuInvView.removeAll(copy);
		copy.forEach(HumanEntity::closeInventory);

		/*
		 * Iterator<HumanEntity> hent = this.customInventory.getViewers().iterator();
		 * while (hent.hasNext()) { final HumanEntity ent = hent.next(); hent.remove();
		 * ent.closeInventory(); }
		 * 
		 * hent = this.menuInventory.getViewers().iterator(); while (hent.hasNext()) {
		 * final HumanEntity ent = hent.next(); hent.remove(); ent.closeInventory(); }
		 */
	}

	/*
	 * This shall return an absolute number gained by the path since we can't allow
	 * for negative or zero based boosts.
	 */
	public double getBooster() {
		final FileManager fm = FileManager.getInstance();
		FileConfiguration voidFile = fm.getVoidInventory().getFile();
		final double booster = voidFile.getDouble("VoidChests." + this.name + ".Booster", 1d);
		return booster == 0 ? 1d : Math.abs(booster);
	}

	public String getPermissionBreak() {
		final FileManager fm = FileManager.getInstance();
		FileConfiguration voidFile = fm.getVoidInventory().getFile();
		return voidFile.getString("VoidChests." + this.name + ".Permissions.break", "voidchest.break");
	}

	public String getPermissionPlace() {
		final FileManager fm = FileManager.getInstance();
		FileConfiguration voidFile = fm.getVoidInventory().getFile();
		return voidFile.getString("VoidChests." + this.name + ".Permissions.place", "voidchest.place");
	}

	public Inventory getBlockInventory() {
		Block block = this.getBlock();
		BlockState state = block.getState();
		if (block.getType() != Material.CHEST || !(state instanceof Chest)) {
			return null;
		}
		Chest cont = (Chest) state;
		return cont.getInventory();
	}

	public void update() {
		VoidIconManager.getInstance().loadItems(this);
	}

	/**
	 * @return the block
	 */
	public Block getBlock() {
		return this.location.getWorld().getBlockAt(this.location);
	}

	public Location getLocation() {
		return this.location.clone();
	}

	/**
	 * @return the data
	 */
	public PlayerData getPlayerData() {
		return data;
	}

	/**
	 * @return the money
	 */
	public double getMoney() {
		return money;
	}

	/**
	 * @param money
	 *            the money to set
	 */
	public void setMoney(double money) {
		this.money = money;
	}

	/**
	 * @return the autoSell
	 */
	public boolean isAutoSell() {
		return autoSell;
	}

	/**
	 * @param autoSell
	 *            the autoSell to set
	 */
	public void setAutoSell(boolean autoSell) {
		this.autoSell = autoSell;
	}

	/**
	 * @return the purgeInvalidItems
	 */
	public boolean isPurgeInvalidItems() {
		return purgeInvalidItems;
	}

	/**
	 * @param purgeInvalidItems
	 *            the purgeInvalidItems to set
	 */
	public void setPurgeInvalidItems(boolean purgeInvalidItems) {
		this.purgeInvalidItems = purgeInvalidItems;
	}

	/**
	 * @return the menuInventory
	 */
	public Inventory getMenuInventory() {
		return menuInventory;
	}

	/**
	 * @return the items
	 */
	public long getItemsSold() {
		return itemsSold;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItemsSold(long itemsSold) {
		this.itemsSold = itemsSold;
	}

	/**
	 * @return the creationTime
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime
	 *            the creationTime to set
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public Map<Integer, VoidIcon> getVoidItems() {
		return voidItems;
	}

	public boolean isHologramActivated() {
		return hologram;
	}

	public void setHologramActivated(boolean hologram) {
		this.hologram = hologram;
	}

	public Hologram getHolo() {
		return holo;
	}

	public void setHolo(Hologram holo) {
		this.holo = holo;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoidStorage other = (VoidStorage) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		return true;
	}

	public long getItemsPurged() {
		return itemsPurged;
	}

	public void setItemsPurged(long itemsPurged) {
		this.itemsPurged = itemsPurged;
	}

}
