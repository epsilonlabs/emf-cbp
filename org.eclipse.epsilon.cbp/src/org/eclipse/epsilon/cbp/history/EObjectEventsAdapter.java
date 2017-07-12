package org.eclipse.epsilon.cbp.history;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
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
public class EObjectEventsAdapter {

	protected Map<EObject, EObjectEventLines> eObjectHistoryList = new HashMap<>();
	protected Set<Integer> ignoreList;

	public EObjectEventsAdapter(Set<Integer> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public Map<EObject, EObjectEventLines> geteObjectHistoryList() {
		return eObjectHistoryList;
	}

	public void add(EObject eObject, ChangeEvent<?> event, int line) {
		this.add(eObject, event, line, null);
	}

	public void add(EObject eObject, ChangeEvent<?> event, int line, EObject value) {
		if (event instanceof RegisterEPackageEvent || event instanceof CreateEObjectEvent) {
			if (!eObjectHistoryList.containsKey(eObject)) {
				EObjectEventLines eObjectHistory = new EObjectEventLines(eObject);
				eObjectHistory.addEventLine(event, line);
				eObjectHistoryList.put(eObject, eObjectHistory);
			} else {
				eObjectHistoryList.get(eObject).addEventLine(event, line);
			}
		} else if (event instanceof AddToResourceEvent || event instanceof RemoveFromResourceEvent) {
			eObjectHistoryList.get(eObject).addEventLine(event, line);
		}
		// ATTRIBUTES
		else if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent
				|| event instanceof MoveWithinEAttributeEvent || event instanceof AddToEAttributeEvent
				|| event instanceof RemoveFromEAttributeEvent) {
			EAttribute eAttribute = ((EAttributeEvent) event).getEAttribute();
			Map<EObject, EObjectEventLines> attributeList = eObjectHistoryList.get(eObject).getAttributes();
			if (!attributeList.containsKey(eAttribute)) {
				EObjectEventLines eAttributeHistory = new EObjectEventLines(eObject);
				eAttributeHistory.addEventLine(event, line);
				attributeList.put(eAttribute, eAttributeHistory);
			} else {
				attributeList.get(eAttribute).addEventLine(event, line);
			}

			// ignoring Set and Unset Attribute
			if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent) {
				Map<String, List<Integer>> eventLinesMap = attributeList.get(eAttribute).getEventLinesMap();
				String setAttributeName = SetEAttributeEvent.class.getSimpleName();
				String unsetAttributeName = UnsetEAttributeEvent.class.getSimpleName();
				int setAttributeLastLine = -1;
				int unsetAttributeLastLine = -1;

				List<Integer> setAttributeLines = null;
				List<Integer> unsetAttributeLines = null;

				if (eventLinesMap.containsKey(setAttributeName)) {
					setAttributeLines = eventLinesMap.get(setAttributeName);
					setAttributeLastLine = setAttributeLines.get(setAttributeLines.size() - 1);
				}
				if (eventLinesMap.containsKey(unsetAttributeName)) {
					unsetAttributeLines = eventLinesMap.get(unsetAttributeName);
					unsetAttributeLastLine = unsetAttributeLines.get(unsetAttributeLines.size() - 1);
				}

				if (unsetAttributeLastLine > setAttributeLastLine) {
					if (setAttributeLines != null)
						ignoreList.addAll(setAttributeLines);
					if (unsetAttributeLines != null)
						ignoreList.addAll(unsetAttributeLines);
				} else if (setAttributeLastLine > unsetAttributeLastLine) {
					if (setAttributeLines != null && setAttributeLines.size() > 1)
						ignoreList.addAll(setAttributeLines.subList(0, setAttributeLines.size() - 1));
					if (unsetAttributeLines != null)
						ignoreList.addAll(unsetAttributeLines);
				}
			}
		}

