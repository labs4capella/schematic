/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.ui.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.navalgroup.capella.schematic.ui.Messages;
import com.navalgroup.capella.schematic.ui.SchematicUIPlugin;

/**
 * Preference page for Schematic configuration file.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class SchematicPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Preference ID for configuration file.
	 */
	public static final String CONFIGURATION_FILE_PREF_ID = "SchematicConfigurationFilePreference";
	/**
	 * Configuration file extension.
	 */
	public static final String CONFIGURATION_FILE_EXTENSION = "*.cfg";

	/**
	 * FieldEditor for configuration file.
	 */
	private ConfigurationFileFieldEditor fileFieldEditor;

	/**
	 * Create the preference page.
	 */
	public SchematicPreferencePage() {
		super(FLAT);
		// get the preference store from the Core UI plugin
		IPreferenceStore store = SchematicUIPlugin.getPlugin().getPreferenceStore();
		setPreferenceStore(store);
		setTitle(Messages.SchematicPreferencePage_Title);
		setDescription(Messages.SchematicPreferencePage_Description);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		this.fileFieldEditor = new ConfigurationFileFieldEditor(CONFIGURATION_FILE_PREF_ID,
				Messages.SchematicPreferencePage_ConfigurationFileLabel, getFieldEditorParent());
		this.fileFieldEditor.setEmptyStringAllowed(true);
		this.fileFieldEditor.setFileExtensions(new String[] { CONFIGURATION_FILE_EXTENSION });
		this.fileFieldEditor.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile());
		addField(this.fileFieldEditor);
	}

	/**
	 * Initialize the preference page.
	 * 
	 * @param workbench
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

}
