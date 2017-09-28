package org.eclipse.epsilon.cbp.history;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import org.eclipse.epsilon.cbp.resource.CBPResource;

public class ModelHistory extends ObjectHistory {

	protected long beforeAttributeSetUnset = 0;
	protected long afterAttributeSetUnset = 0;
	protected long beforeAttributeAddRemoveMove = 0;
	protected long afterAttributeAddRemoveMove = 0;
	protected long beforeReferenceSetUnset = 0;
	protected long afterReferenceSetUnset = 0;
	protected long beforeReferenceAddRemoveMove = 0;
	protected long afterReferenceAddRemoveMove = 0;
	protected long beforeDelete = 0;
	protected long afterDelete = 0;
	protected long countAttributeSetUnset = 0;
	protected long countAttributeAddRemoveMove = 0;
	protected long countReferenceSetUnset = 0;
	protected long countReferenceAddRemoveMove = 0;
	protected long countDelete = 0;
	protected long timeAttributeSetUnset = 0;
	protected long timeAttributeAddRemoveMove = 0;
	protected long timeReferenceSetUnset = 0;
	protected long timeReferenceAddRemoveMove = 0;
	protected long timeDelete = 0;
	protected double avgTimeAttributeSetUnset = 0;
	protected double avgTimeAttributeAddRemoveMove = 0;
	protected double avgTimeReferenceSetUnset = 0;
	protected double avgTimeReferenceAddRemoveMove = 0;
	protected double avgTimeDelete = 0;
	protected long totalTimeAttributeSetUnset = 0;
	protected long totalTimeAttributeAddRemoveMove = 0;
	protected long totalTimeReferenceSetUnset = 0;
	protected long totalTimeReferenceAddRemoveMove = 0;
	protected long totalTimeDelete = 0;

	protected Map<EObject, ObjectHistory> objectHistoryMap = new HashMap<>();
	protected List<Long> ignoreList;
	protected CBPResource resource;

	private void addLinesToIgnoreList(EventHistory lines) {
		for (Line line : lines) {
			if (!ignoreList.contains(line.getEventNumber())) {
				ignoreList.add(line.getEventNumber());
			}
		}
	}

	public ModelHistory(List<Long> ignoreList, EObject eObject, CBPResource resource) {
		super(eObject);
		this.ignoreList = ignoreList;
		this.resource = resource;
	}

	public ModelHistory(List<Long> ignoreList, CBPResource resource) {
		super(null);
		this.ignoreList = ignoreList;
		this.resource = resource;
	}

	public Map<EObject, ObjectHistory> geteObjectHistoryMap() {
		return objectHistoryMap;
	}

	public void addObjectHistoryLine(EObject eObject, ChangeEvent<?> event, long eventNumber) {
		this.addObjectHistoryLine(eObject, event, eventNumber, null);
	}

