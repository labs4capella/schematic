/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.ui.handlers;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.Part;

import com.navalgroup.capella.schematic.design.images.editparts.AbstractSchematicDNodeContainerEditPart;
import com.navalgroup.capella.schematic.design.services.SchematicServices;

/**
 * Action to change Container image position.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public abstract class AbstractImagePositionHandler extends AbstractHandler {

	/**
	 * Rotate image to position.
	 * 
	 * @param event
	 *            ExecutionEvent
	 * @param position
	 *            int
	 */
	public void setPosition(ExecutionEvent event, int position) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof StructuredSelection) {
			Arrays.asList(((StructuredSelection) selection).toArray()).forEach(editPart -> {
				if (editPart instanceof AbstractSchematicDNodeContainerEditPart) {
					// set graphical position
					((AbstractSchematicDNodeContainerEditPart) editPart).setPosition(position);

					// save position on Capella element property value.
					EObject semanticElement = ((AbstractSchematicDNodeContainerEditPart) editPart)
							.resolveTargetSemanticElement();
					if (semanticElement instanceof CapellaElement) {
						// if part, search in abstract type
						if (semanticElement instanceof Part) {
							semanticElement = ((Part) semanticElement).getAbstractType();
						}
						SchematicServices.savePosition((CapellaElement) semanticElement, position);
					}
				}
			});
		}
	}

}
