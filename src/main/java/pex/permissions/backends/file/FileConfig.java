package pex.permissions.backends.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import pex.permissions.backends.FileBackend;

public class FileConfig extends YamlConfiguration {

	protected File file;

	public FileConfig(File file) {
		super();

		options().pathSeparator(FileBackend.PATH_SEPARATOR);

		this.file = file;

		reload();
	}

	public File getFile() {
		return file;
	}

	public void reload() {

		try {
			this.load(file);
		} catch (FileNotFoundException e) {
			// do nothing
		} catch (Throwable e) {
			throw new IllegalStateException("Error loading permissions file", e);
		}
	}

	public void save() {
		try {
			this.save(file);
		} catch (IOException e) {
			Logger.getLogger("Minecraft").severe("[PermissionsEx] Error during saving permissions file: " + e.getMessage());
		}
	}
}