	@SuppressWarnings("unchecked")
	public void addObjectHistoryLine(EObject eObject, ChangeEvent<?> event, long eventNumber, Object value) {
		// REGISTER
		if (event instanceof RegisterEPackageEvent) {
			String name = RegisterEPackageEvent.class.getSimpleName();
			if (!this.getEventHistoryMap().containsKey(name)) {
				EventHistory eventHistory = new EventHistory(event);
				this.getEventHistoryMap().put(name, eventHistory);
				this.addEventLine(event, eventNumber);
			} else {
				this.addEventLine(event, eventNumber);
			}
		}

		// CREATE
		if (/* event instanceof RegisterEPackageEvent || */ event instanceof CreateEObjectEvent) {
			if (!objectHistoryMap.containsKey(eObject)) {
				ObjectHistory eObjectHistory = new ObjectHistory(eObject);
				eObjectHistory.addEventLine(event, eventNumber);
				objectHistoryMap.put(eObject, eObjectHistory);
			} else {
				objectHistoryMap.get(eObject).addEventLine(event, eventNumber);
			}
		}
		// RESOURCE
		else if (event instanceof AddToResourceEvent || event instanceof RemoveFromResourceEvent) {
			if (!objectHistoryMap.containsKey(eObject)) {
				ObjectHistory eObjectHistory = new ObjectHistory(eObject);
				eObjectHistory.addEventLine(event, eventNumber);
				objectHistoryMap.put(eObject, eObjectHistory);
			} else {
				objectHistoryMap.get(eObject).addEventLine(event, eventNumber);
			}

		}
		// OBJECT DELETION
		else if (event instanceof DeleteEObjectEvent) {
			countDelete += 1;
			beforeDelete = System.nanoTime();

			DeleteEObjectEvent deletedEvent = (DeleteEObjectEvent) event;
			EObject parent = (deletedEvent.getParentEObject() instanceof EObject)
					? (EObject) deletedEvent.getParentEObject() : null;
			EReference reference = (deletedEvent.getEReference() instanceof EReference)
					? (EReference) deletedEvent.getEReference() : null;

			// register to parent
			if (parent != null && reference != null) {
				ObjectHistory parentHistory = objectHistoryMap.get(parent);
				ReferenceHistory refHistory = parentHistory.getReferences().get(reference);
				if (refHistory != null) {
					refHistory.addEventLine(deletedEvent, eventNumber, eObject);
				}
			}

			// register to self
			ObjectHistory eObjectHistory = objectHistoryMap.get(eObject);
			eObjectHistory.addEventLine(event, eventNumber);

			// remove children
			for (ReferenceHistory rh : eObjectHistory.getReferences().values()) {
				EventHistory eh = rh.getEventHistoryMap().get((deletedEvent).getClass().getSimpleName());
				if (eh != null) {
					for (Line line : eh) {
						EObject subEObject = (EObject) line.getValue();
						long lineNum = line.getEventNumber();
						this.handleEObjectDeletion(subEObject, deletedEvent, lineNum, true);
					}
				}
			}

			// remove self
			this.handleEObjectDeletion(eObject, event, eventNumber, false);
			
			// remove siblings if isRemoved is false
			if (parent != null && reference != null && eObjectHistory.isMoved() == false) {
				ObjectHistory parentHistory = objectHistoryMap.get(parent);
				ReferenceHistory rh = parentHistory.getReferences().get(reference);
				if (rh != null) {
					EventHistory eh = rh.getEventHistoryMap().get((deletedEvent).getClass().getSimpleName());
					if (eh!= null){
						for (Line line : eh) {
							EObject subEObject = (EObject) line.getValue();
							long lineNum = line.getEventNumber();
							this.handleEObjectDeletion(subEObject, deletedEvent, lineNum, true);
						}
					}
				}
			}

			afterDelete = System.nanoTime();
			timeDelete = afterDelete - beforeDelete;
			totalTimeDelete += timeDelete;
			avgTimeDelete = (double) totalTimeDelete / countDelete;
		}
		// ATTRIBUTE SET AND UNSET EVENTS
		else if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent) {
			countAttributeSetUnset += 1;
			beforeAttributeSetUnset = System.nanoTime();
			this.handleAttributeSetUnsetEvents(eObject, event, eventNumber, value);
			afterAttributeSetUnset = System.nanoTime();
			timeAttributeSetUnset = afterAttributeSetUnset - beforeAttributeSetUnset;
			totalTimeAttributeSetUnset += timeAttributeSetUnset;
			avgTimeAttributeSetUnset = (double) totalTimeAttributeSetUnset / countAttributeSetUnset;
		}
		// ATTRIBUTE ADD, REMOVE, AND MOVE EVENTS
		else if (event instanceof MoveWithinEAttributeEvent || event instanceof AddToEAttributeEvent
				|| event instanceof RemoveFromEAttributeEvent) {
			countAttributeAddRemoveMove += 1;
			beforeAttributeAddRemoveMove = System.nanoTime();
			if (value instanceof List) {
				for (Object val : (List<Object>) value) {
					this.handleAttributeAddRemoveMoveEvents(eObject, event, eventNumber, val);
				}
			} else {

				this.handleAttributeAddRemoveMoveEvents(eObject, event, eventNumber, value);
				afterAttributeAddRemoveMove = System.nanoTime();
			}
			timeAttributeAddRemoveMove = afterAttributeAddRemoveMove - beforeAttributeAddRemoveMove;
			totalTimeAttributeAddRemoveMove += timeAttributeAddRemoveMove;
			avgTimeAttributeAddRemoveMove = (double) totalTimeAttributeAddRemoveMove / countAttributeAddRemoveMove;
		}

