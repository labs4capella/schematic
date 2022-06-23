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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.navalgroup.capella.schematic.ui.SchematicUIPlugin;

/**
 * {@link AbstractPreferenceInitializer} implementation for setting the default
 * values of our preferences.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class SchematicDefaultPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore preferenceStore = SchematicUIPlugin.getPlugin().getPreferenceStore();
		preferenceStore.setDefault(SchematicPreferencePage.CONFIGURATION_FILE_PREF_ID, "");
	}

}
