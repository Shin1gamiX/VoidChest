package me.shin1gamix.voidchest.utilities;

import me.shin1gamix.voidchest.configuration.FileManager;

public final class DebugUtil {

	private DebugUtil() {
		throw new UnsupportedOperationException();
	}

	public static boolean isDebugEnabled() {
		return FileManager.getInstance().getOptions().getFile().getBoolean("Debugging", false);
	}
}
