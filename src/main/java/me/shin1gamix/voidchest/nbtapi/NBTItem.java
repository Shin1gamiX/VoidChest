package me.shin1gamix.voidchest.nbtapi;

import org.bukkit.inventory.ItemStack;

public class NBTItem extends NBTCompound {
	private ItemStack bukkitItem;

	private NBTItem(final ItemStack item) {
		super(null, null);
		if (item == null) {
			throw new NullPointerException("ItemStack can't be null!");
		}
		this.bukkitItem = item.clone();
	}

	public static NBTItem of(final ItemStack item) {
		return new NBTItem(item);
	}

	@Override
	public Object getCompound() {
		return NBTReflectionUtil
				.getItemRootNBTTagCompound(ReflectionMethod.ITEMSTACK_NMSCOPY.run(null, this.bukkitItem));
	}

	@Override
	protected void setCompound(final Object compound) {
		final Object stack = ReflectionMethod.ITEMSTACK_NMSCOPY.run(null, this.bukkitItem);
		ReflectionMethod.ITEMSTACK_SET_TAG.run(stack, compound);
		this.bukkitItem = (ItemStack) ReflectionMethod.ITEMSTACK_BUKKITMIRROR.run(null, stack);
	}

	public ItemStack getItem() {
		return this.bukkitItem;
	}

}