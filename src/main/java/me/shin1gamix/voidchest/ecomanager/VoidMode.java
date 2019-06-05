package me.shin1gamix.voidchest.ecomanager;

import java.util.Optional;

public enum VoidMode {
	CRAFT_VOIDCHEST("VoidChest"),

	CRAFT_SHOPGUIPLUS("ShopGUIPlus"),

	CRAFT_ESSENTIALS("Essentials"),

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

	
	
	public static Optional<VoidMode> getByName(String input) {
		if (input == null) {
			return Optional.empty();
		}

		for (VoidMode mode : values()) {
			if (input.equalsIgnoreCase(mode.getName())) {
				return Optional.of(mode);
			}

		}
		return Optional.empty();

	}
}