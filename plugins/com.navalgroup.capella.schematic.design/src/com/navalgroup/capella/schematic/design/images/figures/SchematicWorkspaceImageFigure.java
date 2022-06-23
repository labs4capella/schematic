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

import java.util.Optional;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.WorkspaceImage;
import org.eclipse.sirius.diagram.ui.tools.api.figure.WorkspaceImageFigure;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.polarsys.capella.core.data.capellacore.CapellaElement;

import com.navalgroup.capella.schematic.design.SchematicDesignPlugin;
import com.navalgroup.capella.schematic.design.services.SchematicViewpointServices;

/**
 * WorkspaceImageFigure for Schematic to get image from property value and
 * configuration file.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 * 
 */
public class SchematicWorkspaceImageFigure extends WorkspaceImageFigure {
	/**
	 * Image Path.
	 */
	private String imgPath;
	/**
	 * Image position (default, 90 left, 90 right, 180, horizontal symmetry,
	 * vertical symmetry).
	 */
	private int position;

	/**
	 * Constructor.
	 * 
	 * @param flyWeightImage
	 *            Image
	 */
	public SchematicWorkspaceImageFigure(Image flyWeightImage) {
		super(flyWeightImage);
		position = PositionConstants.NORTH;
	}

	/**
	 * Create image figure.
	 * 
	 * @param path
	 * @return WorkspaceImageFigure
	 */
	public static WorkspaceImageFigure createImageFigure(final String path) {
		final SchematicWorkspaceImageFigure fig = new SchematicWorkspaceImageFigure(
				WorkspaceImageFigure.flyWeightImage(path));
		return fig;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see fr.obeo.dsl.viewpoint.diagram.ui.tools.api.figure.WorkspaceImageFigure#refreshFigure(fr.obeo.dsl.viewpoint.WorkspaceImage)
	 */
	public void refreshFigure(final WorkspaceImage bundledImage) {
		String path = bundledImage.getWorkspacePath();
		if (SchematicViewpointServices.SCHEMATIC_WORKSPACE_IMAGE_PATH.equals(path)) {
			EObject semanticElement = ((DDiagramElement) bundledImage.eContainer()).getTarget();
			if (semanticElement instanceof CapellaElement) {
				Optional<String> imagePath = SchematicViewpointServices
						.getSchematicImagePropertyValue((CapellaElement) semanticElement);
				if (imagePath.isPresent()) {
					path = imagePath.get();
				}
			}
		}
		imgPath = path;
		final Image image = WorkspaceImageFigure.flyWeightImage(path);
		if (image == this.getImage()) {
			return;
		}
		this.setImage(image);
		this.repaint();
	}

	private void refreshFigure() {
		Image image = getImage(imgPath, position);
		if (image == this.getImage()) {
			return;
		}
		this.setImage(image);
		this.repaint();
	}

	/**
	 * Set position and refresh figure.
	 *
	 * @param pos
	 *            int
	 */
	public void setPosition(int pos) {
		this.position = pos;
		refreshFigure();
	}

	private Image getImage(String path, int direction) {
		String key = getKey(path, direction);
		Image image = getImageFromRegistry(key);
		if (image == null) {
			// We have to create and store the image
			if (direction == PositionConstants.NORTH) {
				image = flyWeightImage(imgPath);
			} else {
				// We have to rotate the default image (NORTH)
				Image northImage = getImage(path, PositionConstants.NORTH);
				image = rotate(northImage, direction);
			}
			putImageInRegistry(key, image);
		}
		return image;
	}

	private String getKey(String path, int direction) {
		return path + "?" + direction;
	}

	private Image getImageFromRegistry(String key) {
		return SchematicDesignPlugin.getPlugin().getImageRegistry().get(key);
	}

	private void putImageInRegistry(String key, Image image) {
		if (SchematicDesignPlugin.getPlugin().getImageRegistry().get(key) == null) {
			SchematicDesignPlugin.getPlugin().getImageRegistry().put(key, image);
		}
	}

	private static Image rotate(Image image, int direction) {
		ImageData srcData = image.getImageData();

		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		boolean noRotation = direction == PositionConstants.HORIZONTAL || direction == PositionConstants.VERTICAL
				|| direction == PositionConstants.SOUTH;
		int destBytesPerLine = noRotation ? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
		byte[] newData = new byte[srcData.data.length];

		boolean isAlpha = srcData.alphaData != null;
		byte[] newAlphaData = null;

		if (isAlpha) {
			newAlphaData = new byte[srcData.alphaData.length];
		}
		ImageData imgData = new ImageData(noRotation ? srcData.width : srcData.height,
				noRotation ? srcData.height : srcData.width, srcData.depth, srcData.palette, destBytesPerLine, newData);
		if (isAlpha) {
			imgData.alphaData = newAlphaData;
		}
		imgData.alpha = srcData.alpha;

		for (int srcY = 0; srcY < srcData.height; srcY++) {
			for (int srcX = 0; srcX < srcData.width; srcX++) {
				int destX;
				int destY;
				switch (direction) {
				// left 90 degrees
				case PositionConstants.WEST:
					destX = srcY;
					destY = srcData.width - srcX - 1;
					break;
				// right 90 degrees
				case PositionConstants.EAST:
					destX = srcData.height - srcY - 1;
					destY = srcX;
					break;
				// horizontal symmetry
				case PositionConstants.HORIZONTAL:
					destX = srcData.width - srcX - 1;
					destY = srcY;
					break;
				// vertical symmetry
				case PositionConstants.VERTICAL:
					destX = srcX;
					destY = srcData.height - srcY - 1;
					break;
				// rotation 180 degrees
				case PositionConstants.SOUTH:
					destX = srcData.width - srcX - 1;
					destY = srcData.height - srcY - 1;
					break;
				default:
					destX = 0;
					destY = 0;
				}

				imgData.setPixel(destX, destY, srcData.getPixel(srcX, srcY));
				if (isAlpha) {
					imgData.setAlpha(destX, destY, srcData.getAlpha(srcX, srcY));
				}
			}
		}

		return new Image(image.getDevice(), imgData);
	}
}
