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

import java.io.File;
import java.util.Optional;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.common.tools.api.resource.FileProvider;
import org.eclipse.sirius.common.tools.api.util.ReflectionHelper;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.WorkspaceImage;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SVGFigure;
import org.eclipse.sirius.diagram.ui.tools.api.figure.SVGWorkspaceImageFigure;
import org.eclipse.sirius.diagram.ui.tools.internal.figure.svg.SimpleImageTranscoder;
import org.eclipse.sirius.ext.base.Option;
import org.eclipse.sirius.ext.base.Options;
import org.polarsys.capella.core.data.capellacore.CapellaElement;

import com.navalgroup.capella.schematic.design.services.SchematicViewpointServices;

/**
 * SVGWorkspaceImageFigure for Schematic to get image from property value and
 * configuration file.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
@SuppressWarnings("restriction")
public class SchematicSVGWorkspaceImageFigure extends SVGWorkspaceImageFigure {

	/**
	 * Create the {@link SVGWorkspaceImageFigure} from a {@link WorkspaceImage}
	 * instance.
	 *
	 * @param image
	 *            {@link SVGWorkspaceImageFigure} specification.
	 * @return new Figure.
	 */
	public static SVGWorkspaceImageFigure createImageFigure(final WorkspaceImage image) {
		SchematicSVGWorkspaceImageFigure fig = new SchematicSVGWorkspaceImageFigure();
		fig.refreshFigure(image);
		return fig;
	}

	@Override
	public void refreshFigure(final WorkspaceImage workspaceImage) {
		if (workspaceImage != null) {
			String workspacePath = workspaceImage.getWorkspacePath();
			if (SchematicViewpointServices.SCHEMATIC_WORKSPACE_IMAGE_PATH.equals(workspacePath)) {
				EObject semanticElement = ((DDiagramElement) workspaceImage.eContainer()).getTarget();
				if (semanticElement instanceof CapellaElement) {
					Optional<String> imagePath = SchematicViewpointServices
							.getSchematicImagePropertyValue((CapellaElement) semanticElement);
					if (imagePath.isPresent()) {
						workspacePath = imagePath.get();
					}
				}
			}
			boolean updated = this.updateImageURI(workspacePath);
			if (updated) {
				this.contentChanged();
				SimpleImageTranscoder transcoder = getTranscoder();
				if (transcoder != null) {
					ReflectionHelper.setFieldValueWithoutException(this, "imageAspectRatioForModeWithViewBox",
							transcoder.getAspectRatio());
				}
			}
		} else {
			this.setURI(null);
		}
	}

	private boolean updateImageURI(String workspacePath) {
		if (workspacePath != null) {
			Option<String> existingImageUri = SchematicSVGWorkspaceImageFigure.getImageUri(workspacePath, false);
			if (existingImageUri.some()) {
				setURI(existingImageUri.get());
			} else {
				setURI(SVGFigure.IMAGE_NOT_FOUND_URI);
			}
			return true;
		}
		return false;
	}

	/**
	 * Return an optional uri as used in the document key to read svg files.
	 *
	 * @param workspacePath
	 *            the workspace path of the file.
	 * @param force
	 *            true to avoid to check that the file exists and is readble.
	 * @return an optional system uri.
	 */
	private static Option<String> getImageUri(String workspacePath, boolean force) {
		final File imageFile = FileProvider.getDefault().getFile(new Path(workspacePath));
		if (imageFile != null && (force || imageFile.exists() && imageFile.canRead())) {
			return Options.newSome(imageFile.toURI().toString());
		}
		Option<String> nonExistingFile = Options.newNone();
		if (force) {
			// Deleted file : retrieve the key.
			nonExistingFile = Options
					.newSome(ResourcesPlugin.getWorkspace().getRoot().getLocationURI().toString() + workspacePath);
		}

		return nonExistingFile;
	}

}
