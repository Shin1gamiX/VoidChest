package me.shin1gamix.voidchest.utilities;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.shin1gamix.voidchest.configuration.CFG;

public enum MessagesUtil {

	NO_PERMISSION("Messages.No-Permission", "&c&l(!) &cYou do not have the correct permissions to do this!"),

	PLUGIN_RELOAD("Messages.Reload", "&a&l(!) &aAll data have been reloaded successfuly."),

	// ----------------------------------------------------------- //

	SELL_INVENTORY("Sell.Inventory-sell", "&a&l+ $%money% &7(Sold %itemssold%, Purged %itemspurged%) &7[VOID CHEST]"),

	SELL_MESSAGE_ON("Sell.Notify-sell.enabled", "&a&l(!) &aYou have enabled sell notifications."),

	SELL_MESSAGE_OFF("Sell.Notify-sell.disabled", "&c&l(!) &cYou have disabled sell notifications."),

	// ----------------------------------------------------------- //

	PLAYER_OFFLINE("Player.Offline", "&c&l(!) &4%player%&c is not found online."),

	PLAYER_INVALID("Player.Invalid", "&c&l(!) &4%player%&c couldn't be found."),

	// ----------------------------------------------------------- //

	VOIDCHEST_LIMIT_REACHED("VoidChest.Limit-Reached",
			"&c&l(!) &cYou have reached the maximum limit for placed void chests."),

	VOIDCHEST_GIVE("VoidChest.Give", "&a&l(!) &aYou have given &7%player% %amount%x %voidchest% &3voidchest(s)&a."),

	VOIDCHEST_GIVE_INVALID("VoidChest.Give-Invalid", "&c&l(!) &cNo voidchest by the name &e%voidchest% &cexists."),

	VOIDCHEST_RECEIVE("VoidChest.Receive", "&a&l(!) &aYou have received &7%amount%x %voidchest% &3voidchest(s)&a."),

	VOIDCHEST_LIST("VoidChest.List", "&7&l(!) &7Currently &e%amount% &7voidchests loaded: &e%voidchests%"),

	VOIDCHEST_MODE("VoidChest.Mode",
			"&7&l(!) &7Current VoidChest mode being used is: &e%mode% &7with the name: &e%mode_name%"),

	// ----------------------------------------------------------- //

	BOOST_USAGE("Boost.Usage",
			"&7&l(!) &7Incorrect usage, please use this foramt: &c/vc boost <player> <boost_amount> <time_in_seconds>"),

	BOOST_LIMIT("Boost.Limit", "&c&l(!) &cYou need to specify a boost higher than 1.0!"),

	BOOST_APPLIED("Boost.Applied",
			"&7&l(!) &7A boost of &e%boost% &7has been applied to &e%player% &7for &e%timeleft%"),

	BOOST_TIME_LIMIT("Boost.Time-Limit", "&c&l(!) &cYou need to specify a boost timer higher than 0 seconds!"),

	// ----------------------------------------------------------- //

	STATS("Stats.Self",

			"", "&7&l&m------==[&6 Your Stats &7&l&m]==------",

			"",

			"&7Total voidchests: &6%voidchests%",

			"&7Booster: &a%booster%",

			"&7Booster timeleft: &a%timeleft%",

			"&7Money made: &a$%money%",

			"&7Items sold: &6%itemssold%",

			"&7Items purged: &6%itemspurged%",

			"",

			"&7&l&m------==[&6 Your Stats &7&l&m]==------"

	),

	STATS_OTHER("Stats.Other",

			"", "&7&l&m------==[&6 %player% Stats &7&l&m]==------",

			"",

			"&7Total voidchests: &6%voidchests%",

			"&7Booster: &a%booster%",

			"&7Booster timeleft: &a%timeleft%",

			"&7Money made: &a$%money%",

			"&7Items sold: &6%itemssold%",

			"&7Items purged: &6%itemspurged%",

			"",

			"&7&l&m------==[&6 %player% Stats &7&l&m]==------"

	),

	// ----------------------------------------------------------- //

	HELP_FORMAT("Messages.Help",

			"",

			"&c&l(!) &cVoidChest Commands:",

			" &8» &7/vc &ereload &7- &oReloads all plugin files.",

			" &8» &7/vc &etoggle &7- &oPrevents sell messages from being sent.",

			" &8» &7/vc &estats &o[player] &7- &oShows someone else statistics or yours.",

			" &8» &7/vc &elist &7- &oList all available voidchest names.",

			" &8» &7/vc &emode &7- &oDisplays the current voidchest mode being used.",

			" &8» &7/vc &e<voidchest> &o[player] [amount] &7- &oGive someone some voidchests",

			" &8» &7/vc &eboost &o<player> <boost_amount> <time_in_seconds> &7- &oBoost a player's income!",

			""),