		// REFERENCES
		else if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent
				|| event instanceof MoveWithinEReferenceEvent || event instanceof AddToEReferenceEvent
				|| event instanceof RemoveFromEReferenceEvent) {

			EReference eReference = ((EReferenceEvent) event).getEReference();
			Map<EObject, EObjectEventLines> referenceList = eObjectHistoryList.get(eObject).getReferences();
			if (!referenceList.containsKey(eReference)) {
				EObjectEventLines eReferenceHistory = new EObjectEventLines(eObject);
				eReferenceHistory.addEventLine(event, line);
				referenceList.put(eReference, eReferenceHistory);
			} else {
				referenceList.get(eReference).addEventLine(event, line);
			}

			// ignoring Set and Unset Reference
			if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent) {
				Map<String, List<Integer>> eventLinesMap = referenceList.get(eReference).getEventLinesMap();
				String seReferenceName = SetEReferenceEvent.class.getSimpleName();
				String unsetReferenceName = UnsetEReferenceEvent.class.getSimpleName();
				int setReferenceLastLine = -1;
				int unsetReferenceLastLine = -1;

				List<Integer> setReferenceLines = null;
				List<Integer> unsetReferenceLines = null;

				if (eventLinesMap.containsKey(seReferenceName)) {
					setReferenceLines = eventLinesMap.get(seReferenceName);
					setReferenceLastLine = setReferenceLines.get(setReferenceLines.size() - 1);
				}
				if (eventLinesMap.containsKey(unsetReferenceName)) {
					unsetReferenceLines = eventLinesMap.get(unsetReferenceName);
					unsetReferenceLastLine = unsetReferenceLines.get(unsetReferenceLines.size() - 1);
				}

				if (unsetReferenceLastLine > setReferenceLastLine) {
					if (setReferenceLines != null)
						ignoreList.addAll(setReferenceLines);
					if (unsetReferenceLines != null)
						ignoreList.addAll(unsetReferenceLines);
				} else if (setReferenceLastLine > unsetReferenceLastLine) {
					if (setReferenceLines != null && setReferenceLines.size() > 1)
						ignoreList.addAll(setReferenceLines.subList(0, setReferenceLines.size() - 1));
					if (unsetReferenceLines != null)
						ignoreList.addAll(unsetReferenceLines);
				}
			}

			// ignoring Add To and Remove From Reference
			if (value != null && (event instanceof AddToEReferenceEvent || event instanceof RemoveFromEReferenceEvent)) {
				
				// also add these two events to the value object not only the target
				// object
				eObjectHistoryList.get(value).addEventLine(event, line);
				
				Map<String, List<Integer>> eventLinesMap = eObjectHistoryList.get(value).getEventLinesMap();
				String addReferenceName = AddToEReferenceEvent.class.getSimpleName();
				String removeReferenceName = RemoveFromEReferenceEvent.class.getSimpleName();
				String addResourceName = AddToResourceEvent.class.getSimpleName();
				String removeResourceName = RemoveFromResourceEvent.class.getSimpleName();

				int addReferencetLastLine = -1;
				int removeReferenceLastLine = -1;
				int addResourceLastLine = -1;
				int removeResourceLastLine = -1;

				List<Integer> addReferenceLines = null;
				List<Integer> removeReferenceLines = null;
				List<Integer> addResourceLines = null;
				List<Integer> removeResourceLines = null;

				if (eventLinesMap.containsKey(addReferenceName)) {
					addReferenceLines = eventLinesMap.get(addReferenceName);
					addReferencetLastLine = addReferenceLines.get(addReferenceLines.size() - 1);
				}
				if (eventLinesMap.containsKey(removeReferenceName)) {
					removeReferenceLines = eventLinesMap.get(removeReferenceName);
					removeReferenceLastLine = removeReferenceLines.get(removeReferenceLines.size() - 1);
				}
				if (eventLinesMap.containsKey(addResourceName)) {
					addResourceLines = eventLinesMap.get(addResourceName);
					addResourceLastLine = addResourceLines.get(addResourceLines.size() - 1);
				}
				if (eventLinesMap.containsKey(removeResourceName)) {
					removeResourceLines = eventLinesMap.get(removeResourceName);
					removeResourceLastLine = removeResourceLines.get(removeResourceLines.size() - 1);
				}

				if (removeReferenceLastLine > addReferencetLastLine) {
					if (addReferenceLines != null)
						ignoreList.addAll(addReferenceLines);
					if (removeReferenceLines != null)
						ignoreList.addAll(removeReferenceLines);
					if (addResourceLines != null)
						ignoreList.addAll(addResourceLines);
					if (removeResourceLines != null)
						ignoreList.addAll(removeResourceLines);
				} else if (addReferencetLastLine > removeReferenceLastLine) {
					if (addReferenceLines != null && addReferenceLines.size() > 1)
						ignoreList.addAll(addReferenceLines.subList(0, addReferenceLines.size() - 1));
					if (removeReferenceLines != null)
						ignoreList.addAll(removeReferenceLines);
					if (addResourceLines != null)
						ignoreList.addAll(addResourceLines);
					if (removeResourceLines != null)
						ignoreList.addAll(removeResourceLines);
				} else if (removeResourceLastLine > addResourceLastLine) {
					if (addResourceLines != null)
						ignoreList.addAll(addResourceLines);
					if (removeResourceLines != null)
						ignoreList.addAll(removeResourceLines);
				}

			}
		}

	}
}