		// REFERENCE SET AND UNSET EVENTS
		else if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent) {
			countReferenceSetUnset += 1;
			beforeReferenceSetUnset = System.nanoTime();
			this.handleReferenceSetUnsetEvents(eObject, event, eventNumber, (EObject) value);
			afterReferenceSetUnset = System.nanoTime();
			timeReferenceSetUnset = afterReferenceSetUnset - beforeReferenceSetUnset;
			totalTimeReferenceSetUnset += timeReferenceSetUnset;
			avgTimeReferenceSetUnset = (double) totalTimeReferenceSetUnset / countReferenceSetUnset;
		}

		// REFERENCE ADD, REMOVE, AND MOVE EVENTS
		else if (event instanceof MoveWithinEReferenceEvent || event instanceof AddToEReferenceEvent
				|| event instanceof RemoveFromEReferenceEvent) {

			countReferenceAddRemoveMove += 1;
			beforeReferenceAddRemoveMove = System.nanoTime();
			if (value instanceof List) {
				for (Object val : (List<Object>) value) {
					this.handleReferenceAddRemoveMoveEvents(eObject, event, eventNumber, (EObject) val);
				}
			} else {

				this.handleReferenceAddRemoveMoveEvents(eObject, event, eventNumber, (EObject) value);
				afterReferenceAddRemoveMove = System.nanoTime();
			}
			timeReferenceAddRemoveMove = afterReferenceAddRemoveMove - beforeReferenceAddRemoveMove;
			totalTimeReferenceAddRemoveMove += timeReferenceAddRemoveMove;
			avgTimeReferenceAddRemoveMove = (double) totalTimeReferenceAddRemoveMove / countReferenceAddRemoveMove;

		}

	}

	private void handleAttributeSetUnsetEvents(EObject eObject, ChangeEvent<?> event, long eventNumber, Object value) {
		EAttribute eAttribute = ((EAttributeEvent) event).getEAttribute();
		boolean x = objectHistoryMap.containsValue(eObject);
		Map<EObject, AttributeHistory> attributeList = objectHistoryMap.get(eObject).getAttributes();
		if (!attributeList.containsKey(eAttribute)) {
			AttributeHistory eAttributeHistory = new AttributeHistory(eObject);
			eAttributeHistory.addEventLine(event, eventNumber, value);
			attributeList.put(eAttribute, eAttributeHistory);
		} else {
			attributeList.get(eAttribute).addEventLine(event, eventNumber, value);
		}

		// ignoring Set and Unset Attribute
		if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent) {
			Map<String, EventHistory> eventLinesMap = attributeList.get(eAttribute).getEventHistoryMap();
			String setAttributeName = SetEAttributeEvent.class.getSimpleName();
			String unsetAttributeName = UnsetEAttributeEvent.class.getSimpleName();
			long setAttributeLastLine = -1L;
			long unsetAttributeLastLine = -1L;

			EventHistory setAttributeLines = null;
			EventHistory unsetAttributeLines = null;

			if (eventLinesMap.containsKey(setAttributeName)) {
				setAttributeLines = eventLinesMap.get(setAttributeName);
				setAttributeLastLine = setAttributeLines.get(setAttributeLines.size() - 1).getEventNumber();
			}
			if (eventLinesMap.containsKey(unsetAttributeName)) {
				unsetAttributeLines = eventLinesMap.get(unsetAttributeName);
				unsetAttributeLastLine = unsetAttributeLines.get(unsetAttributeLines.size() - 1).getEventNumber();
			}

			if (unsetAttributeLastLine > setAttributeLastLine) {
				if (setAttributeLines != null)
					this.addLinesToIgnoreList(setAttributeLines);
				if (unsetAttributeLines != null)
					this.addLinesToIgnoreList(unsetAttributeLines);
			} else if (setAttributeLastLine > unsetAttributeLastLine) {
				if (setAttributeLines != null && setAttributeLines.size() > 1) {
					this.addLinesToIgnoreList(setAttributeLines.subList(0, setAttributeLines.size() - 1));
				}
				if (unsetAttributeLines != null)
					this.addLinesToIgnoreList(unsetAttributeLines);
			}
		}
	}

	private void handleAttributeAddRemoveMoveEvents(EObject eObject, ChangeEvent<?> event, long eventNumber,
			Object value) {
		EAttribute eAttribute = ((EAttributeEvent) event).getEAttribute();
		Map<EObject, AttributeHistory> attributeList = objectHistoryMap.get(eObject).getAttributes();
		if (!attributeList.containsKey(eAttribute)) {
			AttributeHistory eAttributeHistory = new AttributeHistory(eObject);
			eAttributeHistory.addEventLine(event, eventNumber, value);
			attributeList.put(eAttribute, eAttributeHistory);
		} else {
			attributeList.get(eAttribute).addEventLine(event, eventNumber, value);
		}

		// ignoring Add To and Remove EAttribute
		if (value != null && (event instanceof AddToEAttributeEvent || event instanceof RemoveFromEAttributeEvent
				|| event instanceof MoveWithinEAttributeEvent)) {
			ObjectHistory eAttributeHistory = attributeList.get(eAttribute);
			if (event instanceof MoveWithinEAttributeEvent) {
				eAttributeHistory.setMoved(true);
			}
			Map<String, EventHistory> eventLinesMap = eAttributeHistory.getEventHistoryMap();
			String addAttributeName = AddToEAttributeEvent.class.getSimpleName();
			String removeAttributeName = RemoveFromEAttributeEvent.class.getSimpleName();
			String moveAttributeName = MoveWithinEAttributeEvent.class.getSimpleName();

			EventHistory addAttributeLines = null;
			EventHistory removeAttributeLines = null;
			EventHistory moveWithinAttributeLines = null;

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
				eAttributeHistory.setMoved(false);
				if (addAttributeLines != null)
					this.addLinesToIgnoreList(addAttributeLines);
				if (removeAttributeLines != null)
					this.addLinesToIgnoreList(removeAttributeLines);
				if (moveWithinAttributeLines != null)
					this.addLinesToIgnoreList(moveWithinAttributeLines);
			}
			if (delta == 1) {
				eAttributeHistory.setMoved(false);
				if (addAttributeLines != null) {
					EventHistory temp = new EventHistory(event);
					for (Line lAdd : addAttributeLines) {
						for (Line lRem : removeAttributeLines) {
							if (lAdd.getValue().equals(lRem.getValue())) {
								temp.add(lAdd);
							}
						}
					}
					this.addLinesToIgnoreList(temp);
				}
				if (removeAttributeLines != null)
					this.addLinesToIgnoreList(removeAttributeLines);
				if (moveWithinAttributeLines != null)
					this.addLinesToIgnoreList(moveWithinAttributeLines);
			}
			if (delta != 0 && delta != 1) {
				if (eAttributeHistory.isMoved() == false) {
					long addAttributeLastLine = -1L;
					long removeAttributeLastLine = -1L;
					EventHistory valueAddAttributeLines = new EventHistory(event);
					EventHistory valueRemoveAttributeLines = new EventHistory(event);

					if (eventLinesMap.containsKey(addAttributeName)) {
						for (Line line : eventLinesMap.get(addAttributeName)) {
							if (line.getValue().equals(value)) {
								valueAddAttributeLines.add(line);
							}
						}
						if (valueAddAttributeLines.size() > 0)
							addAttributeLastLine = valueAddAttributeLines.get(valueAddAttributeLines.size() - 1)
									.getEventNumber();
					}
					if (eventLinesMap.containsKey(removeAttributeName)) {
						for (Line line : eventLinesMap.get(removeAttributeName)) {
							if (line.getValue().equals(value)) {
								valueRemoveAttributeLines.add(line);
							}
						}
						if (valueRemoveAttributeLines.size() > 0)
							removeAttributeLastLine = valueRemoveAttributeLines
									.get(valueRemoveAttributeLines.size() - 1).getEventNumber();
					}

					if (addAttributeLastLine > removeAttributeLastLine) {
						if (valueAddAttributeLines != null)
							this.addLinesToIgnoreList(
									valueAddAttributeLines.subList(0, valueAddAttributeLines.size() - 1));
						if (valueRemoveAttributeLines != null)
							this.addLinesToIgnoreList(valueRemoveAttributeLines);
					} else if (addAttributeLastLine < removeAttributeLastLine) {
						if (valueAddAttributeLines != null)
							this.addLinesToIgnoreList(valueAddAttributeLines);
						if (valueRemoveAttributeLines != null)
							this.addLinesToIgnoreList(valueRemoveAttributeLines);
					}
				}
			}
		}
	}

	private void handleReferenceSetUnsetEvents(EObject eObject, ChangeEvent<?> event, long eventNumber, EObject value) {
		EReference eReferenceTarget = ((EReferenceEvent) event).getEReference();
		Map<EObject, ReferenceHistory> referenceList = objectHistoryMap.get(eObject).getReferences();
		if (!referenceList.containsKey(eReferenceTarget)) {
			ReferenceHistory eReferenceHistory = new ReferenceHistory(eObject);
			eReferenceHistory.addEventLine(event, eventNumber, value);
			referenceList.put(eReferenceTarget, eReferenceHistory);
		} else {
			referenceList.get(eReferenceTarget).addEventLine(event, eventNumber, value);
		}

		// ignoring Set and Unset Reference
		if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent) {
			Map<String, EventHistory> eventLinesMap = referenceList.get(eReferenceTarget).getEventHistoryMap();
			String seReferenceName = SetEReferenceEvent.class.getSimpleName();
			String unsetReferenceName = UnsetEReferenceEvent.class.getSimpleName();
			long setReferenceLastLine = -1L;
			long unsetReferenceLastLine = -1L;

			EventHistory setReferenceLines = null;
			EventHistory unsetReferenceLines = null;

			if (eventLinesMap.containsKey(seReferenceName)) {
				setReferenceLines = eventLinesMap.get(seReferenceName);
				setReferenceLastLine = setReferenceLines.get(setReferenceLines.size() - 1).getEventNumber();
			}
			if (eventLinesMap.containsKey(unsetReferenceName)) {
				unsetReferenceLines = eventLinesMap.get(unsetReferenceName);
				unsetReferenceLastLine = unsetReferenceLines.get(unsetReferenceLines.size() - 1).getEventNumber();
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
	}

	private void handleReferenceAddRemoveMoveEvents(EObject eObject, ChangeEvent<?> event, long eventNumber,
			EObject value) {
		EReference eReferenceTarget = ((EReferenceEvent) event).getEReference();
		Map<EObject, ReferenceHistory> referenceList = objectHistoryMap.get(eObject).getReferences();
		if (!referenceList.containsKey(eReferenceTarget)) {
			ReferenceHistory eReferenceHistory = new ReferenceHistory(eObject);
			eReferenceHistory.addEventLine(event, eventNumber, value);
			referenceList.put(eReferenceTarget, eReferenceHistory);
		} else {
			referenceList.get(eReferenceTarget).addEventLine(event, eventNumber, value);
		}

		// ignoring Add To and Remove From Reference
		if (value != null && (event instanceof AddToEReferenceEvent || event instanceof RemoveFromEReferenceEvent
				|| event instanceof MoveWithinEReferenceEvent)) {

			// also add these AddTo/RemoveFromReference events to the value
			// object (not only the target
			// object)
			if (!objectHistoryMap.containsKey(value)) {
				ObjectHistory eReferenceObjectHistory = new ObjectHistory(value);
				eReferenceObjectHistory.addEventLine(event, eventNumber);
				objectHistoryMap.put(eReferenceTarget, eReferenceObjectHistory);
			} else {
				objectHistoryMap.get(value).addEventLine(event, eventNumber);
			}

			ObjectHistory eReferenceHistory = referenceList.get(eReferenceTarget);
			ObjectHistory valueHistory = objectHistoryMap.get(value);
			if (event instanceof MoveWithinEReferenceEvent) {
				eReferenceHistory.setMoved(true);
				EventHistory lines = eReferenceHistory.getEventHistoryMap()
						.get(AddToEReferenceEvent.class.getSimpleName());
				Set<EObject> eObjectList = new HashSet<>();
				for (Line line : lines) {
					eObjectList.add((EObject) line.getValue());
				}
				for (EObject item : eObjectList) {
					objectHistoryMap.get(item).setMoved(true);
				}
			}

			Map<String, EventHistory> targetEventLinesMap = referenceList.get(eReferenceTarget).getEventHistoryMap();
			String addReferenceName = AddToEReferenceEvent.class.getSimpleName();
			String removeReferenceName = RemoveFromEReferenceEvent.class.getSimpleName();
			String moveWithinReferenceName = MoveWithinEReferenceEvent.class.getSimpleName();

			EventHistory addReferenceLines = null;
			EventHistory removeReferenceLines = null;
			EventHistory moveWithinReferenceLines = null;

			// TARGET OBJECT
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

				eReferenceHistory.setMoved(false);
				EventHistory lines = eReferenceHistory.getEventHistoryMap()
						.get(AddToEReferenceEvent.class.getSimpleName());
				Set<EObject> eObjectList = new HashSet<>();
				for (Line line : lines) {
					eObjectList.add((EObject) line.getValue());
				}
				for (EObject item : eObjectList) {
					objectHistoryMap.get(item).setMoved(false);
				}

				if (addReferenceLines != null)
					this.addLinesToIgnoreList(addReferenceLines);
				if (removeReferenceLines != null)
					this.addLinesToIgnoreList(removeReferenceLines);
				if (moveWithinReferenceLines != null)
					this.addLinesToIgnoreList(moveWithinReferenceLines);

			} else if (delta == 1) {
				eReferenceHistory.setMoved(false);

				EventHistory lines = eReferenceHistory.getEventHistoryMap()
						.get(AddToEReferenceEvent.class.getSimpleName());
				Set<EObject> eObjectList = new HashSet<>();
				for (Line line : lines) {
					eObjectList.add((EObject) line.getValue());
				}
				for (EObject item : eObjectList) {
					objectHistoryMap.get(item).setMoved(false);
				}

				if (addReferenceLines != null) {
					EventHistory temp = new EventHistory(event);
					for (Line lAdd : addReferenceLines) {
						for (Line lRem : removeReferenceLines) {
							if (lAdd.getValue().equals(lRem.getValue())) {
								temp.add(lAdd);
							}
						}
					}
					this.addLinesToIgnoreList(temp);
				}
				if (removeReferenceLines != null)
					this.addLinesToIgnoreList(removeReferenceLines);
				if (moveWithinReferenceLines != null)
					this.addLinesToIgnoreList(moveWithinReferenceLines);
			}

			else if (delta != 0 && delta != 1) {
				if (valueHistory.isMoved() == false) {
					long addReferenceLastLine = -1L;
					long removeReferenceLastLine = -1L;
					long addResourceLastLine = -1L;
					long removeResourceLastLine = -1L;

					Map<String, EventHistory> valueEventLinesMap = objectHistoryMap.get(value).getEventHistoryMap();

					String addResourceName = AddToResourceEvent.class.getSimpleName();
					String removeResourceName = RemoveFromResourceEvent.class.getSimpleName();

					EventHistory addResourceLines = null;
					EventHistory removeResourceLines = null;

					if (valueEventLinesMap.containsKey(addReferenceName)) {
						addReferenceLines = valueEventLinesMap.get(addReferenceName);
						addReferenceLastLine = addReferenceLines.get(addReferenceLines.size() - 1).getEventNumber();
					}
					if (valueEventLinesMap.containsKey(removeReferenceName)) {
						removeReferenceLines = valueEventLinesMap.get(removeReferenceName);
						removeReferenceLastLine = removeReferenceLines.get(removeReferenceLines.size() - 1)
								.getEventNumber();
					}
					if (valueEventLinesMap.containsKey(addResourceName)) {
						addResourceLines = valueEventLinesMap.get(addResourceName);
						addResourceLastLine = addResourceLines.get(addResourceLines.size() - 1).getEventNumber();
					}
					if (valueEventLinesMap.containsKey(removeResourceName)) {
						removeResourceLines = valueEventLinesMap.get(removeResourceName);
						removeResourceLastLine = removeResourceLines.get(removeResourceLines.size() - 1)
								.getEventNumber();
					}
					if (valueEventLinesMap.containsKey(moveWithinReferenceName)) {
						moveWithinReferenceLines = valueEventLinesMap.get(moveWithinReferenceName);
					}

					if (removeReferenceLastLine > addReferenceLastLine) {
						if (addReferenceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(addReferenceLines);
						if (removeReferenceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(removeReferenceLines);
						if (addResourceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(addResourceLines);
						if (removeResourceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(removeResourceLines);
						if (moveWithinReferenceLines != null)
							this.addLinesToIgnoreList(moveWithinReferenceLines);
					} else if (addReferenceLastLine > removeReferenceLastLine) {
						if (addReferenceLines != null && addReferenceLines.size() > 1
								&& eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(addReferenceLines.subList(0, addReferenceLines.size() - 1));
						if (removeReferenceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(removeReferenceLines);
						if (addResourceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(addResourceLines);
						if (removeResourceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(removeResourceLines);
					} else if (removeResourceLastLine > addResourceLastLine) {
						if (addResourceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(addResourceLines);
						if (removeResourceLines != null && eReferenceTarget.isContainment() == true)
							this.addLinesToIgnoreList(removeResourceLines);
					}
				}
			}
			// ---------------------------------------------

		}
	}

	private void handleEObjectDeletion(EObject eObject, ChangeEvent<?> event, long eventNumber, boolean childDeletion) {

		ObjectHistory deletedEObjectHistory = objectHistoryMap.get(eObject);
		if (childDeletion == true || deletedEObjectHistory.isMoved() == false) {

			// get all current object's references' lines
			Map<EObject, ReferenceHistory> references = deletedEObjectHistory.getReferences();
			for (Entry<EObject, ReferenceHistory> referenceEntry : references.entrySet()) {
				ReferenceHistory referenceHistory = referenceEntry.getValue();

				for (Entry<String, EventHistory> eventLines : referenceHistory.getEventHistoryMap().entrySet()) {
					EventHistory lines = eventLines.getValue();

					this.addLinesToIgnoreList(lines);
				}
			}

			// get all current object's attributes' lines
			Map<EObject, AttributeHistory> attributes = deletedEObjectHistory.getAttributes();
			for (Entry<EObject, AttributeHistory> attributeEntry : attributes.entrySet()) {
				AttributeHistory attributeHistory = attributeEntry.getValue();
				for (Entry<String, EventHistory> eventLines : attributeHistory.getEventHistoryMap().entrySet()) {
					EventHistory lines = eventLines.getValue();
					this.addLinesToIgnoreList(lines);
				}
			}

			// get all current object's lines
			for (Entry<String, EventHistory> eventLines : deletedEObjectHistory.getEventHistoryMap().entrySet()) {
				String key = eventLines.getKey();
				if (key.equals(RemoveFromResourceEvent.class.getSimpleName())
						|| key.equals(AddToResourceEvent.class.getSimpleName())
						|| key.equals(AddToEReferenceEvent.class.getSimpleName())
						|| key.equals(RemoveFromEReferenceEvent.class.getSimpleName())
						|| key.equals(MoveWithinEReferenceEvent.class.getSimpleName())
						|| key.equals(DeleteEObjectEvent.class.getSimpleName())
						|| key.equals(CreateEObjectEvent.class.getSimpleName())) {
					EventHistory lines = eventLines.getValue();
					this.addLinesToIgnoreList(lines);
				}
			}
		}
	}

	public double getAvgTimeDelete() {
		return this.avgTimeDelete;
	}

	public double getAvgTimeAttributeSetUnset() {
		return avgTimeAttributeSetUnset;
	}

	public double getAvgTimeAttributeAddRemoveMove() {
		return avgTimeAttributeAddRemoveMove;
	}

	public double getAvgTimeReferenceSetUnset() {
		return avgTimeReferenceSetUnset;
	}

	public double getAvgTimeReferenceAddRemoveMove() {
		return avgTimeReferenceAddRemoveMove;
	}

	public void printStructure() {
		for (Entry<EObject, ObjectHistory> entry1 : this.geteObjectHistoryMap().entrySet()) {
			EObject eObject = entry1.getKey();
			ObjectHistory eObjectEventLineHistory = entry1.getValue();
			System.out.println("EObject: " + resource.getURIFragment(eObject) + " -------------------");
			for (Entry<String, EventHistory> entry2 : eObjectEventLineHistory.getEventHistoryMap().entrySet()) {
				String eventName = entry2.getKey();
				List<Line> lines = entry2.getValue();
				System.out.println("    " + eventName + " = " + lines);
			}
			// attributes
			Map<EObject, AttributeHistory> attributeList = eObjectEventLineHistory.getAttributes();
			System.out.println("    EAttribute:");
			for (Entry<EObject, AttributeHistory> entry2 : attributeList.entrySet()) {
				EAttribute eAttribute = (EAttribute) entry2.getKey();
				ObjectHistory eAttributeHistory = entry2.getValue();
				System.out.println("        " + eAttribute.getName() + " -------------------");
				for (Entry<String, EventHistory> entry3 : eAttributeHistory.getEventHistoryMap().entrySet()) {
					String eventName = entry3.getKey();
					List<Line> lines = entry3.getValue();
					System.out.println("            " + eventName + " = " + lines);
				}
			}
			// references
			Map<EObject, ReferenceHistory> referenceList = eObjectEventLineHistory.getReferences();
			System.out.println("    EReference:");
			for (Entry<EObject, ReferenceHistory> entry2 : referenceList.entrySet()) {
				EReference eReference = (EReference) entry2.getKey();
				ObjectHistory eReferenceHistory = entry2.getValue();
				System.out.println("        " + eReference.getName() + " -------------------");
				for (Entry<String, EventHistory> entry3 : eReferenceHistory.getEventHistoryMap().entrySet()) {
					String eventName = entry3.getKey();
					List<Line> lines = entry3.getValue();
					System.out.println("            " + eventName + " = " + lines);
				}
			}
		}
	}
}
