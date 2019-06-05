package me.shin1gamix.voidchest.utilities;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public final class Utils {

	private Utils() {
		throw new UnsupportedOperationException();
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */

	/**
	 * 
	 * @param input
	 *            The seconds to be converted into days, hours, minutes, seconds.
	 * @return String -> The seconds converted into d,h,m,s.
	 */
	public static String convertSeconds(final long input) {
		Validate.isTrue(input > 0, "Why are you even trying to convert: " + input + "?");

		/* Days */
		final int days = (int) TimeUnit.SECONDS.toDays(input);
		StringBuilder builder = new StringBuilder(days > 0 ? pluralize("^# day^s", days) : "");

		/* Hours */
		final int hours = (int) (TimeUnit.SECONDS.toHours(input) - TimeUnit.DAYS.toHours(days));
		builder.append(hours > 0 ? (builder.toString().equals("") ? "" : ", ") + pluralize("^# hour^s", hours) : "");

		/* Minutes */
		final int minutes = (int) (TimeUnit.SECONDS.toMinutes(input) - TimeUnit.HOURS.toMinutes(hours)
				- TimeUnit.DAYS.toMinutes(days));
		builder.append(
				minutes > 0 ? (builder.toString().equals("") ? "" : ", ") + pluralize("^# minute^s", minutes) : "");

		/* Seconds */
		final int seconds = (int) (TimeUnit.SECONDS.toSeconds(input) - TimeUnit.MINUTES.toSeconds(minutes)
				- TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));
		builder.append(
				seconds > 0 ? (builder.toString().equals("") ? "" : ", ") + pluralize("^# second^s", seconds) : "");
		/* Result */
		return builder.toString();
	}

	private static String pluralize(String text, int value) {
		return text.replace("^s", value > 1 ? "s" : "").replace("^#", String.valueOf(value));
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */
	public static boolean isLong(final String input) {
		return Longs.tryParse(StringUtils.deleteWhitespace(input)) != null;
	}

	public static boolean isDouble(final String input) {
		return Doubles.tryParse(StringUtils.deleteWhitespace(input)) != null;
	}

	public static boolean isInt(final String input) {
		return Ints.tryParse(StringUtils.deleteWhitespace(input)) != null;
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */
	public static boolean isList(final FileConfiguration file, final String path) {
		return isList(file.get(path));
	}

	public static boolean isList(final Object obj) {
		return obj instanceof ArrayList;
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */

	public static void broadcastMsg(final String input) {
		Bukkit.broadcastMessage(colorize(input));
	}

	public static void broadcastMsg(final List<String> input) {
		input.forEach(Utils::broadcastMsg);
	}

	public static void broadcastMsg(final Object input) {
		broadcastMsg(String.valueOf(input));
	}

	public static void printMsg(final String input) {
		Bukkit.getConsoleSender().sendMessage(colorize(input));
	}

	public static void printMsg(final List<String> input) {
		input.forEach(Utils::printMsg);
	}

	public static void printMsg(final Object input) {
		printMsg(String.valueOf(input));
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */

	public static void msg(final CommandSender target, final String message) {
		Validate.notNull(target, "The target can't be null");
		if (message == null) {
			return;
		}
		target.sendMessage(colorize(message));
	}

	public static void msg(final CommandSender target, final String[] message) {
		Validate.notNull(target, "The target can't be null");
		if (message == null || message.length == 0) {
			return;
		}
		Validate.noNullElements(message, "The string array can't have null elements.");
		target.sendMessage(colorize(message));
	}

	public static void msg(final CommandSender target, final List<String> message) {
		Validate.notNull(target, "The target can't be null");
		if (message == null || message.isEmpty()) {
			return;
		}
		Validate.noNullElements(message, "The list can't have null elements.");
		msg(target, message.stream().toArray(String[]::new));
	}

	/* ----------------------------------------------------------------- */

	public static void msg(final CommandSender target, final String message, final Map<String, String> map,
			final boolean ignoreCase) {
		msg(target, placeHolder(message, map, ignoreCase));
	}

	public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map,
			final boolean ignoreCase) {
		msg(target, placeHolder(message, map, ignoreCase));
	}

	public static void msg(final CommandSender target, final String[] message, final Map<String, String> map,
			final boolean ignoreCase) {
		msg(target, placeHolder(message, map, ignoreCase));
	}

	public static void msg(final CommandSender target, final FileConfiguration file, final String path) {
		msg(target, file, path, null, false);
	}

	public static void msg(final CommandSender target, final FileConfiguration file, final String path,
			final Map<String, String> map, final boolean replace) {
		Validate.notNull(file, "The file can't be null");
		Validate.notNull(file, "The path can't be null");
		if (isList(file, path)) {
			msg(target, file.getStringList(path), map, replace);
		} else {
			msg(target, file.getString(path), map, replace);
		}
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */

	public static String placeHolder(String str, final Map<String, String> map, final boolean ignoreCase) {
		Validate.notNull(str, "The string can't be null!");
		if (map == null) {
			return new String(str);
		}
		for (final Entry<String, String> entr : map.entrySet()) {
			str = ignoreCase ? replaceIgnoreCase(str, entr.getKey(), entr.getValue())
					: str.replace(entr.getKey(), entr.getValue());
		}
		return new String(str);
	}

	private static String replaceIgnoreCase(final String text, String searchString, final String replacement) {

		if (text == null || text.length() == 0) {
			return text;
		}
		if (searchString == null || searchString.length() == 0) {
			return text;
		}
		if (replacement == null) {
			return text;
		}

		int max = -1;

		final String searchText = text.toLowerCase();
		searchString = searchString.toLowerCase();
		int start = 0;
		int end = searchText.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		final int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase < 0 ? 0 : increase;
		increase *= 16;

		final StringBuilder buf = new StringBuilder(text.length() + increase);
		while (end != -1) {
			buf.append(text, start, end).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = searchText.indexOf(searchString, start);
		}
		return buf.append(text, start, text.length()).toString();
	}

	public static String[] placeHolder(final String[] array, final Map<String, String> map, final boolean ignoreCase) {
		Validate.notNull(array, "The string array can't be null!");
		Validate.noNullElements(array, "The string array can't have null elements!");
		final String[] newarr = Arrays.copyOf(array, array.length);
		if (map == null) {
			return newarr;
		}
		for (int i = 0; i < newarr.length; i++) {
			newarr[i] = placeHolder(newarr[i], map, ignoreCase);
		}
		return newarr;
	}

	public static List<String> placeHolder(final List<String> coll, final Map<String, String> map,
			final boolean ignoreCase) {
		Validate.notNull(coll, "The string collection can't be null!");
		Validate.noNullElements(coll, "The string collection can't have null elements!");
		if (map == null) {
			return coll;
		}
		return coll.stream().map(str -> placeHolder(str, map, ignoreCase)).collect(Collectors.toList());
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */

	/**
	 * Returns a translated string.
	 * 
	 * @param msg
	 *            The message to be translated
	 * 
	 * @return A translated message
	 */
	public static String colorize(final String msg) {
		Validate.notNull(msg, "The string can't be null!");
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Returns a translated string array.
	 * 
	 * @param msg
	 *            The messages to be translated
	 * 
	 * @return A translated message array
	 */
	public static String[] colorize(final String[] array) {
		Validate.notNull(array, "The string array can't be null!");
		Validate.noNullElements(array, "The string array can't have null elements!");
		final String[] newarr = Arrays.copyOf(array, array.length);
		for (int i = 0; i < newarr.length; i++) {
			newarr[i] = colorize(newarr[i]);
		}
		return newarr;
	}

	/**
	 * Returns a translated string collection.
	 * 
	 * @param coll
	 *            The collection to be translated
	 * 
	 * @return A translated message
	 */
	public static List<String> colorize(final List<String> coll) {
		Validate.notNull(coll, "The string collection can't be null!");
		Validate.noNullElements(coll, "The string collection can't have null elements!");
		final List<String> newColl = Lists.newArrayList(coll);
		newColl.replaceAll(Utils::colorize);
		return newColl;
	}

	/* ----------------------------------------------------------------- */

	//

	/* ----------------------------------------------------------------- */

	public static void debug(final JavaPlugin plugin, final Map<String, String> map, String... messages) {
		System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		System.out.println("=");
		final String name, desc;
		name = plugin.getDescription().getName() + " ";
		desc = plugin.getDescription().getVersion();
		System.out.println("           " + name + desc);
		System.out.println("=");
		for (final String msg : messages) {
			System.out.println(placeHolder(msg, map, false));
		}
		System.out.println("=");
		System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
	}

	public static void debug(final JavaPlugin plugin, String... messages) {
		debug(plugin, null, messages);
	}

	public static void debug(final JavaPlugin plugin, List<String> messages) {
		debug(plugin, null, messages.stream().toArray(String[]::new));
	}

	public static ItemStack[] getItems(final ItemStack item, int amount) {
		if (amount <= 64) {
			item.setAmount(amount < 1 ? 1 : amount);
			return new ItemStack[] { item };
		}
		final List<ItemStack> resultItems = Lists.newArrayList();
		do {
			item.setAmount(Math.min(amount, 64));
			resultItems.add(new ItemStack(item));
			amount = amount >= 64 ? amount - 64 : 0;
		} while (amount != 0);
		return resultItems.stream().toArray(ItemStack[]::new);
	}

	private static String formatNumber(Locale lang, double input) {
		Validate.notNull(lang);
		return NumberFormat.getInstance(lang).format(input);
	}

	public static String formatNumber(double input) {
		return formatNumber(Locale.US, input);
	}
}
