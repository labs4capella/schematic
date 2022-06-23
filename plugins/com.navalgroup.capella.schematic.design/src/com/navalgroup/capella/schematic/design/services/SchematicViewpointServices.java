/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.schematic.design.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.logger.RuntimeLoggerManager;
import org.eclipse.sirius.common.tools.api.interpreter.EvaluationException;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreter;
import org.eclipse.sirius.common.tools.api.interpreter.IInterpreterSiriusVariables;
import org.eclipse.sirius.diagram.DDiagram;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DDiagramElementContainer;
import org.eclipse.sirius.diagram.DNode;
import org.eclipse.sirius.diagram.DNodeContainer;
import org.eclipse.sirius.diagram.business.api.query.DDiagramQuery;
import org.eclipse.sirius.diagram.business.internal.metamodel.helper.MappingWithInterpreterHelper;
import org.eclipse.sirius.diagram.description.ContainerMappingImport;
import org.eclipse.sirius.diagram.description.DiagramElementMapping;
import org.eclipse.sirius.diagram.description.EdgeMapping;
import org.eclipse.sirius.diagram.description.EdgeMappingImport;
import org.eclipse.sirius.diagram.description.IEdgeMapping;
import org.eclipse.sirius.diagram.description.Layer;
import org.eclipse.sirius.diagram.description.style.EdgeStyleDescription;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.description.style.BasicLabelStyleDescription;
import org.eclipse.sirius.viewpoint.description.style.StylePackage;
import org.polarsys.capella.common.data.modellingcore.AbstractTypedElement;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.Part;

