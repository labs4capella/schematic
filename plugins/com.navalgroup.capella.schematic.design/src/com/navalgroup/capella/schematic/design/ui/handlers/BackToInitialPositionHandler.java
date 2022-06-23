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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.PositionConstants;

/**
 * Action to reinit the container image position.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class BackToInitialPositionHandler extends AbstractImagePositionHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		setPosition(event, PositionConstants.NORTH);
		return null;
	}

}
