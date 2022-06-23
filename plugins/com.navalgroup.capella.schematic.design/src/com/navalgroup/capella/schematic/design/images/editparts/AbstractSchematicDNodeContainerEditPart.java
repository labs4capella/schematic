/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.images.editparts;

import java.util.Optional;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.sirius.common.tools.api.util.ReflectionHelper;
import org.eclipse.sirius.common.tools.api.util.StringUtil;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DDiagramElementContainer;
import org.eclipse.sirius.diagram.WorkspaceImage;
import org.eclipse.sirius.diagram.business.internal.query.DDiagramElementContainerExperimentalQuery;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DNodeContainerEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SVGWorkspaceImageFigure;
import org.eclipse.sirius.diagram.ui.tools.api.figure.WorkspaceImageFigure;
import org.eclipse.sirius.diagram.ui.tools.api.image.DiagramImagesPath;
import org.eclipse.sirius.diagram.ui.tools.internal.figure.ContainerWithTitleBlockFigure;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.ext.base.Options;
import org.eclipse.sirius.ext.gmf.runtime.gef.ui.figures.SiriusDefaultSizeNodeFigure;
import org.eclipse.sirius.viewpoint.DStylizable;
import org.eclipse.sirius.viewpoint.description.style.LabelBorderStyleDescription;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellacore.IntegerPropertyValue;
import org.polarsys.capella.core.data.cs.Part;

import com.navalgroup.capella.schematic.design.Messages;
import com.navalgroup.capella.schematic.design.SchematicDesignPlugin;
import com.navalgroup.capella.schematic.design.images.figures.RotativeImageListener;
import com.navalgroup.capella.schematic.design.images.figures.SchematicSVGWorkspaceImageFigure;
import com.navalgroup.capella.schematic.design.images.figures.SchematicWorkspaceImageFigure;
import com.navalgroup.capella.schematic.design.services.SchematicServices;
import com.navalgroup.capella.schematic.design.services.SchematicViewpointServices;

