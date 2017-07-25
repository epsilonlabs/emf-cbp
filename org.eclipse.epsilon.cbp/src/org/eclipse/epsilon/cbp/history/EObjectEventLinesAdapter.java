package org.eclipse.epsilon.cbp.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DeleteEObjectEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EReferenceEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;

/***
 * 
 * @author Alfa This is a Class to capture lines in the change-based model XML
 *         where an operation involves an object. It maps operations applied to
 *         an object to their respective lines in the change-based model XML.
 */
public class EObjectEventLinesAdapter {

	protected Map<EObject, EObjectEventLines> eObjectEventLinesMap = new HashMap<>();
	protected Set<Integer> ignoreList;

	private void addLinesToIgnoreList(List<Line> lines) {
		for (Line line : lines) {
			ignoreList.add(line.getLineNumber());
		}
	}

	public EObjectEventLinesAdapter(Set<Integer> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public Map<EObject, EObjectEventLines> geteObjectHistoryList() {
		return eObjectEventLinesMap;
	}

	public void add(EObject eObject, ChangeEvent<?> event, int line) {
		this.add(eObject, event, line, null);
	}

	public void add(EObject eObject, ChangeEvent<?> event, int line, Object value) {
		// REGISTER AND CREATE
		if (event instanceof RegisterEPackageEvent || event instanceof CreateEObjectEvent) {
			if (!eObjectEventLinesMap.containsKey(eObject)) {
				EObjectEventLines eObjectHistory = new EObjectEventLines(eObject);
				eObjectHistory.addEventLine(event, line);
				eObjectEventLinesMap.put(eObject, eObjectHistory);
			} else {
				eObjectEventLinesMap.get(eObject).addEventLine(event, line);
			}
		}
		// RESOURCE
		else if (event instanceof AddToResourceEvent || event instanceof RemoveFromResourceEvent) {
			eObjectEventLinesMap.get(eObject).addEventLine(event, line);

		}
		// OBJECT DELETION
//		else if (event instanceof DeleteEObjectEvent) {
//			this.handleEObjectDeletion(eObject, event, line);
//		}
		// ATTRIBUTES
		else if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent
				|| event instanceof MoveWithinEAttributeEvent || event instanceof AddToEAttributeEvent
				|| event instanceof RemoveFromEAttributeEvent) {
			if (value instanceof List) {
				for (Object val : (List<Object>) value) {
					this.handleEAttribute(eObject, event, line, val);
				}
			} else {
				this.handleEAttribute(eObject, event, line, value);
			}
		}

		// REFERENCES
		else if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent
				|| event instanceof MoveWithinEReferenceEvent || event instanceof AddToEReferenceEvent
				|| event instanceof RemoveFromEReferenceEvent) {

			this.handleEReference(eObject, event, line, (EObject) value);
		}

	}

	private void handleEAttribute(EObject eObject, ChangeEvent<?> event, int lineNumber, Object value) {
		EAttribute eAttribute = ((EAttributeEvent) event).getEAttribute();
		Map<EObject, EObjectEventLines> attributeList = eObjectEventLinesMap.get(eObject).getAttributes();
		if (!attributeList.containsKey(eAttribute)) {
			EObjectEventLines eAttributeHistory = new EObjectEventLines(eObject);
			eAttributeHistory.addEventLine(event, lineNumber, value);
			attributeList.put(eAttribute, eAttributeHistory);
		} else {
			attributeList.get(eAttribute).addEventLine(event, lineNumber, value);
		}

		// ignoring Set and Unset Attribute
		if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent) {
			Map<String, List<Line>> eventLinesMap = attributeList.get(eAttribute).getEventLinesMap();
			String setAttributeName = SetEAttributeEvent.class.getSimpleName();
			String unsetAttributeName = UnsetEAttributeEvent.class.getSimpleName();
			int setAttributeLastLine = -1;
			int unsetAttributeLastLine = -1;

			List<Line> setAttributeLines = null;
			List<Line> unsetAttributeLines = null;

			if (eventLinesMap.containsKey(setAttributeName)) {
				setAttributeLines = eventLinesMap.get(setAttributeName);
				setAttributeLastLine = setAttributeLines.get(setAttributeLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(unsetAttributeName)) {
				unsetAttributeLines = eventLinesMap.get(unsetAttributeName);
				unsetAttributeLastLine = unsetAttributeLines.get(unsetAttributeLines.size() - 1).getLineNumber();
			}

			if (unsetAttributeLastLine > setAttributeLastLine) {
				if (setAttributeLines != null)
					this.addLinesToIgnoreList(setAttributeLines);
				if (unsetAttributeLines != null)
					this.addLinesToIgnoreList(unsetAttributeLines);
			} else if (setAttributeLastLine > unsetAttributeLastLine) {
				if (setAttributeLines != null && setAttributeLines.size() > 1)
					this.addLinesToIgnoreList(setAttributeLines.subList(0, setAttributeLines.size() - 1));
				if (unsetAttributeLines != null)
					this.addLinesToIgnoreList(unsetAttributeLines);
			}
		}
		// ignoring Add To and Remove EAttribute
		else if (value != null && (event instanceof AddToEAttributeEvent || event instanceof RemoveFromEAttributeEvent
				|| event instanceof MoveWithinEAttributeEvent)) {
			Map<String, List<Line>> eventLinesMap = attributeList.get(eAttribute).getEventLinesMap();
			String addAttributeName = AddToEAttributeEvent.class.getSimpleName();
			String removeAttributeName = RemoveFromEAttributeEvent.class.getSimpleName();
			String moveAttributeName = MoveWithinEAttributeEvent.class.getSimpleName();

			// int addAttributeLastLine = -1;
			// int removeAttributeLastLine = -1;

			List<Line> addAttributeLines = null;
			List<Line> removeAttributeLines = null;
			List<Line> moveWithinAttributeLines = null;

			if (eventLinesMap.containsKey(addAttributeName)) {
				addAttributeLines = eventLinesMap.get(addAttributeName);
			}
			if (eventLinesMap.containsKey(removeAttributeName)) {
				removeAttributeLines = eventLinesMap.get(removeAttributeName);
			}
			if (eventLinesMap.containsKey(moveAttributeName)) {
				moveWithinAttributeLines = eventLinesMap.get(moveAttributeName);
			}

			int delta = -1;
			if (addAttributeLines != null && removeAttributeLines != null) {
				if (addAttributeLines.size() > 0 && removeAttributeLines.size() > 0) {
					delta = addAttributeLines.size() - removeAttributeLines.size();
				}
			}

			if (delta == 0) {
				if (addAttributeLines != null)
					this.addLinesToIgnoreList(addAttributeLines);
				if (removeAttributeLines != null)
					this.addLinesToIgnoreList(removeAttributeLines);
				if (moveWithinAttributeLines != null)
					this.addLinesToIgnoreList(moveWithinAttributeLines);
			}
		}
	}

	/***
	 * 
	 * @param eObject
	 * @param event
	 * @param lineNumber
	 * @param value
	 */
	private void handleEReference(EObject eObject, ChangeEvent<?> event, int lineNumber, EObject value) {
		EReference eReference = ((EReferenceEvent) event).getEReference();
		Map<EObject, EObjectEventLines> referenceList = eObjectEventLinesMap.get(eObject).getReferences();
		if (!referenceList.containsKey(eReference)) {
			EObjectEventLines eReferenceHistory = new EObjectEventLines(eObject);
			eReferenceHistory.addEventLine(event, lineNumber, value);
			referenceList.put(eReference, eReferenceHistory);
		} else {
			referenceList.get(eReference).addEventLine(event, lineNumber, value);
		}

		// ignoring Set and Unset Reference
		if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent) {
			Map<String, List<Line>> eventLinesMap = referenceList.get(eReference).getEventLinesMap();
			String seReferenceName = SetEReferenceEvent.class.getSimpleName();
			String unsetReferenceName = UnsetEReferenceEvent.class.getSimpleName();
			int setReferenceLastLine = -1;
			int unsetReferenceLastLine = -1;

			List<Line> setReferenceLines = null;
			List<Line> unsetReferenceLines = null;

			if (eventLinesMap.containsKey(seReferenceName)) {
				setReferenceLines = eventLinesMap.get(seReferenceName);
				setReferenceLastLine = setReferenceLines.get(setReferenceLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(unsetReferenceName)) {
				unsetReferenceLines = eventLinesMap.get(unsetReferenceName);
				unsetReferenceLastLine = unsetReferenceLines.get(unsetReferenceLines.size() - 1).getLineNumber();
			}

			if (unsetReferenceLastLine > setReferenceLastLine) {
				if (setReferenceLines != null)
					this.addLinesToIgnoreList(setReferenceLines);
				if (unsetReferenceLines != null)
					this.addLinesToIgnoreList(unsetReferenceLines);
			} else if (setReferenceLastLine > unsetReferenceLastLine) {
				if (setReferenceLines != null && setReferenceLines.size() > 1)
					this.addLinesToIgnoreList(setReferenceLines.subList(0, setReferenceLines.size() - 1));
				if (unsetReferenceLines != null)
					this.addLinesToIgnoreList(unsetReferenceLines);
			}
		}

		// ignoring Add To and Remove From Reference
		else if (value != null && (event instanceof AddToEReferenceEvent || event instanceof RemoveFromEReferenceEvent
				|| event instanceof MoveWithinEReferenceEvent)) {

			// also add these AddTo/RemoveFromReference events to the value
			// object (not only the target
			// object)
			if (!eObjectEventLinesMap.containsKey(value)) {
				EObjectEventLines eReferenceHistory = new EObjectEventLines(value);
				eReferenceHistory.addEventLine(event, lineNumber);
				eObjectEventLinesMap.put(eReference, eReferenceHistory);
			} else {
				eObjectEventLinesMap.get(value).addEventLine(event, lineNumber);
			}

			Map<String, List<Line>> targetEventLinesMap = referenceList.get(eReference).getEventLinesMap();
			String addReferenceName = AddToEReferenceEvent.class.getSimpleName();
			String removeReferenceName = RemoveFromEReferenceEvent.class.getSimpleName();
			String moveWithinReferenceName = MoveWithinEReferenceEvent.class.getSimpleName();

			//

			List<Line> addReferenceLines = null;
			List<Line> removeReferenceLines = null;
			List<Line> moveWithinReferenceLines = null;

			// TARGET OJBECT
			if (targetEventLinesMap.containsKey(addReferenceName)) {
				addReferenceLines = targetEventLinesMap.get(addReferenceName);
			}
			if (targetEventLinesMap.containsKey(removeReferenceName)) {
				removeReferenceLines = targetEventLinesMap.get(removeReferenceName);
			}
			if (targetEventLinesMap.containsKey(moveWithinReferenceName)) {
				moveWithinReferenceLines = targetEventLinesMap.get(moveWithinReferenceName);
			}

			int delta = -1;
			if (addReferenceLines != null && removeReferenceLines != null) {
				if (addReferenceLines.size() > 0 && removeReferenceLines.size() > 0) {
					delta = addReferenceLines.size() - removeReferenceLines.size();
				}
			}

			if (delta == 0) {
				if (addReferenceLines != null)
					this.addLinesToIgnoreList(addReferenceLines);
				if (removeReferenceLines != null)
					this.addLinesToIgnoreList(removeReferenceLines);
				if (moveWithinReferenceLines != null)
					this.addLinesToIgnoreList(moveWithinReferenceLines);
				
				for (Line l : removeReferenceLines){
					this.handleEObjectDeletion((EObject)l.getValue(), event, l.getLineNumber());
				}
			}

//			// VALUE OBJECT
//			Map<String, List<Line>> valueEventLinesMap = eObjectEventLinesMap.get(value).getEventLinesMap();
//
//			String addResourceName = AddToResourceEvent.class.getSimpleName();
//			String removeResourceName = RemoveFromResourceEvent.class.getSimpleName();
//
//			List<Line> addResourceLines = null;
//			List<Line> removeResourceLines = null;
//
//			int addReferenceLastLine = -1;
//			int removeReferenceLastLine = -1;
//			int addResourceLastLine = -1;
//			int removeResourceLastLine = -1;
//
//			if (valueEventLinesMap.containsKey(addReferenceName)) {
//				addReferenceLines = valueEventLinesMap.get(addReferenceName);
//				addReferenceLastLine = addReferenceLines.get(addReferenceLines.size() - 1).getLineNumber();
//			}
//			if (valueEventLinesMap.containsKey(removeReferenceName)) {
//				removeReferenceLines = valueEventLinesMap.get(removeReferenceName);
//				removeReferenceLastLine = removeReferenceLines.get(removeReferenceLines.size() - 1).getLineNumber();
//			}
//			if (valueEventLinesMap.containsKey(addResourceName)) {
//				addResourceLines = valueEventLinesMap.get(addResourceName);
//				addResourceLastLine = addResourceLines.get(addResourceLines.size() - 1).getLineNumber();
//			}
//			if (valueEventLinesMap.containsKey(removeResourceName)) {
//				removeResourceLines = valueEventLinesMap.get(removeResourceName);
//				removeResourceLastLine = removeResourceLines.get(removeResourceLines.size() - 1).getLineNumber();
//			}
//			if (valueEventLinesMap.containsKey(moveWithinReferenceName)) {
//				moveWithinReferenceLines = valueEventLinesMap.get(moveWithinReferenceName);
//			}
//
//			if (removeReferenceLastLine > addReferenceLastLine) {
//				if (addReferenceLines != null)
//					this.addLinesToIgnoreList(addReferenceLines);
//				if (removeReferenceLines != null)
//					this.addLinesToIgnoreList(removeReferenceLines);
//				if (addResourceLines != null)
//					this.addLinesToIgnoreList(addResourceLines);
//				if (removeResourceLines != null)
//					this.addLinesToIgnoreList(removeResourceLines);
//				if (moveWithinReferenceLines != null)
//					this.addLinesToIgnoreList(moveWithinReferenceLines);
//			} else if (addReferenceLastLine > removeReferenceLastLine) {
//				if (addReferenceLines != null && addReferenceLines.size() > 1)
//					this.addLinesToIgnoreList(addReferenceLines.subList(0, addReferenceLines.size() - 1));
//				if (removeReferenceLines != null)
//					this.addLinesToIgnoreList(removeReferenceLines);
//				if (addResourceLines != null)
//					this.addLinesToIgnoreList(addResourceLines);
//				if (removeResourceLines != null)
//					this.addLinesToIgnoreList(removeResourceLines);
//			} else if (removeResourceLastLine > addResourceLastLine) {
//				if (addResourceLines != null)
//					this.addLinesToIgnoreList(addResourceLines);
//				if (removeResourceLines != null)
//					this.addLinesToIgnoreList(removeResourceLines);
//			}

		}
	}

	private void handleEObjectDeletion(EObject eObject, ChangeEvent<?> event, int line) {
		eObjectEventLinesMap.get(eObject).addEventLine(event, line);

		EObjectEventLines deletedEObjectEventLines = eObjectEventLinesMap.get(eObject);
		Map<String, List<Line>> targetEventLinesMap = deletedEObjectEventLines.getEventLinesMap();

		// get all current object's attributes' lines
		Map<EObject, EObjectEventLines> attributes = deletedEObjectEventLines.getAttributes();
		for (Entry<EObject, EObjectEventLines> entry : attributes.entrySet()) {
			EObjectEventLines attributeEventLines = entry.getValue();
			for (Entry<String, List<Line>> eventLines : attributeEventLines.getEventLinesMap().entrySet()) {
				List<Line> lines = eventLines.getValue();
				this.addLinesToIgnoreList(lines);
			}
		}

		// get all current object's references' lines
		Map<EObject, EObjectEventLines> references = deletedEObjectEventLines.getReferences();
		for (Entry<EObject, EObjectEventLines> entry : references.entrySet()) {
			EObjectEventLines referenceEventLines = entry.getValue();
			for (Entry<String, List<Line>> eventLines : referenceEventLines.getEventLinesMap().entrySet()) {
				List<Line> lines = eventLines.getValue();
				this.addLinesToIgnoreList(lines);
			}
		}

		// get all current object's lines
		for (Entry<String, List<Line>> eventLines : deletedEObjectEventLines.getEventLinesMap().entrySet()) {
			String key = eventLines.getKey();
			if (key.equals(AddToResourceEvent.class.getSimpleName())
					|| key.equals(RemoveFromResourceEvent.class.getSimpleName())
					|| key.equals(DeleteEObjectEvent.class.getSimpleName())
					|| key.equals(CreateEObjectEvent.class.getSimpleName())) {
				List<Line> lines = eventLines.getValue();
				this.addLinesToIgnoreList(lines);
			}
		}
	}
}
