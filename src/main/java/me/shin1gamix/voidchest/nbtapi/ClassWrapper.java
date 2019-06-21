package me.shin1gamix.voidchest.nbtapi;

import org.bukkit.Bukkit;

public enum ClassWrapper {
	CRAFT_ITEMSTACK("org.bukkit.craftbukkit.", ".inventory.CraftItemStack"),

	NMS_NBTBASE("net.minecraft.server.", ".NBTBase"),

	NMS_ITEMSTACK("net.minecraft.server.", ".ItemStack"),

	NMS_NBTTAGCOMPOUND("net.minecraft.server.", ".NBTTagCompound"),

	;
	private Class<?> clazz;

	private ClassWrapper(final String pre, final String suffix) {
			final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
			try {
				this.clazz = Class.forName(pre + version + suffix);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		
	}

	public Class<?> getClazz() {
		return this.clazz;
	}
}