/**
 * Abstract DNodeContainerEditPart for Schematic to get image from property
 * value and configuration file.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractSchematicDNodeContainerEditPart extends DNodeContainerEditPart {

	/**
	 * Lister for rotative image.
	 */
	private static RotativeImageListener listener;

	/**
	 * Constructor.
	 * 
	 * @param view
	 *            View
	 */
	public AbstractSchematicDNodeContainerEditPart(View view) {
		super(view);
	}

	/**
	 * Create background figure.
	 */
	public void createBackgroundFigure() {
		ReflectionHelper.setFieldValueWithoutException(this, "backgroundFigure", createBackgroundFigure(this));
	}

	/**
	 * Creates the background image.
	 * 
	 * @param self
	 *            the container edit part.
	 * @return the created image.
	 */
	public IFigure createBackgroundFigure(final IGraphicalEditPart self) {
		final EObject eObj = self.resolveSemanticElement();
		if (eObj instanceof DDiagramElementContainer) {
			final DDiagramElementContainer container = (DDiagramElementContainer) eObj;
			if (container.getOwnedStyle() instanceof WorkspaceImage
					&& !SchematicViewpointServices.hasSchematicDNodeContainerChildren(container)) {
				final WorkspaceImage img = (WorkspaceImage) container.getOwnedStyle();
				return createWkpImageFigure(img);
			}
		}
		return null;
	}

	private IFigure createWkpImageFigure(final WorkspaceImage img) {
		IFigure imageFigure = null;
		if (img != null && !StringUtil.isEmpty(img.getWorkspacePath())) {
			String workspacePath = img.getWorkspacePath();
			if (SchematicViewpointServices.SCHEMATIC_WORKSPACE_IMAGE_PATH.equals(workspacePath)) {
				imageFigure = createSchematicImageFigure(img);
			} else if (WorkspaceImageFigure.isSvgImage(workspacePath)) {
				imageFigure = SVGWorkspaceImageFigure.createImageFigure(img);
			} else {
				imageFigure = WorkspaceImageFigure.createImageFigure(workspacePath);
			}
		}
		return imageFigure;
	}

	private IFigure createSchematicImageFigure(final WorkspaceImage img) {
		IFigure imageFigure;
		EObject semanticElement = ((DDiagramElement) img.eContainer()).getTarget();
		if (semanticElement instanceof CapellaElement) {
			Optional<String> imagePath = SchematicViewpointServices
					.getSchematicImagePropertyValue((CapellaElement) semanticElement);
			if (imagePath.isPresent()) {
				if (WorkspaceImageFigure.isSvgImage(imagePath.get())) {
					imageFigure = SchematicSVGWorkspaceImageFigure.createImageFigure(img);
				} else {
					imageFigure = SchematicWorkspaceImageFigure.createImageFigure(imagePath.get());
				}
				listener = new RotativeImageListener(this);
				imageFigure.addFigureListener(listener);
			} else {
				imageFigure = WorkspaceImageFigure.createImageFigure(DiagramImagesPath.IMAGE_NOT_FOUND);
			}
		} else {
			imageFigure = WorkspaceImageFigure.createImageFigure(DiagramImagesPath.IMAGE_NOT_FOUND);
		}
		return imageFigure;
	}

	/**
	 * Set image position.
	 * 
	 * @param position
	 */
	public void setPosition(int position) {
		if (getBackgroundFigure() instanceof SchematicWorkspaceImageFigure) {
			((SchematicWorkspaceImageFigure) getBackgroundFigure()).setPosition(position);
		} else if (getBackgroundFigure() instanceof SchematicSVGWorkspaceImageFigure) {
			SchematicDesignPlugin.getPlugin().logWarning(
					Messages.AbstractSchematicDNodeContainerEditPart_ImageTranslationNotAvailableOnSVGImage);
		}
	}

	/**
	 * Update position when figure has changed.
	 */
	public void figureHasChanged() {
		if (getBackgroundFigure() != null) {
			EObject semanticElement = resolveTargetSemanticElement();
			if (semanticElement instanceof CapellaElement) {
				// if part, search in abstract type
				if (semanticElement instanceof Part) {
					semanticElement = ((Part) semanticElement).getAbstractType();
				}
				Optional<IntegerPropertyValue> propertyValueValue = SchematicServices.getIntegerPropertyValueValue(
						(CapellaElement) semanticElement, SchematicServices.SCHEMATIC_IMAGE_POSITION_PROPERTY_VALUE);
				if (propertyValueValue.isPresent()) {
					setPosition(propertyValueValue.get().getValue());

				}
			}
		}
	}

	/**
	 * Creates a figure for this edit part, depending on the label style.
	 * 
	 * @return a figure for this edit part.
	 */
	protected NodeFigure createNodePlate() {
		if (getBackgroundFigure() == null) {
			return super.createNodePlate();
		}

		// keep previous image dimension if last style was workspace image.
		NodeFigure result;
		DDiagramElement dde = resolveDiagramElement();
		Dimension defaultSize = getBackgroundFigure().getSize();
		Option<LabelBorderStyleDescription> getLabelBorderStyle = getLabelBorderStyle(dde);
		if (getLabelBorderStyle.some()) {
			result = new ContainerWithTitleBlockFigure(getMapMode().DPtoLP(defaultSize.width),
					getMapMode().DPtoLP(defaultSize.height), dde, getLabelBorderStyle.get());
		} else {
			result = new SiriusDefaultSizeNodeFigure(getMapMode().DPtoLP(defaultSize.width),
					getMapMode().DPtoLP(defaultSize.height));
		}

		return result;
	}

	private Option<LabelBorderStyleDescription> getLabelBorderStyle(DStylizable viewNode) {
		if (viewNode instanceof DDiagramElementContainer) {
			return new DDiagramElementContainerExperimentalQuery((DDiagramElementContainer) viewNode)
					.getLabelBorderStyle();
		}
		return Options.newNone();
	}

	@Override
	public void refresh() {
		super.refresh();
		figureHasChanged();
	}
}