	;

	/** @see #getMessages() */
	private String[] messages;
	private final String path;

	MessagesUtil(final String path, final String... messages) {
		this.messages = messages;
		this.path = path;
	}

	/**
	 * @return boolean -> Whether or not the messages array contains more than 1
	 *         element. If true, it's more than 1 message/string.
	 */
	private boolean isMultiLined() {
		return this.messages.length > 1;
	}

	/**
	 * @param cfg
	 * @see #setPathToFile(CFG, MessagesUtil)
	 * @see #setMessageToFile(CFG, MessagesUtil)
	 */
	public static void repairPaths(final CFG cfg) {

		boolean changed = false;

		for (MessagesUtil enumMessage : MessagesUtil.values()) {

			/* Does our file contain our path? */
			if (cfg.getFile().contains(enumMessage.getPath())) {
				/* It does! Let's set our message to be our path. */
				setPathToMessage(cfg, enumMessage);
				continue;
			}

			/* Since the path doesn't exist, let's set our default message to that path. */
			setMessageToPath(cfg, enumMessage);
			if (!changed) {
				changed = true;
			}

		}
		/* Save the custom yaml file. */
		if (changed) {
			cfg.saveFile();
		}
	}

	/**
	 * Sets a message from the MessagesX enum to the file.
	 * 
	 * @param cfg
	 * @param enumMessage
	 */
	private static void setMessageToPath(final CFG cfg, final MessagesUtil enumMessage) {
		/* Is our message multilined? */
		if (enumMessage.isMultiLined()) {
			/* Set our message (array) to the path. */
			cfg.getFile().set(enumMessage.getPath(), enumMessage.getMessages());
		} else {
			/* Set our message (string) to the path. */
			cfg.getFile().set(enumMessage.getPath(), enumMessage.getMessages()[0]);
		}
	}

	/**
	 * Sets the current MessagesX messages to a string/list retrieved from the
	 * messages file.
	 * 
	 * @param cfg
	 * @param enumMessage
	 */
	private static void setPathToMessage(final CFG cfg, final MessagesUtil enumMessage) {
		/* Is our path a list? */
		if (Utils.isList(cfg.getFile(), enumMessage.getPath())) {
			/* Set our default message to be the path's message. */
			enumMessage.setMessages(cfg.getFile().getStringList(enumMessage.getPath()).toArray(new String[0]));
		} else {
			/* Set our default message to be the path's message. */
			enumMessage.setMessages(cfg.getFile().getString(enumMessage.getPath()));
		}
	}

	/**
	 * @return the path -> The path of the enum in the file.
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return the messages -> The messages array that contains all strings.
	 */
	public String[] getMessages() {
		return this.messages;
	}

	/**
	 * Sets the current messages to a different string array.
	 * 
	 * @param messages
	 */
	public void setMessages(final String[] messages) {
		this.messages = messages;
	}

	/**
	 * Sets the string message to a different string assuming that the array has
	 * only 1 element.
	 * 
	 * @param messages
	 */
	public void setMessages(final String messages) {
		this.messages[0] = messages;
	}

	/**
	 * @param target
	 * @see #msg(CommandSender, Map, boolean)
	 */
	public void msg(final CommandSender target) {
		msg(target, null, false);
	}

	/**
	 * Sends a translated message to a target commandsender with placeholders gained
	 * from a map. If the map is null, no placeholder will be set and it will still
	 * execute.
	 * 
	 * @param target
	 * @param map
	 */
	public void msg(final CommandSender target, final Map<String, String> map, final boolean ignoreCase) {
		if (this.isMultiLined()) {
			Utils.msg(target, this.getMessages(), map, ignoreCase);
		} else {
			Utils.msg(target, this.getMessages()[0], map, ignoreCase);
		}
	}

	/**
	 * Sends a translated message to a target commandsender with placeholders gained
	 * from a map. If the map is null, no placeholder will be set and it will still
	 * execute.
	 * 
	 * @param target
	 * @param map
	 */
	public void msgAll() {
		if (this.isMultiLined()) {
			Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()));
		} else {
			Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()[0]));
		}
	}

	/**
	 * Sends a translated message to a target commandsender with placeholders gained
	 * from a map. If the map is null, no placeholder will be set and it will still
	 * execute.
	 * 
	 * @param target
	 * @param map
	 */
	public void msgAll(final Map<String, String> map, final boolean ignoreCase) {
		if (this.isMultiLined()) {
			Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages(), map, ignoreCase));
		} else {
			Bukkit.getOnlinePlayers().forEach(target -> Utils.msg(target, this.getMessages()[0], map, ignoreCase));
		}
	}

}
