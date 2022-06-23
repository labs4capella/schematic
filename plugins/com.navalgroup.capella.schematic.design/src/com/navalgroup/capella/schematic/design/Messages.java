/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design;

import org.eclipse.sirius.ext.base.I18N;
import org.eclipse.sirius.ext.base.I18N.TranslatableMessage;

/**
 * I18n messages for the plug-in.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class Messages {

	@TranslatableMessage
	public static String ConfigurationFileService_CouldNotLoadConfigurationFile;
	@TranslatableMessage
	public static String ConfigurationFileService_WrongConfigurationPropertyValue;
	@TranslatableMessage
	public static String ConfigurationFileService_ImageFileNotFound;
	@TranslatableMessage
	public static String ConfigurationFileService_NoConfigurationPropertyValue;
	@TranslatableMessage
	public static String ConfigurationFileService_NoConfigurationFileInPreference;
	@TranslatableMessage
	public static String SchematicService_NoSessionFound;
	@TranslatableMessage
	public static String AbstractSchematicDNodeContainerEditPart_ImageTranslationNotAvailableOnSVGImage;

	static {
		// initialize resource bundle
		I18N.initializeMessages(Messages.class, SchematicDesignPlugin.INSTANCE);
	}

	private Messages() {
	}
}
