package org.itsallcode.time;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {

	private final Properties properties;

	private Config(Properties properties) {
		this.properties = properties;
	}

	public static Config read(Path configFile) {
		return new Config(loadProperties(configFile));
	}

	private static Properties loadProperties(Path configFile) {
		try (InputStream stream = Files.newInputStream(configFile)) {
			final Properties props = new Properties();
			props.load(stream);
			return props;
		} catch (final IOException e) {
			throw new IllegalStateException("Error reading " + configFile, e);
		}
	}

	public Path getDataDir() {
		return Paths.get(getMandatoryValue("data"));
	}

	private String getMandatoryValue(String param) {
		final String value = this.properties.getProperty(param);
		if (value == null) {
			throw new IllegalStateException("Property " + param + " not found in config file");
		}
		return value;
	}
}
