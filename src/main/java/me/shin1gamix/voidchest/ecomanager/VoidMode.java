package me.shin1gamix.voidchest.ecomanager;

import javax.annotation.Nullable;

public enum VoidMode {
	VOIDCHEST("VoidChest"),

	SHOPGUIPLUS("ShopGUIPlus"),

	ESSENTIALS("Essentials"),

	CUSTOM("Custom"),

	;

	private final String name;

	VoidMode(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public @Nullable static VoidMode getByName(String input) {
		if (input == null) {
			throw new IllegalArgumentException("You can't pass a null arguement to get a VoidMode.");
		}

		for (VoidMode mode : values()) {
			if (input.equalsIgnoreCase(mode.getName())) {
				return mode;
			}

		}
		return null;
	}
}