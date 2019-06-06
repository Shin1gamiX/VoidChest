package me.shin1gamix.voidchest.commands;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.configuration.CFG;
import me.shin1gamix.voidchest.configuration.FileManager;
import me.shin1gamix.voidchest.data.PlayerData;
import me.shin1gamix.voidchest.data.PlayerDataManager;
import me.shin1gamix.voidchest.data.customchest.VoidStorage;
import me.shin1gamix.voidchest.ecomanager.VoidMode;
import me.shin1gamix.voidchest.utilities.MaterialUtil;
import me.shin1gamix.voidchest.utilities.MessagesUtil;
import me.shin1gamix.voidchest.utilities.Utils;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidItemManager;
import me.shin1gamix.voidchest.utilities.voidmanager.VoidItemManager.VoidChestItemCache;

public final class VoidCommand implements CommandExecutor {

	private final VoidChestPlugin core;

	public VoidCommand(final VoidChestPlugin core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(final CommandSender cs, final Command cmd, final String lb, final String[] args) {

		if (args.length == 0) {
			this.sendHelp(cs);
			return true;
		}

		switch (args[0].toLowerCase()) {
		case "reload":
			this.reload(cs);
			break;
		case "toggle":
			this.toggle(cs);
			break;
		case "stats":
			this.showStats(cs, args.length == 1 ? null : args[1]);
			break;
		case "list":
			this.showVoidChests(cs);
			break;
		case "boost":
			this.boost(cs, args);
			break;
		case "mode":
			this.mode(cs);
			break;
		default:
			this.attemptGiveChest(cs, args);
			break;
		}

		return true;
	}

	private void mode(CommandSender cs) {
		if (!cs.hasPermission("voidchest.mode")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}
		VoidMode vm = this.core.getVoidEconomyManager().getCurrentMode();
		final String name = vm.getName();
		final Map<String, String> map = Maps.newHashMap();
		map.put("%mode%", name);
		map.put("%mode_name%", this.core.getVoidEconomyManager().getVoidEconomy().getName());
		MessagesUtil.VOIDCHEST_MODE.msg(cs, map, false);

	}

	private void boost(CommandSender cs, String[] args) {

		if (!cs.hasPermission("voidchest.boost")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}

		/*
		 * Player just typed /vc boost, let's simply tell them to specify a user and the
		 * boost amount.
		 */
		if (args.length != 4) {
			MessagesUtil.BOOST_USAGE.msg(cs);
			return;
		}

		final Map<String, String> map = Maps.newHashMap();

		final String targetInput = args[1];
		final Player target = Bukkit.getPlayer(targetInput);

		/* Target is offline */
		if (target == null) {
			map.put("%player%", args[1]);
			MessagesUtil.PLAYER_OFFLINE.msg(cs, map, false);
			return;
		}

		final String boostInput = args[2];
		if (!Utils.isDouble(boostInput)) {
			MessagesUtil.BOOST_USAGE.msg(cs);
			return;
		}

		final String boostTimeInput = args[3];
		if (!Utils.isLong(boostTimeInput)) {
			MessagesUtil.BOOST_USAGE.msg(cs);
			return;
		}

		final double boost = Double.parseDouble(boostInput);
		if (boost <= 1d) {
			MessagesUtil.BOOST_LIMIT.msg(cs);
			return;
		}

		final PlayerData data = PlayerDataManager.getInstance().loadPlayerData(target);

		final long boostTime = Long.parseLong(boostTimeInput);

		if (boostTime < 1) {
			MessagesUtil.BOOST_TIME_LIMIT.msg(cs);
			return;
		}

		final long result = System.currentTimeMillis() + (1000 * boostTime);
		System.out.println(result);
		data.setBoostTime(result);
		data.setBooster(boost);

		map.put("%timeleft%", Utils.convertSeconds(boostTime));
		map.put("%player%", target.getName());
		map.put("%boost%", data.getBoosterString());
		MessagesUtil.BOOST_APPLIED.msg(cs, map, false);

	}

	private void showVoidChests(CommandSender cs) {

		if (!cs.hasPermission("voidchest.list")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}

		VoidItemManager vim = VoidItemManager.getInstance();
		final Set<String> voidChestNames = vim.getItemCache().keySet();

		String vcList = voidChestNames.isEmpty() ? "none" : String.join(", ", voidChestNames);
		final Map<String, String> map = Maps.newHashMap();
		map.put("%voidchests%", vcList);
		map.put("%amount%", String.valueOf(voidChestNames.size()));
		MessagesUtil.VOIDCHEST_LIST.msg(cs, map, false);
	}

	private void showStats(final CommandSender cs, String targetInput) {
		final Map<String, String> map = Maps.newHashMap();

		Player target = null;

		if (targetInput == null) {

			if (!cs.hasPermission("voidchest.stats")) {
				MessagesUtil.NO_PERMISSION.msg(cs);
				return;
			}

			if (!(cs instanceof Player)) {
				return;
			}
			target = (Player) cs;
			PlayerData data = PlayerDataManager.getInstance().loadPlayerData(target.getUniqueId(), target.getName());

			map.put("%voidchests%", String.valueOf(data.getVoidStorages().size()));
			map.put("%money%",
					Utils.formatNumber(data.getVoidStorages().stream().mapToDouble(VoidStorage::getMoney).sum()));
			map.put("%itemssold%",
					Utils.formatNumber(data.getVoidStorages().stream().mapToDouble(VoidStorage::getItemsSold).sum()));
			map.put("%itemspurged%",
					Utils.formatNumber(data.getVoidStorages().stream().mapToDouble(VoidStorage::getItemsPurged).sum()));
			map.put("%booster%", data.getBoosterString());
			map.put("%timeleft%", data.getBoosterTimeLeft());

			MessagesUtil.STATS.msg(target, map, false);
			return;
		}

		if (cs instanceof Player && cs.getName().equalsIgnoreCase(targetInput)) {
			this.showStats(cs, null);
			return;
		}

		if (!cs.hasPermission("voidchest.stats.other")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}

		final Optional<PlayerData> opt = PlayerDataManager.getInstance().getPlayerDatas().values().stream()
				.filter(data -> targetInput.equalsIgnoreCase(data.getOwner().getName())).findAny();

		if (!opt.isPresent()) {
			map.put("%player%", targetInput);
			MessagesUtil.PLAYER_INVALID.msg(cs, map, false);
			return;
		}

		PlayerData data = opt.get();

		map.put("%voidchests%", String.valueOf(data.getVoidStorages().size()));
		map.put("%money%",
				Utils.formatNumber(data.getVoidStorages().stream().mapToDouble(VoidStorage::getMoney).sum()));
		map.put("%itemssold%",
				Utils.formatNumber(data.getVoidStorages().stream().mapToDouble(VoidStorage::getItemsSold).sum()));
		map.put("%itemspurged%",
				Utils.formatNumber(data.getVoidStorages().stream().mapToDouble(VoidStorage::getItemsPurged).sum()));
		map.put("%booster%", data.getBoosterString());
		map.put("%timeleft%", data.getBoosterTimeLeft());
		map.put("%player%", data.getOwner().getName());

		MessagesUtil.STATS_OTHER.msg(cs, map, false);
	}

	private void sendHelp(final CommandSender cs) {
		if (!cs.hasPermission("voidchest.help")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}
		MessagesUtil.HELP_FORMAT.msg(cs);
	}

	private void toggle(final CommandSender cs) {
		if (!(cs instanceof Player)) {
			return;
		}

		final Player player = (Player) cs;

		if (!player.hasPermission("voidchest.toggle")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}

		final PlayerData data = PlayerDataManager.getInstance().loadPlayerData(player.getUniqueId(), player.getName());
		final boolean isSend = data.isSendMessage();
		if (isSend) {
			MessagesUtil.SELL_MESSAGE_OFF.msg(player);
		} else {
			MessagesUtil.SELL_MESSAGE_ON.msg(player);
		}
		data.setSendMessage(!isSend);
	}

	private void reload(final CommandSender cs) {

		if (!cs.hasPermission("voidchest.reload")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}
		if (this.core.isHdSupport()) {
			HologramsAPI.getHolograms(this.core).forEach(holo -> holo.delete());
		}

		CFG.reloadFiles();
		VoidItemManager.getInstance().cacheItems();
		final FileManager fm = FileManager.getInstance();
		MessagesUtil.repairPaths(fm.getMessages());

		Bukkit.getServicesManager().unregisterAll(this.core);
		this.core.getVoidEconomyManager().hookVoidEcon();

		PlayerDataManager.getInstance().savePlayerDatas(true, true);
		fm.getPlayerBase().saveFile();
		PlayerDataManager.getInstance().loadPlayerDatas();

		this.core.attemptStartSaving();
		this.core.attemptStartPurging();

		MessagesUtil.PLUGIN_RELOAD.msg(cs);

		if (this.core.getVoidEconomyManager().getCurrentMode() == VoidMode.CRAFT_VOIDCHEST) {
			final List<String> debug = Lists.newArrayList();
			debug.add("An attempt to detect invalid material types in shop.yml is initiated...");

			FileConfiguration shop = fm.getShop().getFile();
			String result = shop.getConfigurationSection("Items").getKeys(false).stream()
					.filter(name -> !MaterialUtil.fromString(name).isPresent()).collect(Collectors.joining(", "));

			debug.add("Invalid materials: " + (result.isEmpty() ? "none" : result));
			Utils.debug(this.core, debug);
		}

	}

	private void attemptGiveChest(CommandSender cs, String[] args) {

		if (!cs.hasPermission("voidchest.give")) {
			MessagesUtil.NO_PERMISSION.msg(cs);
			return;
		}

		final Optional<VoidChestItemCache> cacheOpt = VoidItemManager.getInstance().getCachedItem(args[0]);
		final Map<String, String> map = Maps.newHashMap();

		if (!cacheOpt.isPresent()) {
			map.put("%voidchest%", args[0]);
			MessagesUtil.VOIDCHEST_GIVE_INVALID.msg(cs, map, false);
			this.showVoidChests(cs);
			return;
		}

		final VoidChestItemCache ic = cacheOpt.get();

		if (args.length == 1) {
			if (cs instanceof Player) {
				this.giveChest(cs, ic, (Player) cs, "1");
			}
			return;
		}

		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
			map.put("%player%", args[1]);
			MessagesUtil.PLAYER_OFFLINE.msg(cs, map, false);
			return;
		}

		this.giveChest(cs, ic, target, args.length != 3 ? null : args[2]);

	}

	private void giveChest(final CommandSender cs, final VoidChestItemCache ic, final Player target,
			String amountInput) {
		final Map<String, String> replace = Maps.newHashMap();

		/* No number for the chest is set or is invalid. */
		if (amountInput == null || !Utils.isInt(amountInput)) {
			amountInput = "1";
		}

		int amount = Math.abs(Integer.parseInt(amountInput));
		amount = amount == 0 ? 1 : amount;
		final ItemStack[] items = ic.getVoidChestItem(amount);

		final Map<Integer, ItemStack> map = target.getInventory().addItem(items);
		for (final ItemStack item : map.values()) {
			target.getWorld().dropItemNaturally(target.getLocation(), item);
		}

		replace.put("%player%", target.getName());
		replace.put("%amount%", NumberFormat.getInstance(Locale.US).format(amount));
		replace.put("%voidchest%", ic.getName());
		MessagesUtil.VOIDCHEST_GIVE.msg(cs, replace, false);
		MessagesUtil.VOIDCHEST_RECEIVE.msg(target, replace, false);
	}

}
