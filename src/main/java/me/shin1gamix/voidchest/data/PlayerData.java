package me.shin1gamix.voidchest.data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.runnables.SellTask;
import me.shin1gamix.voidchest.utilities.Utils;

public class PlayerData {

	private BukkitTask task = null;

	private boolean sendMessage = true;
	private final String name;

	private double booster = 1d;
	private long boostTime = 0l;

	private OfflinePlayer offlinePlayer;

	private long attemptSaleTime = 0;

	private final Set<VoidStorage> chests = Sets.newHashSet();
	private final UUID uuid;

	public PlayerData(final UUID uuid, final String name) {
		this.name = name;
		this.uuid = uuid;
		this.offlinePlayer = new OfflinePlayerCopy(this);
	}

	public OfflinePlayer getOwner() {
		return this.offlinePlayer;
	}

	public void recalculateOwner() {
		final Player online = Bukkit.getPlayer(this.uuid);
		if (online == null) {
			return;
		}

		this.offlinePlayer = new OfflinePlayerCopy(this);
	}

	/**
	 * @return the sendMessage
	 */
	public boolean isSendMessage() {
		return sendMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		PlayerData other = (PlayerData) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	/**
	 * @param sendMessage
	 *            the sendMessage to set
	 */
	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}

	public void terminate() {
		FileConfiguration file = FileManager.getInstance().getPlayerBase().getFile();
		String name = getOwner().getName();
		file.set("Players." + name, null);

		file.set("Players." + name + ".uuid", getOwner().getUniqueId().toString());
		file.set("Players." + name + ".booster.multiplier", this.getBooster());
		file.set("Players." + name + ".booster.time", this.boostTime);

		int i = 0;
		for (VoidStorage chest : this.chests) {
			file.set("Players." + name + ".chests." + i + ".name", chest.getName());
			file.set("Players." + name + ".chests." + i + ".location", chest.getLocation());
			file.set("Players." + name + ".chests." + i + ".money", chest.getMoney());
			file.set("Players." + name + ".chests." + i + ".items-sold-amount", chest.getItemsSold());
			file.set("Players." + name + ".chests." + i + ".items-purged-amount", chest.getItemsPurged());
			file.set("Players." + name + ".chests." + i + ".autosell", chest.isAutoSell());
			file.set("Players." + name + ".chests." + i + ".purge-items", chest.isPurgeInvalidItems());
			file.set("Players." + name + ".chests." + i + ".hologram", chest.isHologramActivated());
			file.set("Players." + name + ".chests." + i + ".creation-time", chest.getCreationTime());
			this.saveChestsInFile(file, chest, i);
			++i;
		}
	}

	private void saveChestsInFile(final FileConfiguration file, final VoidStorage chest, final int i) {
		final Inventory custInv = chest.getCustomInventory();
		for (int x = 0; x < custInv.getSize(); x++) {
			final ItemStack item = custInv.getItem(x);
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			file.set("Players." + name + ".chests." + i + ".items." + x, item);
		}
	}

	public void init() {
		final FileConfiguration file = FileManager.getInstance().getPlayerBase().getFile();

		String name = getOwner().getName();

		if (!file.isSet("Players." + name)) {
			return;
		}

		this.setBooster(file.getDouble("Players." + name + ".booster.multiplier", 1d));
		this.setBoostTime(file.getLong("Players." + name + ".booster.time", 0));

		/* No data to load from. */
		if (!file.isSet("Players." + name + ".chests")) {
			return;
		}

		final ConfigurationSection sect = file.getConfigurationSection("Players." + name + ".chests");
		for (final String key : sect.getKeys(false)) {
			final Location loc = (Location) sect.get(key + ".location");
			if (loc == null) {
				continue;
			}
			final Block block = loc.getBlock();
			if (!VoidChestPlugin.getInstance().getVoidManager().isChest(block)) {
				continue;
			}

			final String type = sect.getString(key + ".name");
			if (type == null) {
				continue;
			}

			VoidStorage chest = new VoidStorage(this, type, block);
			this.chests.add(chest);
			if (sect.isSet(key + ".items")) {
				for (String item : sect.getConfigurationSection(key + ".items").getKeys(false)) {
					final ItemStack stack = sect.getItemStack(key + ".items." + item);
					chest.getCustomInventory().setItem(Integer.parseInt(item), stack);
				}
			}
			chest.setAutoSell(sect.getBoolean(key + ".autosell", true));
			chest.setCreationTime(sect.getLong(key + ".creation-time", System.currentTimeMillis()));
			chest.setPurgeInvalidItems(sect.getBoolean(key + ".purge-items", false));
			chest.setHologramActivated(sect.getBoolean(key + ".hologram", false));
			chest.setItemsSold(sect.getInt(key + ".items-sold-amount", 0));
			chest.setItemsPurged(sect.getInt(key + ".items-purged-amount", 0));
			chest.setMoney(sect.getDouble(key + ".money", 0));
			chest.update();

		}
		file.set("Players." + name, null);
		FileManager.getInstance().getPlayerBase().saveFile();
	}

