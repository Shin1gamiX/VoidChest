package me.shin1gamix.voidchest.nbtapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

import me.shin1gamix.voidchest.nbtapi.util.MinecraftVersion;

public final class NBTReflectionUtil {

	public static Object getItemRootNBTTagCompound(final Object nmsitem) {
		final Class clazz = nmsitem.getClass();
		try {
			final Method method = clazz.getMethod("getTag", (Class[]) new Class[0]);
			final Object answer = method.invoke(nmsitem, new Object[0]);
			return answer;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object convertNBTCompoundtoNMSItem(final NBTCompound nbtcompound) {
		final Class clazz = ClassWrapper.NMS_ITEMSTACK.getClazz();
		try {
			if (MinecraftVersion.getVersion().getVersionId() >= MinecraftVersion.MC1_12_R1.getVersionId()) {
				final Constructor<?> constructor = clazz.getConstructor(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz());
				constructor.setAccessible(true);
				return constructor.newInstance(nbtcompound.getCompound());
			}
			final Method method = clazz.getMethod("createStack", ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz());
			method.setAccessible(true);
			return method.invoke(null, nbtcompound.getCompound());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getSubNBTTagCompound(final Object compound, final String name) {
		final Class c = compound.getClass();
		try {
			final Method method = c.getMethod("getCompound", String.class);
			final Object answer = method.invoke(compound, name);
			return answer;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addNBTTagCompound(final NBTCompound comp, final String name) {
		if (name == null) {
			remove(comp, name);
			return;
		}
		Object nbttag = comp.getCompound();
		if (nbttag == null) {
			nbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(nbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("set", String.class,
					ClassWrapper.NMS_NBTBASE.getClazz());
			method.invoke(workingtag, name, ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz().newInstance());
			comp.setCompound(nbttag);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException ex) {
			ex.printStackTrace();
		}
	}

	public static Boolean valideCompound(final NBTCompound comp) {
		Object root = comp.getCompound();
		if (root == null) {
			root = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		return gettoCompount(root, comp) != null;
	}

	private static Object gettoCompount(Object nbttag, NBTCompound comp) {
		final Stack<String> structure = new Stack<String>();
		while (comp.getParent() != null) {
			structure.add(comp.getName());
			comp = comp.getParent();
		}
		while (!structure.isEmpty()) {
			nbttag = getSubNBTTagCompound(nbttag, structure.pop());
			if (nbttag == null) {
				return null;
			}
		}
		return nbttag;
	}

	public static String getContent(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return null;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("get", String.class);
			return method.invoke(workingtag, key).toString();
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void set(final NBTCompound comp, final String key, final Object val) {
		if (val == null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			new Throwable("InvalideCompound").printStackTrace();
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		try {
			final Method method = workingtag.getClass().getMethod("set", String.class,
					ClassWrapper.NMS_NBTBASE.getClazz());
			method.invoke(workingtag, key, val);
			comp.setCompound(rootnbttag);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

	public static void remove(final NBTCompound comp, final String key) {
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		ReflectionMethod.COMPOUND_REMOVE_KEY.run(workingtag, key);
		comp.setCompound(rootnbttag);
	}

	public static void setData(final NBTCompound comp, final ReflectionMethod type, final String key,
			final Object data) {
		if (data == null) {
			remove(comp, key);
			return;
		}
		Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			rootnbttag = ObjectCreator.NMS_NBTTAGCOMPOUND.getInstance(new Object[0]);
		}
		if (!valideCompound(comp)) {
			return;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		type.run(workingtag, key, data);
		comp.setCompound(rootnbttag);
	}

	public static Object getData(final NBTCompound comp, final ReflectionMethod type, final String key) {
		final Object rootnbttag = comp.getCompound();
		if (rootnbttag == null) {
			return null;
		}
		if (!valideCompound(comp)) {
			return null;
		}
		final Object workingtag = gettoCompount(rootnbttag, comp);
		return type.run(workingtag, key);
	}

	private enum ObjectCreator {

		NMS_NBTTAGCOMPOUND(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz(), (Class<?>[]) new Class[0]);
		private Constructor<?> construct;

		private ObjectCreator(final Class<?> clazz, final Class<?>[] args) {
			try {
				this.construct = clazz.getConstructor(args);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

		public Object getInstance(final Object... args) {
			try {
				return this.construct.newInstance(args);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
