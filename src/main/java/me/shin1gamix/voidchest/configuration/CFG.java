package me.shin1gamix.voidchest.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Sets;

public final class CFG {

	private final static Set<CFG> cachedFiles = Sets.newHashSet();

	public static void reloadFiles() {
		cachedFiles.forEach(CFG::reloadFile);
	}

	/* The plugin instance. */
	private final JavaPlugin plugin;
	/* The file's name (without the .yml) */
	private final String fileName;

	/* The yml file configuration. */
	private FileConfiguration fileConfiguration;
	/* The file. */
	private File file;

	private final boolean saveResource;

	public CFG(final JavaPlugin plugin, final String string, final boolean saveResource) {
		this.plugin = plugin;
		this.fileName = string + ".yml";
		this.saveResource = saveResource;
		this.setup();

		cachedFiles.add(this);
	}

	/**
	 * Attempts to load the file.
	 * 
	 * @param saveResource
	 *            -> Saves the raw contents embedded with the plugin's jar
	 *
	 * @see #filename
	 * @see #cfile
	 * @see #reloadFile()
	 * @see JavaPlugin#saveResource(String, boolean)
	 */
	public void setup() {
		if (!this.plugin.getDataFolder().exists()) {
			this.plugin.getDataFolder().mkdir();
		}

		this.file = new File(this.plugin.getDataFolder(), this.fileName);

		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (saveResource) {
				this.plugin.saveResource(this.fileName, true);
			}
		}

		this.reloadFile();
	}

	/**
	 * Saves the file configuration.
	 * 
	 * @see FileConfiguration#save(File)
	 * @see #getFile()
	 * @see #cfile
	 */
	public void saveFile() {
		try {
			this.getFile().save(this.file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reloads the file.
	 * 
	 * @see YamlConfiguration#loadConfiguration(File)
	 * @see #file
	 * @see #cfile
	 */
	public void reloadFile() {
		this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
	}

	/**
	 * @return the file -> The fileconfiguration.
	 */
	public FileConfiguration getFile() {
		return this.fileConfiguration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CFG other = (CFG) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}
}