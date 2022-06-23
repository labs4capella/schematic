/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.images.providers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DNodeContainer2EditPart;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DNodeContainerEditPart;

import com.navalgroup.capella.schematic.design.images.editparts.SchematicDNodeContainer2EditPart;
import com.navalgroup.capella.schematic.design.images.editparts.SchematicDNodeContainerEditPart;
import com.navalgroup.capella.schematic.design.services.SchematicServices;

/**
 * Edit Part Provider for Schematic to get Schematic edit parts for Schematic
 * graphical elements.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 * 
 */
@SuppressWarnings("restriction")
public class SchematicEditPartProvider extends AbstractEditPartProvider {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider#getNodeEditPartClass(org.eclipse.gmf.runtime.notation.View)
	 */
	// CHECKSTYLE:OFF
	@Override
	protected Class<?> getNodeEditPartClass(View view) {
		final EObject resolvedSemanticElement = ViewUtil.resolveSemanticElement(view);
		boolean isSchematicElement = SchematicServices.isSchematicElement(resolvedSemanticElement);
		if (isSchematicElement) {
			if (view.getType().equals(Integer.toString(DNodeContainerEditPart.VISUAL_ID))) {
				return SchematicDNodeContainerEditPart.class;
			}
			if (view.getType().equals(Integer.toString(DNodeContainer2EditPart.VISUAL_ID))) {
				return SchematicDNodeContainer2EditPart.class;
			}
		}
		return super.getNodeEditPartClass(view);
	}
	// CHECKSTYLE:ON
}
