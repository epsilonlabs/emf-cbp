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
		//REGISTER AND CREATE
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
		else if (event instanceof DeleteEObjectEvent) {
			this.handleEObjectDeletion(eObject, event, line);
		}
		// ATTRIBUTES
		else if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent
				|| event instanceof MoveWithinEAttributeEvent || event instanceof AddToEAttributeEvent
				|| event instanceof RemoveFromEAttributeEvent) {
			this.handleEAttribute(eObject, event, line, value);
		}

		// REFERENCES
		else if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent
				|| event instanceof MoveWithinEReferenceEvent || event instanceof AddToEReferenceEvent
				|| event instanceof RemoveFromEReferenceEvent) {
			if (value instanceof List) {
				List<Object> list = (List<Object>) value;
				for (Object val : list){
					this.handleEReference(eObject, event, line, val);
				}
			} else {
				this.handleEReference(eObject, event, line, value);
			}
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
		else if (value != null
				&& (event instanceof AddToEAttributeEvent || event instanceof RemoveFromEAttributeEvent)) {
			Map<String, List<Line>> eventLinesMap = attributeList.get(eAttribute).getEventLinesMap();
			String addAttributeName = AddToEAttributeEvent.class.getSimpleName();
			String removeAttributeName = RemoveFromEAttributeEvent.class.getSimpleName();
			String moveAttributeName = MoveWithinEAttributeEvent.class.getSimpleName();

			int addAttributeLastLine = -1;
			int removeAttributeLastLine = -1;

			List<Line> addAttributeLines = new ArrayList<Line>();
			List<Line> removeAttributeLines = new ArrayList<Line>();
			List<Line> moveWithinAttributeLines = new ArrayList<Line>();

			if (eventLinesMap.containsKey(addAttributeName)) {
				for (Line line : eventLinesMap.get(addAttributeName)) {
					if (line.getValue().equals(value)) {
						addAttributeLines.add(line);
					}
				}
				addAttributeLastLine = addAttributeLines.get(addAttributeLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(removeAttributeName)) {
				for (Line line : eventLinesMap.get(removeAttributeName)) {
					if (line.getValue().equals(value)) {
						removeAttributeLines.add(line);
					}
				}
				removeAttributeLastLine = removeAttributeLines.get(removeAttributeLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(moveAttributeName)) {
				for (Line line : eventLinesMap.get(moveAttributeName)) {

					@SuppressWarnings("unchecked")
					List<Object> values = (List<Object>) line.getValue();
					if (values.get(0).equals(value) || values.get(1).equals(value)) {
						moveWithinAttributeLines.add(line);
					}
				}
			}

			if (removeAttributeLastLine > addAttributeLastLine) {
				if (addAttributeLines != null)
					this.addLinesToIgnoreList(addAttributeLines);
				if (removeAttributeLines != null)
					this.addLinesToIgnoreList(removeAttributeLines);
				if (moveWithinAttributeLines != null)
					this.addLinesToIgnoreList(moveWithinAttributeLines);
			} else if (addAttributeLastLine > removeAttributeLastLine) {
				if (addAttributeLines != null && addAttributeLines.size() > 1)
					this.addLinesToIgnoreList(addAttributeLines.subList(0, addAttributeLines.size() - 1));
				if (removeAttributeLines != null)
					this.addLinesToIgnoreList(removeAttributeLines);
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
	private void handleEReference(EObject eObject, ChangeEvent<?> event, int lineNumber, Object value) {
		EReference eReference = ((EReferenceEvent) event).getEReference();
		Map<EObject, EObjectEventLines> referenceList = eObjectEventLinesMap.get(eObject).getReferences();
		if (!referenceList.containsKey(eReference)) {
			EObjectEventLines eReferenceHistory = new EObjectEventLines(eObject);
			eReferenceHistory.addEventLine(event, lineNumber);
			referenceList.put(eReference, eReferenceHistory);
		} else {
			referenceList.get(eReference).addEventLine(event, lineNumber);
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
			eObjectEventLinesMap.get(value).addEventLine(event, lineNumber);

			Map<String, List<Line>> eventLinesMap = eObjectEventLinesMap.get(value).getEventLinesMap();
			String addReferenceName = AddToEReferenceEvent.class.getSimpleName();
			String removeReferenceName = RemoveFromEReferenceEvent.class.getSimpleName();
			String addResourceName = AddToResourceEvent.class.getSimpleName();
			String removeResourceName = RemoveFromResourceEvent.class.getSimpleName();
			String moveWithinReferenceName = MoveWithinEReferenceEvent.class.getSimpleName();

			int addReferencetLastLine = -1;
			int removeReferenceLastLine = -1;
			int addResourceLastLine = -1;
			int removeResourceLastLine = -1;

			List<Line> addReferenceLines = null;
			List<Line> removeReferenceLines = null;
			List<Line> addResourceLines = null;
			List<Line> removeResourceLines = null;
			List<Line> moveWithiReferenceLines = new ArrayList<Line>();

			if (eventLinesMap.containsKey(addReferenceName)) {
				addReferenceLines = eventLinesMap.get(addReferenceName);
				addReferencetLastLine = addReferenceLines.get(addReferenceLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(removeReferenceName)) {
				removeReferenceLines = eventLinesMap.get(removeReferenceName);
				removeReferenceLastLine = removeReferenceLines.get(removeReferenceLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(addResourceName)) {
				addResourceLines = eventLinesMap.get(addResourceName);
				addResourceLastLine = addResourceLines.get(addResourceLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(removeResourceName)) {
				removeResourceLines = eventLinesMap.get(removeResourceName);
				removeResourceLastLine = removeResourceLines.get(removeResourceLines.size() - 1).getLineNumber();
			}
			if (eventLinesMap.containsKey(moveWithinReferenceName)) {
				moveWithiReferenceLines = eventLinesMap.get(moveWithinReferenceName);
			}

			if (removeReferenceLastLine > addReferencetLastLine) {
				if (addReferenceLines != null)
					this.addLinesToIgnoreList(addReferenceLines);
				if (removeReferenceLines != null)
					this.addLinesToIgnoreList(removeReferenceLines);
				if (addResourceLines != null)
					this.addLinesToIgnoreList(addResourceLines);
				if (removeResourceLines != null)
					this.addLinesToIgnoreList(removeResourceLines);
				if (moveWithiReferenceLines != null)
					this.addLinesToIgnoreList(moveWithiReferenceLines);
			} else if (addReferencetLastLine > removeReferenceLastLine) {
				if (addReferenceLines != null && addReferenceLines.size() > 1)
					this.addLinesToIgnoreList(addReferenceLines.subList(0, addReferenceLines.size() - 1));
				if (removeReferenceLines != null)
					this.addLinesToIgnoreList(removeReferenceLines);
				if (addResourceLines != null)
					this.addLinesToIgnoreList(addResourceLines);
				if (removeResourceLines != null)
					this.addLinesToIgnoreList(removeResourceLines);
			} else if (removeResourceLastLine > addResourceLastLine) {
				if (addResourceLines != null)
					this.addLinesToIgnoreList(addResourceLines);
				if (removeResourceLines != null)
					this.addLinesToIgnoreList(removeResourceLines);
			}

		}
	}

	private void handleEObjectDeletion(EObject eObject, ChangeEvent<?> event, int line) {
		eObjectEventLinesMap.get(eObject).addEventLine(event, line);

		EObjectEventLines deletedEObjectEventLines = eObjectEventLinesMap.get(eObject);

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
