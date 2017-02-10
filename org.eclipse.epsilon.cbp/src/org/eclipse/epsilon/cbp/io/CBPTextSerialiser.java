
package org.eclipse.epsilon.cbp.io;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddEObjectsToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EPackageRegistrationEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

public class CBPTextSerialiser extends AbstractCBPSerialiser {

	public CBPTextSerialiser(CBPResource resource) {
		this.ePackages = new HashSet<EPackage>();
		this.eventList = resource.getChangelog().getEventsList();

		this.commonsimpleTypeNameMap = persistenceUtil.getCommonSimpleTypesMap();
		this.textSimpleTypeNameMap = persistenceUtil.getTextSimpleTypesMap();

		this.resource = resource;
	}

	public void serialise(OutputStream outputStream, Map<?, ?> options) throws IOException {
		
		Closeable printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));

		for (Event e : eventList) {
			
			if (e instanceof EPackageRegistrationEvent) {
				handleEPackageRegistrationEvent((EPackageRegistrationEvent) e, printWriter);
			}
			else if (e instanceof AddEObjectsToResourceEvent) {
				handleAddToResourceEvent((AddEObjectsToResourceEvent) e, printWriter);
			}
			else if (e instanceof SetEAttributeEvent) {
				handleSetEAttributeEvent((SetEAttributeEvent) e, printWriter);
			}
			else if (e instanceof AddToEAttributeEvent) {
				handleAddToEAttributeEvent((AddToEAttributeEvent) e, printWriter);
			}
			else if (e instanceof RemoveFromEAttributeEvent) {
				handleRemoveFromAttributeEvent((RemoveFromEAttributeEvent) e, printWriter);
			}
			else if (e instanceof SetEReferenceEvent) {
				handleSetEReferenceEvent((SetEReferenceEvent) e, printWriter);
			}
			else if (e instanceof AddToEReferenceEvent) {
				handleAddToEReferenceEvent((AddToEReferenceEvent) e, printWriter);
			}
			else if (e instanceof RemoveFromEReferenceEvent) {
				handleRemoveFromEReferenceEvent((RemoveFromEReferenceEvent) e, printWriter);
			}
			else if (e instanceof RemoveFromResourceEvent) {
				handleRemoveFromResourceEvent((RemoveFromResourceEvent) e, printWriter);
			}
		}

		printWriter.close();
	}

	/*
	 * event has the format of: 0 (MetaElementTypeID objectID(, )?)*
	 */
	@Override
	protected void handleAddToResourceEvent(AddEObjectsToResourceEvent e, Closeable out) {
		PrintWriter writer = (PrintWriter) out;
		ArrayList<String> eObjectsToCreateList = new ArrayList<String>();

		for (EObject obj : e.getEObjectList()) {
			// if obj is not added already
			if (resource.addObjectToMap(obj)) {
				// add type to object-to-create-list
				eObjectsToCreateList.add(getID(obj.eClass()));

				// add id to object-to-create-list
				eObjectsToCreateList.add(resource.getObjectId(obj) + "");
			} else {
				// should not happen
				System.err.println("redundant creation");
			}
		}

		// delimiter
		String delimiter = "";

		// if create list is not empty
		if (!eObjectsToCreateList.isEmpty()) {
			
			writer.print(SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE + " ");

			int index = 0;
			for (int i = 0; i < (eObjectsToCreateList.size() / 2); i++) {
				// add type-id pair
				writer.print(delimiter + eObjectsToCreateList.get(index) + " " + eObjectsToCreateList.get(index + 1));

				// set delimiter
				delimiter = persistenceUtil.DELIMITER;

				// increase index by 2
				index = index + 2;
			}
		}
		writer.println();
	}

	/*
	 * event type: 2 EObjectID*
	 */
	@Override
	protected void handleRemoveFromResourceEvent(RemoveFromResourceEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;
		List<EObject> removedEObjectsList = e.getEObjectList();
		writer.print(SerialisationEventType.REMOVE_FROM_RESOURCE + " ");

		String delimiter = "";

		for (EObject obj : removedEObjectsList) {
			writer.print(delimiter + resource.getObjectId(obj));
			delimiter = persistenceUtil.DELIMITER;
		}
		writer.println();
	}

	/*
	 * event format: 3/4 objectID EAttributeID value*
	 */
	@Override
	protected void handleSetEAttributeEvent(EAttributeEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;
		// get forcus object
		EObject focusObject = e.getFocusObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		// get serialisation type flag
		String serializationType = SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE + "";
		
		String newValue;
		String delimiter = "";

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			writer.print((serializationType + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eAttribute) + " "));

			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					newValue = String.valueOf(obj);
					newValue = newValue.replace(persistenceUtil.DELIMITER,
							persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER); // escape
																						// delimiter
					writer.print(delimiter + newValue);
				}

				delimiter = persistenceUtil.DELIMITER;
			}
		} else // all other datatypes
		{
			serializationType = SerialisationEventType.SET_EATTRIBUTE_COMPLEX + "";

			writer.print((serializationType + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eAttribute) + " "));

			for (Object obj : e.getEAttributeValuesList()) {
				newValue = (EcoreUtil.convertToString(eDataType, obj));

				if (newValue != null) {
					newValue = newValue.replace(persistenceUtil.DELIMITER,
							persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER); // escape
																						// delimiter
					writer.print(delimiter + newValue);
				}
				delimiter = persistenceUtil.DELIMITER;
			}
		}
		writer.println();
	}

	/*
	 * event format: 5/6 objectID EAttributeID value*
	 */
	@Override
	protected void handleAddToEAttributeEvent(EAttributeEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;
		// get forcus object
		EObject focusObject = e.getFocusObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		// get serialisation type flag
		String serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE + "";
		String newValue;
		String delimiter = "";

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			writer.print((serializationType + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eAttribute) + " "));

			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					newValue = String.valueOf(obj);
					newValue = newValue.replace(persistenceUtil.DELIMITER,
							persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER); // escape
																						// delimiter
					writer.print(delimiter + newValue);
				}
				delimiter = persistenceUtil.DELIMITER;
			}
		} else // all other datatypes
		{
			serializationType = SerialisationEventType.ADD_TO_EATTRIBUTE_COMPLEX + "";

			writer.print((serializationType + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eAttribute) + " "));

			for (Object obj : e.getEAttributeValuesList()) {
				newValue = (EcoreUtil.convertToString(eDataType, obj));

				if (newValue != null) {
					newValue = newValue.replace(persistenceUtil.DELIMITER,
							persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER); // escape
																						// delimiter
					writer.print(delimiter + newValue);
				}
				delimiter = persistenceUtil.DELIMITER;
			}
		}
		writer.println();
	}

	/*
	 * event format: 10 objectID EReferenceID (ECLass ID, EObject (,)?)* 12/9
	 * objectID EReferenceID EObjectID
	 */
	@Override
	protected void handleSetEReferenceEvent(SetEReferenceEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;

		boolean created = false;
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();

		ArrayList<String> eObjectsToAddList = new ArrayList<String>();
		ArrayList<String> eObjectsToCreateList = new ArrayList<String>();

		for (EObject obj : e.getEObjectList()) {
			// if obj is not added already
			if (resource.addObjectToMap(obj)) {
				// add type to object-to-create-list
				eObjectsToCreateList.add(getID(obj.eClass()));

				// add id to object-to-create-list
				eObjectsToCreateList.add(resource.getObjectId(obj) + "");
			} else {
				// add id to object-to-add list
				eObjectsToAddList.add(resource.getObjectId(obj) + "");
			}
		}

		// delimiter
		String delimiter = "";

		// if create list is not empty
		if (!eObjectsToCreateList.isEmpty()) {
			created = true;
			
			writer.print(SerialisationEventType.CREATE_AND_SET_EREFERENCE + " " + resource.getObjectId(focusObject)
					+ " " + getID(focusObject.eClass(), eReference) + " ");

			int index = 0;
			for (int i = 0; i < (eObjectsToCreateList.size() / 2); i++) {
				// add type-id pair
				writer.print(delimiter + eObjectsToCreateList.get(index) + " " + eObjectsToCreateList.get(index + 1));

				// set delimiter
				delimiter = persistenceUtil.DELIMITER;

				// increase index by 2
				index = index + 2;
			}
		}

		// if add list is not empty
		if (!eObjectsToAddList.isEmpty()) {

			if (created) {
				writer.print(SerialisationEventType.ADD_TO_EREFERENCE + " " + resource.getObjectId(focusObject)
							+ " " + getID(focusObject.eClass(), eReference) + " ");
			} else {
				writer.print(SerialisationEventType.SET_EREFERENCE + " " + resource.getObjectId(focusObject) + " "
						+ getID(focusObject.eClass(), eReference) + " ");
			}

			delimiter = "";
			for (Iterator<String> it = eObjectsToAddList.iterator(); it.hasNext();) {
				writer.print(delimiter + it.next());
				delimiter = persistenceUtil.DELIMITER;
			}
		}
		writer.println();
	}

	/*
	 * event format: 11 objectID EReferenceID ((ECLass ID, EObject ID) (,)?)* 12
	 * objectID EReferenceID EObjectID*
	 */
	@Override
	protected void handleAddToEReferenceEvent(AddToEReferenceEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();
		ArrayList<String> eObjectsToAddList = new ArrayList<String>();
		ArrayList<String> eObjectsToCreateList = new ArrayList<String>();

		for (EObject obj : e.getEObjectList()) {
			// if obj is not added already
			if (resource.addObjectToMap(obj)) {
				// add type to object-to-create-list
				eObjectsToCreateList.add(getID(obj.eClass()));

				// add id to object-to-create-list
				eObjectsToCreateList.add(resource.getObjectId(obj) + "");
			} else {
				// add id to object-to-add list
				eObjectsToAddList.add(resource.getObjectId(obj) + "");
			}
		}

		// delimiter
		String delimiter = "";

		// if create list is not empty
		if (!eObjectsToCreateList.isEmpty()) {
			
			writer.print(SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE + " "
					+ resource.getObjectId(focusObject) + " " + getID(focusObject.eClass(), eReference) + " ");

			int index = 0;
			for (int i = 0; i < (eObjectsToCreateList.size() / 2); i++) {
				// add type-id pair
				writer.print(delimiter + eObjectsToCreateList.get(index) + " " + eObjectsToCreateList.get(index + 1));

				// set delimiter
				delimiter = persistenceUtil.DELIMITER;

				// increase index by 2
				index = index + 2;
			}
		}

		// if add list is not empty
		if (!eObjectsToAddList.isEmpty()) {
			
			writer.print(SerialisationEventType.ADD_TO_EREFERENCE + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eReference) + " ");

			delimiter = "";
			for (Iterator<String> it = eObjectsToAddList.iterator(); it.hasNext();) {
				writer.print(delimiter + it.next());
				delimiter = persistenceUtil.DELIMITER;
			}
		}
		writer.println();
	}

	/*
	 * event type: 7/8 objectID EAttributeID value*
	 */
	@Override
	protected void handleRemoveFromAttributeEvent(EAttributeEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;
		// get forcus object
		EObject focusObject = e.getFocusObject();

		// get attr
		EAttribute eAttribute = e.getEAttribute();

		// get data type
		EDataType eDataType = eAttribute.getEAttributeType();

		// get serialisation type flag
		String serializationType = SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + "";
		String newValue;
		String delimiter = "";

		if (getTypeID(eDataType) != SimpleType.COMPLEX_TYPE) {
			writer.print((serializationType + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eAttribute) + " "));

			for (Object obj : e.getEAttributeValuesList()) {
				if (obj != null) {
					newValue = String.valueOf(obj);
					newValue = newValue.replace(persistenceUtil.DELIMITER,
							persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER); // escape
																						// delimiter
					writer.print(delimiter + newValue);
				}
				delimiter = persistenceUtil.DELIMITER;
			}
		} else // all other datatypes
		{
			
			serializationType = SerialisationEventType.REMOVE_FROM_EATTRIBUTE_COMPLEX + "";
			
			writer.print((serializationType + " " + resource.getObjectId(focusObject) + " "
					+ getID(focusObject.eClass(), eAttribute) + " "));

			for (Object obj : e.getEAttributeValuesList()) {
				newValue = (EcoreUtil.convertToString(eDataType, obj));

				if (newValue != null) {
					newValue = newValue.replace(persistenceUtil.DELIMITER,
							persistenceUtil.ESCAPE_CHAR + persistenceUtil.DELIMITER); // escape
																						// delimiter
					writer.print(delimiter + newValue);
				}
				delimiter = persistenceUtil.DELIMITER;
			}
		}
		writer.println();
	}

	/*
	 * event type: 13 objectID EReferenceID EObjectID*
	 */
	@Override
	protected void handleRemoveFromEReferenceEvent(RemoveFromEReferenceEvent e, Closeable out) {

		PrintWriter writer = (PrintWriter) out;
		EObject focusObject = e.getFocusObject();
		EReference eReference = e.getEReference();
		List<EObject> removedEObjectsList = e.getEObjectList();

		
		writer.print(SerialisationEventType.REMOVE_FROM_EREFERENCE + " " + resource.getObjectId(focusObject) + " "
				+ (getID(focusObject.eClass(), eReference) + " "));
		
		String delimiter = "";

		for (EObject obj : removedEObjectsList) {
			writer.print(delimiter + resource.getObjectId(obj));
			delimiter = persistenceUtil.DELIMITER;
		}
		writer.println();
	}

	@Override
	protected void handleEPackageRegistrationEvent(EPackageRegistrationEvent e, Closeable out) {
		ePackageElementsNamesMap = persistenceUtil.generateEPackageElementNamesMap(e.getePackage());
		PrintWriter writer = (PrintWriter) out;
		writer.println(SerialisationEventType.REGISTER_EPACKAGE + " " + e.getePackage().getNsURI());
	}

	public String getID(EClass eClass, EStructuralFeature feature) {
		return ePackageElementsNamesMap
				.getID(eClass.getEPackage().getName() + "-" + eClass.getName() + "-" + feature.getName()) + "";
	}

	public String getID(EClass eClass) {
		return ePackageElementsNamesMap.getID(eClass.getEPackage().getName() + "-" + eClass.getName()) + "";
	}
}