/**
 * Services for Schematic viewpoint (odesign).
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
@SuppressWarnings("restriction")
public final class SchematicViewpointServices {

	/**
	 * Key for odesign workspace image path.
	 */
	public static final String SCHEMATIC_WORKSPACE_IMAGE_PATH = "getImageFromPropertyValue";
	/**
	 * Schematic layer id.
	 */
	public static final String SCHEMATIC_LAYER_LABEL = "schematic.layer.image";
	/**
	 * RTPF layer id.
	 */
	public static final String SCHEMATIC_LAYER_LABEL_RTPF = "schematic.layer.label.rtpf";
	/**
	 * Labels from configuration file layer id.
	 */
	public static final String SCHEMATIC_LAYER_LABEL_CONFIGURATION_FILE = "schematic.layer.label.conf.file";

	/**
	 * PC bordered mapping id.
	 */
	public static final String PC_BORDERED_MAPPING_ID = "PAB_SCH_PC_Bordered";

	/**
	 * Deployment bordered mapping id.
	 */
	public static final String DEPLOYMENT_BORDERED_MAPPING_ID = "PAB_SCH_PAB_SCH_Deployment_Bordered";
	/**
	 * PC mapping id.
	 */
	public static final String PC_MAPPING_ID = "PAB_SCH_PC";

	/**
	 * Deployment mapping id.
	 */
	public static final String DEPLOYMENT_MAPPING_ID = "PAB_SCH_PAB_SCH_Deployment";

	/**
	 * Separator for label suffixes.
	 */
	private static final String LABEL_SEPARATOR = " - ";

	/**
	 * Constructor.
	 */
	private SchematicViewpointServices() {
	}

	/**
	 * Check if element has a string property value named "schematic.image". This
	 * value has to be defined in the configuration properties file.
	 * 
	 * @param element
	 *            CapellaElement
	 * @return if element has a valid string property value named "schematic.image"
	 */
	public static boolean hasSchematicImagePropertyValue(CapellaElement element, DDiagramElement container) {
		return getSchematicImagePropertyValue(element).isPresent() && !hasSchematicDNodeContainerChildren(container);
	}

	/**
	 * Check if DDiagramElement contains SchematicDNodeContainer.
	 * 
	 * @param dde
	 *            DDiagramElement
	 * @return if DDiagramElement contains SchematicDNodeContainer
	 */
	public static boolean hasSchematicDNodeContainerChildren(DDiagramElement dde) {
		DDiagramElement container = dde;
		if (dde instanceof DNode && isSchematicBorderedNode((DNode) dde)
				&& dde.eContainer() instanceof DNodeContainer) {
			container = (DNodeContainer) dde.eContainer();
		}
		return container instanceof DDiagramElementContainer && ((DDiagramElementContainer) container).getElements()
				.stream().filter(DDiagramElementContainer.class::isInstance).findFirst().isPresent();
	}

	/**
	 * Check if dde is a schematic bordered node.
	 * 
	 * @param dde
	 *            DNode
	 * @return if dde is a schematic bordered node.
	 */
	public static boolean isSchematicBorderedNode(DNode dde) {
		return PC_BORDERED_MAPPING_ID.equals(dde.getActualMapping().getName())
				|| DEPLOYMENT_BORDERED_MAPPING_ID.equals(dde.getActualMapping().getName());
	}

	/**
	 * Check if element has a string property value named "schematic.image". This
	 * value has to be defined in the mapping properties file.
	 * 
	 * @param element
	 *            CapellaElement
	 * @return if element has a valid string property value named "schematic.image"
	 */
	public static Optional<String> getSchematicImagePropertyValue(CapellaElement element) {
		return SchematicServices.getSchematicImagePropertyValue(element);
	}

	/**
	 * Compute RTPF label if exists.
	 * 
	 * @param part
	 *            Part
	 * @return part label with RTPF suffix if exists
	 */
	public static String computeSchematicRTPFLabel(EObject element, DDiagramElement view) {
		String label = "";

		// if part has StringPropertyvalue with key RTPF, return value
		if (element instanceof Part) {
			// CHECKSTYLE:OFF
			element = ((AbstractTypedElement) element).getAbstractType();
			// CHECKSTYLE:ON
		}
		if (element instanceof CapellaElement) {
			label = SchematicServices.getRTPFLabel((CapellaElement) element);
		}
		return label;
	}

	/**
	 * Compute element label with RTPF suffix if exists and RTPF layer is activated
	 * and suffixes defines in configuration file schematic.labels key.
	 * 
	 * @param part
	 *            Part
	 * @return element label with RTPF suffix if exists and RTPF layer is activated
	 *         and suffixes defines in configuration file schematic.labels key.
	 */
	public static String computeSchematicLabel(EObject element, DDiagramElement view) {
		DDiagramElement dContainerView = view;
		// test if borderedNode -> compute label from DNodeContainer parent
		if (view.eContainer() instanceof DNodeContainer && isSchematicContainer((DNodeContainer) view.eContainer())) {
			dContainerView = (DDiagramElement) view.eContainer();
		}

		String label = "";
		// image + rtpf activated -> name + rtpf if exists
		if (isSchematicLayerActivated(dContainerView.getParentDiagram())
				&& isRTPFLayerActivated(dContainerView.getParentDiagram())) {
			String rtpfLabel = computeSchematicRTPFLabel(element, dContainerView);
			label = getDDiagramElementLabel(dContainerView);
			if (!rtpfLabel.isEmpty()) {
				label += LABEL_SEPARATOR + rtpfLabel;
			}
		} else if (isRTPFLayerActivated(dContainerView.getParentDiagram())) {
			// image deactivated + rtpf activated -> RTPF if exists else name
			String rtpfLabel = computeSchematicRTPFLabel(element, dContainerView);
			if (rtpfLabel.isEmpty()) {
				label = getDDiagramElementLabel(dContainerView);
			} else {
				label = rtpfLabel;
			}
		} else {
			// image activated + rtpf deactivated -> name
			label = getDDiagramElementLabel(dContainerView);
		}

		if (isLabelsLayerActivated(dContainerView.getParentDiagram())) {
			// get suffixes from configuration file
			if (element instanceof Part) {
				// CHECKSTYLE:OFF
				element = ((AbstractTypedElement) element).getAbstractType();
				// CHECKSTYLE:ON
			}
			if (element instanceof CapellaElement) {
				label += getSchematicSuffixLabelFromConfigurationFile((CapellaElement) element);
			}
		}
		return label;
	}

	/**
	 * Force return empty string.
	 * 
	 * @param eObject
	 *            EObject
	 * @return empty string
	 */
	public String getEmptyString(EObject eObject) {
		return "";
	}

	/**
	 * Check if container belongs to schematic layer.
	 * 
	 * @param container
	 *            DNodeContainer
	 * @return if container belongs to schematic layer.
	 */
	public static boolean isSchematicContainer(DNodeContainer container) {
		return PC_MAPPING_ID.equals(container.getActualMapping().getName())
				|| DEPLOYMENT_MAPPING_ID.equals(container.getActualMapping().getName());
	}

	/**
	 * Get Schematic suffix label from configuration file or empty.
	 * 
	 * @param element
	 *            CapellaElement
	 * @return Schematic suffix label from configuration file or empty.
	 */
	public static String getSchematicSuffixLabelFromConfigurationFile(CapellaElement element) {
		// get schematic.labels value from configuration file.
		Optional<String> configurationFileValue = ConfigurationFileServices
				.getConfigurationPropertyValue(ConfigurationFileServices.SCHEMATIC_CONF_LABEL_NAME_PROPERTY_VALUE);
		if (configurationFileValue.isPresent()) {
			// format is XXX,YYY,ZZZ...
			// Split, trim ang get all StringPropertyValue values. Separate them with
			// LABEL_SEPARATOR.
			String[] properties = configurationFileValue.get().split(ConfigurationFileServices.CONFIGURATION_SEPARATOR);
			List<String> result = Arrays.stream(properties).map(String::trim)
					.map(key -> SchematicServices.getConfigurationLabel(element, key)).filter(s -> !s.isEmpty())
					.collect(Collectors.toList());
			if (!result.isEmpty()) {
				return LABEL_SEPARATOR + result.stream().collect(Collectors.joining(LABEL_SEPARATOR));
			}
		}
		return "";
	}

	/**
	 * Return if RTPF layer is activated.
	 * 
	 * @param dDiagram
	 *            DDiagram
	 * @return if RTPF layer is activated.
	 */
	private static boolean isRTPFLayerActivated(DDiagram dDiagram) {
		Optional<Layer> findFirst = new DDiagramQuery(dDiagram).getAllActivatedLayers().stream()
				.filter(layer -> SchematicViewpointServices.SCHEMATIC_LAYER_LABEL_RTPF.equals(layer.getName()))
				.findFirst();
		return findFirst.isPresent();
	}

	/**
	 * Return if Schematic layer is activated.
	 * 
	 * @param dDiagram
	 *            DDiagram
	 * @return if Schematic layer is activated.
	 */
	private static boolean isSchematicLayerActivated(DDiagram dDiagram) {
		Optional<Layer> findFirst = new DDiagramQuery(dDiagram).getAllActivatedLayers().stream()
				.filter(layer -> SchematicViewpointServices.SCHEMATIC_LAYER_LABEL.equals(layer.getName())).findFirst();
		return findFirst.isPresent();
	}

	/**
	 * Return if labels from configuration file layer is activated.
	 * 
	 * @param dDiagram
	 *            DDiagram
	 * @return if labels from configuration file layer is activated.
	 */
	private static boolean isLabelsLayerActivated(DDiagram dDiagram) {
		Optional<Layer> findFirst = new DDiagramQuery(dDiagram).getAllActivatedLayers().stream().filter(
				layer -> SchematicViewpointServices.SCHEMATIC_LAYER_LABEL_CONFIGURATION_FILE.equals(layer.getName()))
				.findFirst();
		return findFirst.isPresent();
	}

	/**
	 * Get parent imported mapping label from MappingImport.
	 * 
	 * @param view
	 *            DDiagramElement
	 * @return parent imported mapping label from MappingImport.
	 */
	private static String getDDiagramElementLabel(DDiagramElement view) {
		String label = "";
		// find labelExpression in parent mapping and compute it with IInterpreter
		// code from Sirius.
		String styleLabelExpression = null;
		IInterpreter interpreter = SiriusPlugin.getDefault().getInterpreterRegistry().getInterpreter(view);
		DDiagram parentDiagram = view.getParentDiagram();

		styleLabelExpression = getContainerLabelExpression(view, interpreter, parentDiagram);

		interpreter.setVariable(IInterpreterSiriusVariables.VIEW, view);
		interpreter.setVariable(IInterpreterSiriusVariables.DIAGRAM, parentDiagram);
		if (styleLabelExpression != null) {

			try {
				label = interpreter.evaluateString(view.getTarget(), styleLabelExpression);
			} catch (final EvaluationException e) {
				RuntimeLoggerManager.INSTANCE.error(view.getDiagramElementMapping(),
						StylePackage.eINSTANCE.getBasicLabelStyleDescription_LabelExpression(), e);
			}
		}
		interpreter.unSetVariable(IInterpreterSiriusVariables.VIEW);
		interpreter.unSetVariable(IInterpreterSiriusVariables.DIAGRAM);
		return label;
	}

	/**
	 * container view style label expression.
	 * 
	 * @param view
	 *            DDiagramElement
	 * @param interpreter
	 *            IInterpreter
	 * @param parentDiagram
	 *            DDiagram
	 * @return container view style label expression
	 */
	public static String getContainerLabelExpression(DDiagramElement view, IInterpreter interpreter,
			DDiagram parentDiagram) {
		String styleLabelExpression = null;
		DiagramElementMapping mapping = view.getDiagramElementMapping();
		final DSemanticDecorator cContainer = (DSemanticDecorator) view.eContainer();
		if (cContainer != null) {
			if (mapping instanceof ContainerMappingImport) {
				mapping = ((ContainerMappingImport) view.getDiagramElementMapping()).getImportedMapping();
				BasicLabelStyleDescription style = (BasicLabelStyleDescription) new MappingWithInterpreterHelper(
						interpreter).getBestStyleDescription(mapping, view.getTarget(), view, view, parentDiagram);
				if (style != null) {
					styleLabelExpression = style.getLabelExpression();
				}
			} else if (mapping instanceof EdgeMappingImport) {
				IEdgeMapping edgeMapping = ((EdgeMappingImport) view.getDiagramElementMapping()).getImportedMapping();
				if (edgeMapping instanceof EdgeMapping) {
					EdgeStyleDescription style = (EdgeStyleDescription) MappingWithInterpreterHelper
							.getDefaultStyleDescription((EdgeMapping) edgeMapping);
					if (style != null && style.getCenterLabelStyleDescription() != null) {
						styleLabelExpression = style.getCenterLabelStyleDescription().getLabelExpression();
					}
				}
			} else if (mapping instanceof EdgeMapping) {
				EdgeStyleDescription style = (EdgeStyleDescription) MappingWithInterpreterHelper
						.getDefaultStyleDescription((EdgeMapping) mapping);
				if (style != null && style.getCenterLabelStyleDescription() != null) {
					styleLabelExpression = style.getCenterLabelStyleDescription().getLabelExpression();
				}
			}
		}
		return styleLabelExpression;
	}

}
