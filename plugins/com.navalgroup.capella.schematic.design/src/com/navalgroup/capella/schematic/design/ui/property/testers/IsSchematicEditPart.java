/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.ui.property.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.WorkspaceImage;

import com.navalgroup.capella.schematic.design.images.editparts.AbstractSchematicDNodeContainerEditPart;

/**
 * Test if selection is an AbstractSchematicDNodeContainerEditPart with WorkspaceImage style.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class IsSchematicEditPart extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof AbstractSchematicDNodeContainerEditPart) {
			DDiagramElement diagramElement = ((AbstractSchematicDNodeContainerEditPart) receiver)
					.resolveDiagramElement();
			return diagramElement.getStyle() instanceof WorkspaceImage;
		}
		return false;
	}

}
