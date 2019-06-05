package me.shin1gamix.voidchest.utilities;

import me.shin1gamix.voidchest.configuration.FileManager;

public class DebugUtil {

	public static boolean isDebugEnabled() {
		return FileManager.getInstance().getOptions().getFile().getBoolean("Debugging", false);
	}
}
