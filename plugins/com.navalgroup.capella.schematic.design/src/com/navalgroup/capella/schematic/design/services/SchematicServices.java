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

import java.util.Optional;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.polarsys.capella.common.data.modellingcore.AbstractType;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellacore.CapellacoreFactory;
import org.polarsys.capella.core.data.capellacore.IntegerPropertyValue;
import org.polarsys.capella.core.data.capellacore.StringPropertyValue;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.pa.PhysicalComponent;

import com.navalgroup.capella.schematic.design.Messages;
import com.navalgroup.capella.schematic.design.SchematicDesignPlugin;

/**
 * Services for Schematic.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public final class SchematicServices {

	/**
	 * Property value key for image.
	 */
	public static final String SCHEMATIC_IMAGE_NAME_PROPERTY_VALUE = "schematic.image";
	/**
	 * Property value key for image position.
	 */
	public static final String SCHEMATIC_IMAGE_POSITION_PROPERTY_VALUE = "schematic.image.position";

	/**
	 * Property value key for RTPF.
	 */
	public static final String RTPF_PROPERTY_VALUE = "RTPF";

	/**
	 * Constructor.
	 */
	private SchematicServices() {
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
		Optional<String> schematicPropertyValue = Optional.empty();
		// if part, search in abstract type
		if (element instanceof Part) {
			AbstractType type = ((Part) element).getAbstractType();
			if (type instanceof CapellaElement) {
				schematicPropertyValue = getSchematicImagePropertyValue((CapellaElement) type);
			}
		}
		// search in capella element
		if (!schematicPropertyValue.isPresent()) {
			Optional<StringPropertyValue> propertyValue = getStringPropertyValueValue(element,
					SCHEMATIC_IMAGE_NAME_PROPERTY_VALUE);
			if (propertyValue.isPresent()) {
				String value = propertyValue.get().getValue();
				if (value != null && !value.isEmpty()) {
					schematicPropertyValue = ConfigurationFileServices.getConfigurationImagePropertyValue(value);
				}
			}
		}
		return schematicPropertyValue;
	}

	/**
	 * Check if element references a physical component.
	 * 
	 * @param element
	 *            EObject
	 * @return if element references a physical component
	 */
	public static boolean isSchematicElement(EObject element) {
		if (element instanceof DDiagramElement) {
			EObject target = ((DDiagramElement) element).getTarget();
			// if part, search in abstract type
			if (target instanceof Part) {
				target = ((Part) target).getAbstractType();
			}
			return target instanceof PhysicalComponent;
		}
		return false;

	}

	/**
	 * Get StringPropertyValue with key 'key' applied to element if exists. Search
	 * in appliedPropertyValues and appliedPropertyValueGroups.
	 * 
	 * @param element
	 *            CapellaElement
	 * @param key
	 *            String
	 * @return a StringPropertyValue with key 'key' applied to element if exists.
	 */
	public static Optional<StringPropertyValue> getStringPropertyValueValue(CapellaElement element, String key) {
		Optional<StringPropertyValue> propertyValue = element.getAppliedPropertyValues().stream()
				.filter(StringPropertyValue.class::isInstance).map(StringPropertyValue.class::cast)
				.filter(pv -> key.equalsIgnoreCase(pv.getName())).findFirst();
		if (!propertyValue.isPresent()) {
			propertyValue = element.getAppliedPropertyValueGroups().stream()
					.map(group -> group.getAppliedPropertyValues()).filter(StringPropertyValue.class::isInstance)
					.map(StringPropertyValue.class::cast).filter(pv -> key.equalsIgnoreCase(pv.getName())).findFirst();
		}
		return propertyValue;
	}

	/**
	 * Get IntegerPropertyValue with key 'key' applied to element if exists. Search
	 * in appliedPropertyValues and appliedPropertyValueGroups.
	 * 
	 * @param element
	 *            CapellaElement
	 * @param key
	 *            String
	 * @return a IntegerPropertyValue with key 'key' applied to element if exists.
	 */
	public static Optional<IntegerPropertyValue> getIntegerPropertyValueValue(CapellaElement element, String key) {
		Optional<IntegerPropertyValue> propertyValue = element.getAppliedPropertyValues().stream()
				.filter(IntegerPropertyValue.class::isInstance).map(IntegerPropertyValue.class::cast)
				.filter(pv -> key.equalsIgnoreCase(pv.getName())).findFirst();
		if (!propertyValue.isPresent()) {
			propertyValue = element.getAppliedPropertyValueGroups().stream()
					.map(group -> group.getAppliedPropertyValues()).filter(IntegerPropertyValue.class::isInstance)
					.map(IntegerPropertyValue.class::cast).filter(pv -> key.equalsIgnoreCase(pv.getName())).findFirst();
		}
		return propertyValue;
	}

	/**
	 * Get RTPF property value value or empty.
	 * 
	 * @param element
	 *            CapellaElement
	 * @return RTPF property value value or empty.
	 */
	public static String getRTPFLabel(CapellaElement element) {
		Optional<StringPropertyValue> propertyValue = SchematicServices.getStringPropertyValueValue(element,
				SchematicServices.RTPF_PROPERTY_VALUE);
		if (propertyValue.isPresent()) {
			String value = propertyValue.get().getValue();
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}
		return "";
	}

	/**
	 * Get configuration file label property value value or empty.
	 * 
	 * @param element
	 *            CapellaElement
	 * @return configuration file label property value value or empty.
	 */
	public static String getConfigurationLabel(CapellaElement element, String key) {
		Optional<StringPropertyValue> propertyValue = SchematicServices.getStringPropertyValueValue(element, key);
		if (propertyValue.isPresent()) {
			String value = propertyValue.get().getValue();
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}
		return "";
	}

	/**
	 * Create IntegerPropertyValue for image position.
	 * 
	 * @param element
	 *            CapellaElement
	 * @return IntegerPropertyValue for image position.
	 */
	public static IntegerPropertyValue createPositionIntegerPropertyValue(CapellaElement element) {
		IntegerPropertyValue pv = CapellacoreFactory.eINSTANCE.createIntegerPropertyValue();
		pv.setName(SCHEMATIC_IMAGE_POSITION_PROPERTY_VALUE);
		element.getOwnedPropertyValues().add(pv);
		element.getAppliedPropertyValues().add(pv);
		return pv;
	}

	/**
	 * Save image position in element property value.
	 * 
	 * @param element
	 *            CapellaElement
	 * @param position
	 */
	public static void savePosition(CapellaElement element, int position) {
		Session session = SessionManager.INSTANCE.getSession(element);
		if (session == null) {
			SchematicDesignPlugin.getPlugin().logError(Messages.SchematicService_NoSessionFound);
			return;
		}

		Optional<IntegerPropertyValue> propertyValueValue = getIntegerPropertyValueValue(element,
				SchematicServices.SCHEMATIC_IMAGE_POSITION_PROPERTY_VALUE);
		RecordingCommand command = null;
		// if back to initial image -> remove property value
		if (PositionConstants.NORTH == position && propertyValueValue.isPresent()) {
			command = new RecordingCommand(session.getTransactionalEditingDomain()) {

				@Override
				protected void doExecute() {
					EcoreUtil.delete(propertyValueValue.get());

				}
			};
		} else if (PositionConstants.NORTH != position) {
			// create or update property value with new position
			command = new RecordingCommand(session.getTransactionalEditingDomain()) {

				@Override
				protected void doExecute() {
					IntegerPropertyValue positionPV;
					if (!propertyValueValue.isPresent()) {
						positionPV = SchematicServices.createPositionIntegerPropertyValue(element);
					} else {
						positionPV = propertyValueValue.get();
					}
					if (position != positionPV.getValue()) {
						positionPV.setValue(position);
					}

				}
			};
		}
		if (command != null) {
			session.getTransactionalEditingDomain().getCommandStack().execute(command);
		}

	}
}
