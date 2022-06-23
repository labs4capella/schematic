/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.ui;

import org.eclipse.sirius.ext.base.I18N;
import org.eclipse.sirius.ext.base.I18N.TranslatableMessage;

/**
 * I18n messages for the plug-in.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class Messages {

	@TranslatableMessage
	public static String SchematicPreferencePage_Title;
	@TranslatableMessage
	public static String SchematicPreferencePage_Description;
	@TranslatableMessage
	public static String SchematicPreferencePage_ConfigurationFileLabel;

	static {
		// initialize resource bundle
		I18N.initializeMessages(Messages.class, SchematicUIPlugin.INSTANCE);
	}

	private Messages() {
	}
}
