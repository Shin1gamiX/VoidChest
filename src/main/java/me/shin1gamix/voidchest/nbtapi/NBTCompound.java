package me.shin1gamix.voidchest.nbtapi;

public class NBTCompound {
	private String compundName;
	private NBTCompound parent;

	protected NBTCompound(final NBTCompound owner, final String name) {
		this.compundName = name;
		this.parent = owner;
	}

	public String getName() {
		return this.compundName;
	}

	public Object getCompound() {
		return this.parent.getCompound();
	}

	protected void setCompound(final Object compound) {
		this.parent.setCompound(compound);
	}

	public NBTCompound getParent() {
		return this.parent;
	}


	public void setString(final String key, final String value) {
		NBTReflectionUtil.setData(this, ReflectionMethod.COMPOUND_SET_STRING, key, value);
	}

	public String getString(final String key) {
		return (String) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_GET_STRING, key);
	}

	protected void set(final String key, final Object val) {
		NBTReflectionUtil.set(this, key, val);
	}

	public Boolean hasKey(final String key) {
		final Boolean b = (Boolean) NBTReflectionUtil.getData(this, ReflectionMethod.COMPOUND_HAS_KEY, key);
		if (b == null) {
			return false;
		}
		return b;
	}

	public void removeKey(final String key) {
		NBTReflectionUtil.remove(this, key);
	}

	public NBTCompound getCompound(final String name) {
		final NBTCompound next = new NBTCompound(this, name);
		if (NBTReflectionUtil.valideCompound(next)) {
			return next;
		}
		return null;
	}

}