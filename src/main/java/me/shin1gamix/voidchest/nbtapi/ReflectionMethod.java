package me.shin1gamix.voidchest.nbtapi;

import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;

import me.shin1gamix.voidchest.nbtapi.util.MinecraftVersion;

public enum ReflectionMethod {

	COMPOUND_SET_STRING(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz(),

			(Class<?>[]) new Class[] { String.class, String.class },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "setString")),

	COMPOUND_GET_STRING(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz(),

			(Class<?>[]) new Class[] { String.class },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "getString")),

	COMPOUND_REMOVE_KEY(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz(),

			(Class<?>[]) new Class[] { String.class },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "remove")),

	COMPOUND_HAS_KEY(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz(),

			(Class<?>[]) new Class[] { String.class },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "hasKey")),

	ITEMSTACK_SET_TAG(ClassWrapper.NMS_ITEMSTACK.getClazz(),

			(Class<?>[]) new Class[] { ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz() },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "setTag")),

	ITEMSTACK_NMSCOPY(ClassWrapper.CRAFT_ITEMSTACK.getClazz(),

			(Class<?>[]) new Class[] { ItemStack.class },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "asNMSCopy")),

	ITEMSTACK_BUKKITMIRROR(ClassWrapper.CRAFT_ITEMSTACK.getClazz(),

			(Class<?>[]) new Class[] { ClassWrapper.NMS_ITEMSTACK.getClazz() },

			MinecraftVersion.MC1_7_R4,

			new Since(MinecraftVersion.MC1_7_R4, "asCraftMirror")),

	;

	private MinecraftVersion removedAfter;
	private Since targetVersion;
	private Method method;
	private boolean loaded;
	private boolean compatible;

	private ReflectionMethod(final Class<?> targetClass, final Class<?>[] args, final MinecraftVersion addedSince,
			final MinecraftVersion removedAfter, final Since... methodnames) {
		this.loaded = false;
		this.compatible = false;
		this.removedAfter = removedAfter;
		final MinecraftVersion server = MinecraftVersion.getVersion();
		if (server.compareTo(addedSince) < 0
				|| (this.removedAfter != null && server.getVersionId() > this.removedAfter.getVersionId())) {
			return;
		}
		this.compatible = true;
		Since target = methodnames[0];
		for (final Since s : methodnames) {
			if (s.version.getVersionId() <= server.getVersionId()
					&& target.version.getVersionId() < s.version.getVersionId()) {
				target = s;
			}
		}
		this.targetVersion = target;
		try {
			(this.method = targetClass.getMethod(this.targetVersion.name, args)).setAccessible(true);
			this.loaded = true;
		} catch (NullPointerException | NoSuchMethodException | SecurityException ex3) {
			ex3.printStackTrace();
		}
	}

	private ReflectionMethod(final Class<?> targetClass, final Class<?>[] args, final MinecraftVersion addedSince,
			final Since... methodnames) {
		this(targetClass, args, addedSince, (MinecraftVersion) null, methodnames);
	}

	public Object run(final Object target, final Object... args) {
		try {
			return this.method.invoke(target, args);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public boolean isCompatible() {
		return this.compatible;
	}

	public static class Since {
		public final MinecraftVersion version;
		public final String name;

		public Since(final MinecraftVersion version, final String name) {
			this.version = version;
			this.name = name;
		}
	}
}