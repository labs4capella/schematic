/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.images.figures;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;

import com.navalgroup.capella.schematic.design.images.editparts.AbstractSchematicDNodeContainerEditPart;

/**
 * Listener for rotative bordered nodes which notifies the edit part when a
 * change has occured.
 *
 * @author @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class RotativeImageListener implements FigureListener, PropertyChangeListener, AncestorListener {
	/**
	 * Container EditPart.
	 */
	private AbstractSchematicDNodeContainerEditPart editpart;

	/**
	 * Constructor.
	 *
	 * @param editpart
	 *            The part to listen to
	 */
	public RotativeImageListener(AbstractSchematicDNodeContainerEditPart editpart) {
		this.editpart = editpart;
	}

	/**
	 * Notify the edit part that.
	 */
	public void notifyEditPart() {
		editpart.figureHasChanged();
	}

	/**
	 * figureMoved.
	 * 
	 * @param source
	 *            parameter
	 * @see org.eclipse.draw2d.FigureListener#figureMoved(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void figureMoved(IFigure source) {
		notifyEditPart();
	}

	/**
	 * propertyChange.
	 * 
	 * @param evt
	 *            parameter
	 * @see java.beans.PropertyChangeListener#propertyChange(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		notifyEditPart();
	}

	/**
	 * ancestorAdded.
	 * 
	 * @param ancestor
	 *            parameter
	 * @see org.eclipse.draw2d.AncestorListener#ancestorAdded(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void ancestorAdded(IFigure ancestor) {
		notifyEditPart();
	}

	/**
	 * ancestorMoved.
	 * 
	 * @param ancestor
	 *            parameter
	 * @see org.eclipse.draw2d.AncestorListener#ancestorMoved(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void ancestorMoved(IFigure ancestor) {
		notifyEditPart();
	}

	/**
	 * ancestorRemoved.
	 * 
	 * @param ancestor
	 *            parameter
	 * @see org.eclipse.draw2d.AncestorListener#ancestorRemoved(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void ancestorRemoved(IFigure ancestor) {
		notifyEditPart();
	}

}
