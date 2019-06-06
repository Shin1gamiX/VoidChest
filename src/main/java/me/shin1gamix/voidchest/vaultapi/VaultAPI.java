package me.shin1gamix.voidchest.vaultapi;

import me.shin1gamix.voidchest.VoidChestPlugin;
import net.milkbowl.vault.economy.Economy;

public class VaultAPI {
	private final VoidChestPlugin core;

	public VaultAPI(final VoidChestPlugin core) {
		this.core = core;
	}

	private Economy economy;

	public Economy getEconomy() {
		return this.economy;
	}

	public boolean setupEconomy() {
		if (this.economy != null) {
			return true;
		}

		this.economy = this.core.getServer().getServicesManager().load(Economy.class);
		return (this.economy != null);
	}

}