	public void attemptStartSellTask() {
		if (this.task != null) {
			return;
		}
		final long delay = FileManager.getInstance().getOptions().getFile().getLong("Sell.interval", 15);
		this.task = new SellTask(this, delay).runTaskTimer(VoidChestPlugin.getInstance(), delay * 20, delay * 20);
	}

	public void attemptStopSellTask() {
		if (this.task == null) {
			return;
		}
		this.task.cancel();
		this.task = null;
	}

	public Set<VoidStorage> getVoidStorages() {
		return this.chests;
	}

	public String getName() {
		return name;
	}

	public double getBooster() {
		long time = System.currentTimeMillis();
		if (this.boostTime <= time) {
			this.boostTime = 0l;
			this.booster = 1d;
		}
		return this.booster;
	}

	public String getBoosterString() {
		double booster = this.getBooster();
		return booster % 1d == 0 ? ((int) booster) + "" : booster + "";
	}

	public String getBoosterTimeLeft() {
		double booster = this.getBooster();
		if (booster == 1d) {
			return "No active booster";
		}
		return Utils.convertSeconds((this.boostTime - System.currentTimeMillis()) / 1000 + 1);
	}

	public void setBooster(double booster) {
		this.booster = booster;
	}

	public long getBoostTime() {
		return boostTime;
	}

	public void closeVoidStorageInventories() {
		this.chests.forEach(VoidStorage::closeInventories);
	}

	public void setBoostTime(long boostTime) {
		this.boostTime = boostTime;
	}

	public long getAttemptSaleTime() {
		return attemptSaleTime;
	}

	public void setAttemptSaleTime(long attemptSaleTime) {
		this.attemptSaleTime = attemptSaleTime;
	}

	private class OfflinePlayerCopy implements OfflinePlayer {
		private final PlayerData data;
		private final OfflinePlayer offPlayer;

		public OfflinePlayerCopy(final PlayerData data) {
			this.data = data;
			this.offPlayer = Bukkit.getOfflinePlayer(data.uuid);
		}

		@Override
		public boolean isOp() {
			return this.offPlayer.isOp();
		}

		@Override
		public void setOp(boolean arg0) {
			this.offPlayer.setOp(arg0);
		}

		@Override
		public Map<String, Object> serialize() {
			return this.offPlayer.serialize();
		}

		@Override
		public Location getBedSpawnLocation() {
			return this.offPlayer.getBedSpawnLocation();
		}

		@Override
		public long getFirstPlayed() {
			return this.offPlayer.getFirstPlayed();
		}

		@Override
		public long getLastPlayed() {
			return this.offPlayer.getLastPlayed();
		}

		@Override
		public String getName() {
			return this.data.getName();
		}

		@Override
		public Player getPlayer() {
			return this.offPlayer.getPlayer();
		}

		@Override
		public UUID getUniqueId() {
			return this.offPlayer.getUniqueId();
		}

		@Override
		public boolean hasPlayedBefore() {
			return this.offPlayer.hasPlayedBefore();
		}

		@Override
		public boolean isBanned() {
			return this.offPlayer.isBanned();
		}

		@Override
		public boolean isOnline() {
			return this.offPlayer.isOnline();
		}

		@Override
		public boolean isWhitelisted() {
			return this.offPlayer.isWhitelisted();
		}

		@Override
		public void setWhitelisted(boolean arg0) {
			this.offPlayer.setWhitelisted(arg0);
		}

	}
}
