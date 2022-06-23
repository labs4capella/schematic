/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.sirius.diagram.ui.internal.refresh.listeners.WorkspaceFileResourceChangeListener;

import com.navalgroup.capella.schematic.design.Messages;
import com.navalgroup.capella.schematic.design.SchematicDesignPlugin;
import com.navalgroup.capella.schematic.ui.preferences.SchematicPreferencePage;

/**
 * Services for configuration file.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
@SuppressWarnings("restriction")
public final class ConfigurationFileServices {

	/**
	 * Property value key for configuration file labels.
	 */
	public static final String SCHEMATIC_CONF_LABEL_NAME_PROPERTY_VALUE = "schematic.labels";

	/**
	 * Configuration file separator for schematic.label key.
	 */
	public static final String CONFIGURATION_SEPARATOR = ",";

	/**
	 * File separator.
	 */
	public static final String FILE_SEPARATOR = "/";

	/**
	 * Constructor.
	 */
	private ConfigurationFileServices() {
	}

	/**
	 * Check if configuration file contains key with valid value.
	 * 
	 * @param key
	 *            String
	 * @return if configuration file contains key with valid value.
	 */
	public static boolean isConfigurationPropertyValid(String key) {
		return getConfigurationPropertyValue(key).isPresent();
	}

	/**
	 * Get the value corresponding to the key in configuration file, Optional.empty
	 * otherwise.
	 * 
	 * @param key
	 *            String
	 * @return the value corresponding to the key in configuration file.
	 */
	public static Optional<String> getConfigurationImagePropertyValue(String key) {
		Optional<Properties> properties = getConfigurationFileProperties();
		if (properties.isPresent()) {
			return getValidImageProperty(properties.get(), key);
		}
		return Optional.empty();
	}

	/**
	 * Get the value corresponding to the key in configuration file, Optional.empty
	 * otherwise.
	 * 
	 * @param key
	 *            String
	 * @return the value corresponding to the key in configuration file.
	 */
	public static Optional<String> getConfigurationPropertyValue(String key) {
		Optional<Properties> properties = getConfigurationFileProperties();
		if (properties.isPresent()) {
			return getProperty(properties.get(), key);
		}
		return Optional.empty();
	}

	/**
	 * Get properties from configuration file if exists.
	 * 
	 * @return properties from Configuration file.
	 */
	public static Optional<Properties> getConfigurationFileProperties() {
		// get configuration path from preferences.
		String configurationFilePath = com.navalgroup.capella.schematic.ui.SchematicUIPlugin.getPlugin()
				.getPreferenceStore().getString(SchematicPreferencePage.CONFIGURATION_FILE_PREF_ID);
		if (configurationFilePath == null || configurationFilePath.isEmpty()) {
			SchematicDesignPlugin.getPlugin()
					.logWarning(Messages.ConfigurationFileService_NoConfigurationFileInPreference);
		} else {
			// try to load properties configuration file
			final Properties properties = new Properties();
			try {
				final BufferedReader reader = Files.newBufferedReader(Paths.get(configurationFilePath));
				properties.load(reader);
				reader.close();
				return Optional.ofNullable(properties);
			} catch (IOException ioException) {
				SchematicDesignPlugin.getPlugin()
						.logError(String.format(Messages.ConfigurationFileService_CouldNotLoadConfigurationFile,
								configurationFilePath.toString()), ioException);
			}
		}
		return Optional.empty();
	}

	/**
	 * Check if property path is not null, is not empty and references an image.
	 * 
	 * @param properties
	 *            Properties
	 * @param key
	 *            String
	 * @return if property path is not null, is not empty and references an image.
	 */
	// CHECKSTYLE:OFF
	private static Optional<String> getValidImageProperty(Properties properties, String key) {
		if (properties.containsKey(key)) {
			String property = properties.getProperty(key);
			if (property != null && !property.isEmpty()) {
				File imageFile = WorkspaceFileResourceChangeListener.getInstance().getFileFromURI(property);
				if (imageFile != null) {
					Path imagePath = Paths.get(imageFile.getAbsolutePath());
					if (Files.exists(imagePath) && Files.isRegularFile(imagePath)) {
						if (!property.startsWith(FILE_SEPARATOR)) {
							property = FILE_SEPARATOR + property;
						}
						return Optional.ofNullable(property);
					}
				} else {
					SchematicDesignPlugin.getPlugin()
							.logError(String.format(Messages.ConfigurationFileService_ImageFileNotFound, key));
				}
			} else {
				SchematicDesignPlugin.getPlugin().logError(
						String.format(Messages.ConfigurationFileService_WrongConfigurationPropertyValue, key));
			}
		} else {
			SchematicDesignPlugin.getPlugin()
					.logError(String.format(Messages.ConfigurationFileService_NoConfigurationPropertyValue, key));
		}
		return Optional.empty();
	}
	// CHECKSTYLE:ON

	/**
	 * Get value property key or Optional.empty.
	 * 
	 * @param properties
	 *            Properties
	 * @param key
	 *            String
	 * @return value property key or Optional.empty.
	 */
	private static Optional<String> getProperty(Properties properties, String key) {
		if (properties.containsKey(key)) {
			String property = properties.getProperty(key);
			if (property != null && !property.isEmpty()) {
				return Optional.ofNullable(property);
			}
			SchematicDesignPlugin.getPlugin()
					.logError(String.format(Messages.ConfigurationFileService_WrongConfigurationPropertyValue, key));
		} else {
			SchematicDesignPlugin.getPlugin()
					.logError(String.format(Messages.ConfigurationFileService_NoConfigurationPropertyValue, key));
		}
		return Optional.empty();
	}

}
