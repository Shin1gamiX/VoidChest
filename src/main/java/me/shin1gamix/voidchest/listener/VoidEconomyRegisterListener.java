package me.shin1gamix.voidchest.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.google.common.collect.Lists;

import me.shin1gamix.voidchest.VoidChestPlugin;
import me.shin1gamix.voidchest.ecomanager.VoidEconomy;
import me.shin1gamix.voidchest.utilities.DebugUtil;
import me.shin1gamix.voidchest.utilities.Utils;

public class VoidEconomyRegisterListener implements Listener {
	private final VoidChestPlugin core;

	public VoidEconomyRegisterListener(final VoidChestPlugin core) {
		this.core = core;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onRegister(final ServiceRegisterEvent event) {
		if (event.getProvider().getService() != VoidEconomy.class) {
			return;
		}

		final boolean debug = DebugUtil.isDebugEnabled();

		final List<String> debugging = Lists.newArrayList();

		if (debug) {
			debugging.add("A plugin has attemted to register their api in the server, details are shown below.");
			try {
				debugging.add("Details: " + ((VoidEconomy) event.getProvider().getProvider()).getName());
			} catch (ClassCastException e) {
				debugging.add("Something went wrong, contact Shin1gamiX");
				debugging.add(e.getMessage());
			}
		}

		RegisteredServiceProvider<VoidEconomy> provider = null;
		for (final RegisteredServiceProvider<VoidEconomy> registered : Bukkit.getServicesManager()
				.getRegistrations(VoidEconomy.class)) {

			if (provider == null) {
				provider = registered;
				continue;
			}

			int compare = provider.compareTo(registered);
			if (compare == 1 || compare == 0) {
				provider = registered;
			}

		}

		if (provider == null) {
			if (debug) {
				debugging.add("Couldn't add the api.");
				Utils.debug(this.core, debugging);
			}
			return;
		}

		final VoidEconomy econ = provider.getProvider();
		if (debug) {
			debugging.add("registered an api: " + econ.getName());
			Utils.debug(this.core, debugging);
		}
		this.core.getVoidEconomyManager().setVoidEconomy(econ);
	}
}
