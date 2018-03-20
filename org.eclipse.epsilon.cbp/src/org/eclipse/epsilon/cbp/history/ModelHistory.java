package org.eclipse.epsilon.cbp.history;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import java.util.Set;

import org.eclipse.emf.common.util.EList;
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

	protected int ignoredSetAttCount = 0;
	protected int ignoredUnsetAttCount = 0;
	protected int ignoredAddAttCount = 0;
	protected int ignoredRemoveAttCount = 0;
	protected int ignoredMoveAttCount = 0;
	protected int ignoredSetRefCount = 0;
	protected int ignoredUnsetRefCount = 0;
	protected int ignoredAddRefCount = 0;
	protected int ignoredRemoveRefCount = 0;
	protected int ignoredMoveRefCount = 0;
	protected int ignoredDeleteCount = 0;
	protected int ignoredAddResCount = 0;
	protected int ignoredRemoveResCount = 0;
	protected int ignoredPackageCount = 0;
	protected int ignoredSessionCount = 0;
	protected int ignoredCreateCount = 0;

	public int getIgnoredSetAttCount() {
		return ignoredSetAttCount;
	}

	public int getIgnoredUnsetAttCount() {
		return ignoredUnsetAttCount;
	}

	public int getIgnoredAddAttCount() {
		return ignoredAddAttCount;
	}

	public int getIgnoredRemoveAttCount() {
		return ignoredRemoveAttCount;
	}

	public int getIgnoredMoveAttCount() {
		return ignoredMoveAttCount;
	}

	public int getIgnoredSetRefCount() {
		return ignoredSetRefCount;
	}

	public int getIgnoredUnsetRefCount() {
		return ignoredUnsetRefCount;
	}

	public int getIgnoredAddRefCount() {
		return ignoredAddRefCount;
	}

	public int getIgnoredRemoveRefCount() {
		return ignoredRemoveRefCount;
	}

	public int getIgnoredMoveRefCount() {
		return ignoredMoveRefCount;
	}

	public int getIgnoredDeleteCount() {
		return ignoredDeleteCount;
	}

	public int getIgnoredAddResCount() {
		return ignoredAddResCount;
	}

	public int getIgnoredRemoveResCount() {
		return ignoredRemoveResCount;
	}

	public int getIgnoredPackageCount() {
		return ignoredPackageCount;
	}

	public int getIgnoredSessionCount() {
		return ignoredSessionCount;
	}

	public int getIgnoredCreateCount() {
		return ignoredCreateCount;
	}

	protected Map<EObject, ObjectHistory> objectHistoryMap = new HashMap<>();
	protected Set<Integer> ignoreSet;
	protected List<Integer> ignoreList;
	protected CBPResource resource;

	private void addLinesToIgnoreList(EventHistory lines) {
		for (Line line : lines) {
			if (ignoreSet.add(line.getEventNumber())) {
				ignoreList.add(line.getEventNumber());
			}
		}
	}

	public ModelHistory(Set<Integer> ignoreSet, EObject eObject, CBPResource resource) {
		super(eObject);
		this.ignoreSet = ignoreSet;
		this.resource = resource;
	}

	public ModelHistory(Set<Integer> ignoreSet, CBPResource resource, List<Integer> ignoreList) {
		super(null);
		this.ignoreSet = ignoreSet;
		this.resource = resource;
		this.ignoreList = ignoreList;
	}

	public Map<EObject, ObjectHistory> geteObjectHistoryMap() {
		return objectHistoryMap;
	}

	public void addObjectHistoryLine(EObject eObject, ChangeEvent<?> event, int eventNumber) {
		this.addObjectHistoryLine(eObject, event, eventNumber, null);
	}

	@SuppressWarnings("unchecked")
	public void addObjectHistoryLine(EObject eObject, ChangeEvent<?> event, int eventNumber, Object value) {
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
			this.handleEObjectDeletion(eObject, event, eventNumber);
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

	private void handleAttributeSetUnsetEvents(EObject eObject, ChangeEvent<?> event, int eventNumber, Object value) {
		EAttribute eAttribute = ((EAttributeEvent) event).getEAttribute();
		if (objectHistoryMap.get(eObject) != null) {
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
				int setAttributeLastLine = -1;
				int unsetAttributeLastLine = -1;

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
					if (setAttributeLines != null) {
						this.addLinesToIgnoreList(setAttributeLines);
						ignoredSetAttCount += setAttributeLines.size();
						setAttributeLines.clear();
						eventLinesMap.remove(setAttributeName);
					}
					if (unsetAttributeLines != null) {
						this.addLinesToIgnoreList(unsetAttributeLines);
						ignoredUnsetAttCount += unsetAttributeLines.size();
						unsetAttributeLines.clear();
						eventLinesMap.remove(unsetAttributeName);
					}
				} else if (setAttributeLastLine > unsetAttributeLastLine) {
					if (setAttributeLines != null && setAttributeLines.size() > 1) {
						this.addLinesToIgnoreList(setAttributeLines.subList(0, setAttributeLines.size() - 1));
						ignoredSetAttCount += setAttributeLines.subList(0, setAttributeLines.size() - 1).size();
						setAttributeLines.removeAll(setAttributeLines.subList(0, setAttributeLines.size() - 1));
					}
					if (unsetAttributeLines != null) {
						this.addLinesToIgnoreList(unsetAttributeLines);
						ignoredUnsetAttCount += unsetAttributeLines.size();
						unsetAttributeLines.clear();
						eventLinesMap.remove(unsetAttributeName);
					}
				}
			}
		}
	}

	private void handleAttributeAddRemoveMoveEvents(EObject eObject, ChangeEvent<?> event, int eventNumber,
			Object value) {
		EAttribute eAttribute = ((EAttributeEvent) event).getEAttribute();
		if (objectHistoryMap.get(eObject) != null) {
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
					if (addAttributeLines != null) {
						this.addLinesToIgnoreList(addAttributeLines);
						ignoredAddAttCount += addAttributeLines.size();
						addAttributeLines.clear();
						eventLinesMap.remove(addAttributeName);
					}
					if (removeAttributeLines != null) {
						this.addLinesToIgnoreList(removeAttributeLines);
						ignoredRemoveAttCount += removeAttributeLines.size();
						removeAttributeLines.clear();
						eventLinesMap.remove(removeAttributeName);
					}
					if (moveWithinAttributeLines != null) {
						this.addLinesToIgnoreList(moveWithinAttributeLines);
						ignoredMoveAttCount += moveWithinAttributeLines.size();
						moveWithinAttributeLines.clear();
						eventLinesMap.remove(moveAttributeName);
					}
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
						ignoredAddAttCount += temp.size();
						addAttributeLines.removeAll(temp);
					}
					if (removeAttributeLines != null) {
						this.addLinesToIgnoreList(removeAttributeLines);
						ignoredRemoveAttCount += removeAttributeLines.size();
						removeAttributeLines.clear();
						eventLinesMap.remove(removeAttributeName);
					}
					if (moveWithinAttributeLines != null) {
						this.addLinesToIgnoreList(moveWithinAttributeLines);
						ignoredMoveAttCount += moveWithinAttributeLines.size();
						moveWithinAttributeLines.clear();
						eventLinesMap.remove(moveAttributeName);
					}
				}
				if (delta != 0 && delta != 1) {
					if (eAttributeHistory.isMoved() == false) {
						int addAttributeLastLine = -1;
						int removeAttributeLastLine = -1;
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
							if (valueAddAttributeLines != null) {
								this.addLinesToIgnoreList(
										valueAddAttributeLines.subList(0, valueAddAttributeLines.size() - 1));
								ignoredAddAttCount += valueAddAttributeLines
										.subList(0, valueAddAttributeLines.size() - 1).size();
								eventLinesMap.get(addAttributeName).removeAll(
										valueAddAttributeLines.subList(0, valueAddAttributeLines.size() - 1));
							}
							if (valueRemoveAttributeLines != null) {
								this.addLinesToIgnoreList(valueRemoveAttributeLines);
								ignoredRemoveAttCount += valueRemoveAttributeLines.size();
								// this cannot be removed
								// eventLinesMap.get(removeAttributeName).removeAll(valueRemoveAttributeLines);
							}
						} else if (addAttributeLastLine < removeAttributeLastLine) {
							if (valueAddAttributeLines != null) {
								this.addLinesToIgnoreList(valueAddAttributeLines);
								ignoredAddAttCount += valueAddAttributeLines.size();
								eventLinesMap.get(addAttributeName).removeAll(valueAddAttributeLines);
							}
							if (valueRemoveAttributeLines != null) {
								this.addLinesToIgnoreList(valueRemoveAttributeLines);
								ignoredRemoveAttCount += valueRemoveAttributeLines.size();
								eventLinesMap.get(removeAttributeName).removeAll(valueRemoveAttributeLines);
							}
						}
					}
				}
			}
		}
	}

	private void handleReferenceSetUnsetEvents(EObject eObject, ChangeEvent<?> event, int eventNumber, EObject value) {
		EReference eReferenceTarget = ((EReferenceEvent) event).getEReference();
		if (objectHistoryMap.get(eObject) != null) {
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
				String setReferenceName = SetEReferenceEvent.class.getSimpleName();
				String unsetReferenceName = UnsetEReferenceEvent.class.getSimpleName();
				int setReferenceLastLine = -1;
				int unsetReferenceLastLine = -1;

				EventHistory setReferenceLines = null;
				EventHistory unsetReferenceLines = null;

				if (eventLinesMap.containsKey(setReferenceName)) {
					setReferenceLines = eventLinesMap.get(setReferenceName);
					setReferenceLastLine = setReferenceLines.get(setReferenceLines.size() - 1).getEventNumber();
				}
				if (eventLinesMap.containsKey(unsetReferenceName)) {
					unsetReferenceLines = eventLinesMap.get(unsetReferenceName);
					unsetReferenceLastLine = unsetReferenceLines.get(unsetReferenceLines.size() - 1).getEventNumber();
				}

				if (unsetReferenceLastLine > setReferenceLastLine) {
					if (setReferenceLines != null) {
						this.addLinesToIgnoreList(setReferenceLines);
						ignoredAddRefCount += setReferenceLines.size();
						setReferenceLines.clear();
						eventLinesMap.remove(setReferenceName);
					}
					if (unsetReferenceLines != null) {
						this.addLinesToIgnoreList(unsetReferenceLines);
						ignoredUnsetRefCount += unsetReferenceLines.size();
						unsetReferenceLines.clear();
						eventLinesMap.remove(unsetReferenceName);
					}
				} else if (setReferenceLastLine > unsetReferenceLastLine) {
					if (setReferenceLines != null && setReferenceLines.size() > 1) {
						this.addLinesToIgnoreList(setReferenceLines.subList(0, setReferenceLines.size() - 1));
						ignoredSetRefCount += setReferenceLines.subList(0, setReferenceLines.size() - 1).size();
						setReferenceLines.removeAll(setReferenceLines.subList(0, setReferenceLines.size() - 1));
					}
					if (unsetReferenceLines != null) {
						this.addLinesToIgnoreList(unsetReferenceLines);
						ignoredUnsetRefCount += unsetReferenceLines.size();
						unsetReferenceLines.clear();
						eventLinesMap.remove(unsetReferenceName);
					}
				}

				// also add history to the object value
				if (value != null) {
					if (objectHistoryMap.containsKey(value) == false) {
						ObjectHistory objectHistory = new ObjectHistory(value);
						objectHistoryMap.put(value, objectHistory);
					}
					ObjectHistory objectHistory = objectHistoryMap.get(value);
					String eventName = event.getClass().getSimpleName();
					if (objectHistory.getEventHistoryMap().containsKey(eventName) == false) {
						EventHistory eventHistory = new EventHistory(event);
						objectHistory.getEventHistoryMap().put(eventName, eventHistory);
					}
					EventHistory eventHistory = objectHistory.getEventHistoryMap().get(eventName);
					eventHistory.add(new Line(eventNumber));

				}
			}
		}
	}

	private void handleReferenceAddRemoveMoveEvents(EObject eObject, ChangeEvent<?> event, int eventNumber,
			EObject value) {
		EReference eReferenceTarget = ((EReferenceEvent) event).getEReference();
		if (objectHistoryMap.get(eObject) != null) {
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

				// flag target object and all its members to moved
				if (event instanceof MoveWithinEReferenceEvent) {
					setTargetAndChildrenToIsMoved(eReferenceHistory);
				}

				if (event instanceof AddToEReferenceEvent) {
					EObject target = ((AddToEReferenceEvent) event).getTarget();
					EReference reference = ((AddToEReferenceEvent) event).getEReference();
					EList<EObject> values = (EList<EObject>) target.eGet(reference);
					// if (event.getPosition() < values.size()) {
					setTargetAndChildrenToIsMoved(eReferenceHistory);
					// }
					if (valueHistory != null && valueHistory.getPreviousTargetObjects() != null) {
						valueHistory.getPreviousTargetObjects().add(values);
					}
				}

				if (event instanceof RemoveFromEReferenceEvent) {
					EObject target = ((RemoveFromEReferenceEvent) event).getTarget();
					EReference reference = ((RemoveFromEReferenceEvent) event).getEReference();
					EList<EObject> values = (EList<EObject>) target.eGet(reference);
					// if (event.getPosition() < values.size() - 1) {
					setTargetAndChildrenToIsMoved(eReferenceHistory);
					// }

					// add a reference previous container to check if previous
					// it does not contain other objects then it is okay to set
					// the object's isMoved to false, so later the object can be
					// removed/deleted
					if (valueHistory != null && valueHistory.getPreviousTargetObjects() != null) {
						valueHistory.getPreviousTargetObjects().add(values);
					}
				}

				Map<String, EventHistory> targetEventLinesMap = referenceList.get(eReferenceTarget)
						.getEventHistoryMap();
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
						ObjectHistory temp = objectHistoryMap.get(item);
						if (temp != null) {
							if (temp.getPreviousTargetObjects().size() == 0) {
								temp.setMoved(false);
							} else {
								boolean okayToBeDeleted = true;
								for (List<EObject> list : temp.getPreviousTargetObjects()) {
									if (list.size() > 1) {
										okayToBeDeleted = false;
										break;
									}
								}
								if (okayToBeDeleted)
									temp.setMoved(false);
							}
						}
					}

					if (addReferenceLines != null) {
						this.addLinesToIgnoreList(addReferenceLines);
						ignoredAddRefCount += addReferenceLines.size();
						addReferenceLines.clear();
						targetEventLinesMap.remove(addReferenceName);
					}
					if (removeReferenceLines != null) {
						this.addLinesToIgnoreList(removeReferenceLines);
						ignoredRemoveRefCount += removeReferenceLines.size();
						removeReferenceLines.clear();
						targetEventLinesMap.remove(removeReferenceName);
					}
					if (moveWithinReferenceLines != null) {
						this.addLinesToIgnoreList(moveWithinReferenceLines);
						ignoredMoveRefCount += moveWithinReferenceLines.size();
						targetEventLinesMap.remove(moveWithinReferenceName);
					}

				}

				else if (delta != 0) {
					if (valueHistory != null && valueHistory.isMoved() == false) {
						int addReferenceLastLine = -1;
						int removeReferenceLastLine = -1;
						int addResourceLastLine = -1;
						int removeResourceLastLine = -1;

						Map<String, EventHistory> valueEventLinesMap = objectHistoryMap.get(value).getEventHistoryMap();

						String addResourceName = AddToResourceEvent.class.getSimpleName();
						String removeResourceName = RemoveFromResourceEvent.class.getSimpleName();

						EventHistory addResourceLines = null;
						EventHistory removeResourceLines = null;
						moveWithinReferenceLines = null;
						addReferenceLines = null;
						removeReferenceLines = null;

						if (valueEventLinesMap.containsKey(addReferenceName)) {
							addReferenceLines = valueEventLinesMap.get(addReferenceName);
							addReferenceLastLine = addReferenceLines.get(addReferenceLines.size() - 1).getEventNumber();
						}
						if (valueEventLinesMap.containsKey(removeReferenceName)) {
							removeReferenceLines = valueEventLinesMap.get(removeReferenceName);
							if (removeReferenceLines != null && removeReferenceLines.size() > 0)
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
							if (addReferenceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(addReferenceLines);
								ignoredAddRefCount += addReferenceLines.size();
								addReferenceLines.clear();
								valueEventLinesMap.remove(addReferenceName);
							}
							if (removeReferenceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(removeReferenceLines);
								// this cannot be deleted can cause error
								// removeReferenceLines.clear();
								// valueEventLinesMap.remove(removeReferenceLines);
							}
							if (addResourceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(addResourceLines);
								ignoredAddResCount += addResourceLines.size();
								addResourceLines.clear();
								valueEventLinesMap.remove(addResourceName);
							}
							if (removeResourceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(removeResourceLines);
								ignoredRemoveResCount += removeResourceLines.size();
								removeResourceLines.clear();
								valueEventLinesMap.remove(removeResourceName);
							}
							if (moveWithinReferenceLines != null) {
								this.addLinesToIgnoreList(moveWithinReferenceLines);
								ignoredRemoveRefCount += moveWithinReferenceLines.size();
								moveWithinReferenceLines.clear();
								valueEventLinesMap.remove(moveWithinReferenceName);
							}
						} else if (addReferenceLastLine > removeReferenceLastLine) {
							if (addReferenceLines != null && addReferenceLines.size() > 1
									&& eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(addReferenceLines.subList(0, addReferenceLines.size() - 1));
								ignoredAddRefCount += addReferenceLines.subList(0, addReferenceLines.size() - 1).size();
								addReferenceLines.removeAll(addReferenceLines.subList(0, addReferenceLines.size() - 1));
							}
							if (removeReferenceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(removeReferenceLines);
								ignoredRemoveRefCount += removeReferenceLines.size();
								removeReferenceLines.clear();
								valueEventLinesMap.remove(removeReferenceLines);
							}
							if (addResourceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(addResourceLines);
								ignoredAddResCount += addResourceLines.size();
								addResourceLines.clear();
								valueEventLinesMap.remove(addResourceName);
							}
							if (removeResourceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(removeResourceLines);
								ignoredRemoveResCount += removeResourceLines.size();
								removeResourceLines.clear();
								valueEventLinesMap.remove(removeResourceName);
							}
						} else if (removeResourceLastLine > addResourceLastLine) {
							if (addResourceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(addResourceLines);
								ignoredAddResCount += addResourceLines.size();
								addResourceLines.clear();
								valueEventLinesMap.remove(addResourceName);
							}
							if (removeResourceLines != null && eReferenceTarget.isContainment() == true) {
								this.addLinesToIgnoreList(removeResourceLines);
								ignoredRemoveResCount += removeResourceLines.size();
								removeResourceLines.clear();
								valueEventLinesMap.remove(removeResourceName);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param eReferenceHistory
	 */
	private void setTargetAndChildrenToIsMoved(ObjectHistory eReferenceHistory) {
		eReferenceHistory.setMoved(true);
		EventHistory lines = eReferenceHistory.getEventHistoryMap().get(AddToEReferenceEvent.class.getSimpleName());
		Set<EObject> eObjectList = new HashSet<>();
		for (Line line : lines) {
			eObjectList.add((EObject) line.getValue());
		}
		for (EObject item : eObjectList) {
			ObjectHistory temp = objectHistoryMap.get(item);
			if (temp != null) {
				objectHistoryMap.get(item).setMoved(true);
			}
		}
	}

	private void handleEObjectDeletion(EObject eObject, ChangeEvent<?> event, int eventNumber) {

		ObjectHistory eObjectHistory = objectHistoryMap.get(eObject);
		if (eObjectHistory != null) {
			eObjectHistory.addEventLine(event, eventNumber);

			if (eObjectHistory.isMoved() == false) {
				ObjectHistory deletedEObjectHistory = objectHistoryMap.get(eObject);

				// get all current object's references' lines
				Map<EObject, ReferenceHistory> references = deletedEObjectHistory.getReferences();
				for (Entry<EObject, ReferenceHistory> referenceEntry : references.entrySet()) {
					ReferenceHistory referenceHistory = referenceEntry.getValue();
					for (Entry<String, EventHistory> entry : referenceHistory.getEventHistoryMap().entrySet()) {
						EventHistory lines = entry.getValue();
						this.addLinesToIgnoreList(lines);

						if (entry.getKey().equals(SetEReferenceEvent.class.getSimpleName())) {
							ignoredSetRefCount += entry.getValue().size();
						} else if (entry.getKey().equals(UnsetEReferenceEvent.class.getSimpleName())) {
							ignoredUnsetRefCount += entry.getValue().size();
						} else if (entry.getKey().equals(AddToEReferenceEvent.class.getSimpleName())) {
							ignoredAddRefCount += entry.getValue().size();
						} else if (entry.getKey().equals(RemoveFromEReferenceEvent.class.getSimpleName())) {
							ignoredRemoveRefCount += entry.getValue().size();
						} else if (entry.getKey().equals(MoveWithinEReferenceEvent.class.getSimpleName())) {
							ignoredMoveRefCount += entry.getValue().size();
						}

						entry.getValue().clear();
					}
					referenceHistory.getEventHistoryMap().clear();
				}
				references.clear();

				// get all current object's attributes' lines
				Map<EObject, AttributeHistory> attributes = deletedEObjectHistory.getAttributes();
				for (Entry<EObject, AttributeHistory> attributeEntry : attributes.entrySet()) {
					AttributeHistory attributeHistory = attributeEntry.getValue();
					for (Entry<String, EventHistory> entry : attributeHistory.getEventHistoryMap().entrySet()) {
						EventHistory lines = entry.getValue();
						this.addLinesToIgnoreList(lines);

						if (entry.getKey().equals(SetEAttributeEvent.class.getSimpleName())) {
							ignoredSetAttCount += entry.getValue().size();
						} else if (entry.getKey().equals(UnsetEAttributeEvent.class.getSimpleName())) {
							ignoredUnsetAttCount += entry.getValue().size();
						} else if (entry.getKey().equals(AddToEAttributeEvent.class.getSimpleName())) {
							ignoredAddAttCount += entry.getValue().size();
						} else if (entry.getKey().equals(RemoveFromEAttributeEvent.class.getSimpleName())) {
							ignoredRemoveAttCount += entry.getValue().size();
						} else if (entry.getKey().equals(MoveWithinEAttributeEvent.class.getSimpleName())) {
							ignoredMoveAttCount += entry.getValue().size();
						}

						entry.getValue().clear();
					}
					attributeHistory.getEventHistoryMap().clear();
				}
				attributes.clear();

				// get all current object's lines
				for (Entry<String, EventHistory> entry : deletedEObjectHistory.getEventHistoryMap().entrySet()) {
					String key = entry.getKey();
					if (key.equals(RemoveFromResourceEvent.class.getSimpleName())
							|| key.equals(AddToResourceEvent.class.getSimpleName())
							|| key.equals(AddToEReferenceEvent.class.getSimpleName())
							|| key.equals(RemoveFromEReferenceEvent.class.getSimpleName())
							|| key.equals(MoveWithinEReferenceEvent.class.getSimpleName())
							|| key.equals(DeleteEObjectEvent.class.getSimpleName())
							|| key.equals(CreateEObjectEvent.class.getSimpleName())
							|| key.equals(SetEReferenceEvent.class.getSimpleName())
							|| key.equals(UnsetEReferenceEvent.class.getSimpleName())) {
						EventHistory lines = entry.getValue();

						if (entry.getKey().equals(DeleteEObjectEvent.class.getSimpleName())) {
							ignoredDeleteCount += entry.getValue().size();
						} else if (entry.getKey().equals(CreateEObjectEvent.class.getSimpleName())) {
							ignoredCreateCount += entry.getValue().size();
						} else if (entry.getKey().equals(RemoveFromResourceEvent.class.getSimpleName())) {
							ignoredRemoveResCount += entry.getValue().size();
						} else if (entry.getKey().equals(AddToResourceEvent.class.getSimpleName())) {
							ignoredAddResCount += entry.getValue().size();
						} else if (entry.getKey().equals(AddToEReferenceEvent.class.getSimpleName())) {
							ignoredAddRefCount += entry.getValue().size();
						} else if (entry.getKey().equals(RemoveFromEReferenceEvent.class.getSimpleName())) {
							ignoredRemoveRefCount += entry.getValue().size();
						} else if (entry.getKey().equals(MoveWithinEReferenceEvent.class.getSimpleName())) {
							ignoredMoveRefCount += entry.getValue().size();
						}

						this.addLinesToIgnoreList(lines);
					}
				}
				deletedEObjectHistory.getEventHistoryMap().clear();

				this.geteObjectHistoryMap().remove(eObject);
				ignoredDeleteCount += 1;
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

	public void clear() {
		// model events
		for (Entry<String, EventHistory> item : this.getEventHistoryMap().entrySet()) {
			item.getValue().clear();
		}
		this.getEventHistoryMap().clear();

		// ----attributes
		for (Entry<EObject, ObjectHistory> item : this.geteObjectHistoryMap().entrySet()) {
			for (Entry<EObject, AttributeHistory> attr : item.getValue().getAttributes().entrySet()) {
				for (Entry<String, EventHistory> events : attr.getValue().getEventHistoryMap().entrySet()) {
					events.getValue().clear();
				}
				attr.getValue().getEventHistoryMap().clear();
			}
			item.getValue().getAttributes().clear();

			// ----refs
			for (Entry<EObject, ReferenceHistory> refs : item.getValue().getReferences().entrySet()) {
				for (Entry<String, EventHistory> events : refs.getValue().getEventHistoryMap().entrySet()) {
					events.getValue().clear();
				}
				refs.getValue().getEventHistoryMap().clear();
			}
			item.getValue().getReferences().clear();

			// -----------events
			for (Entry<String, EventHistory> events : item.getValue().getEventHistoryMap().entrySet()) {
				events.getValue().clear();
			}
			item.getValue().getEventHistoryMap().clear();

		}
		this.geteObjectHistoryMap().clear();
	}

	public void importModelHistory(File filePath) throws FileNotFoundException, XMLStreamException {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLEventReader xmlReader = xmlInputFactory
				.createXMLEventReader(new BufferedInputStream(new FileInputStream(filePath)));

		String id = null;
		boolean isMoved = false;
		int lineNumber = -1;

		while (xmlReader.hasNext()) {
			XMLEvent xmlEvent = xmlReader.nextEvent();
			if (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT) {
				StartElement e = xmlEvent.asStartElement();
				String name = e.getName().getLocalPart();

				if (name.equals("EObject")) {
					id = e.getAttributeByName(new QName("id")).getValue();
					isMoved = e.getAttributeByName(new QName("id")).getValue().equals("1") ? true : false; 
				}
				
				if (name.equals("Attributes")) {
					
				}
				
				if (name.equals("References")) {
					
				}
				
				if(name.equals(CreateEObjectEvent.class.getName())) {
					
				}
				
				if(name.equals(SetEReferenceEvent.class.getName())) {
					
				
				}
				
				if (name.equals(Line.class.getName())) {
					lineNumber = Integer.valueOf(e.getAttributeByName(new QName("lineNumber")).getValue());
				}
			}

			if (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT) {
				EndElement ee = xmlEvent.asEndElement();
				String name = ee.getName().getLocalPart();

				if (name.equals("EObject")) {
					EObject eObject = resource.getEObject(id);
					resource.getModelHistory().geteObjectHistoryMap().put(eObject, new ObjectHistory(eObject));
				}
				
				if(name.equals(CreateEObjectEvent.class.getName())) {
					EventHistory event = new EventHistory(new CreateEObjectEvent(null));
					resource.getModelHistory().getEventHistoryMap().put(CreateEObjectEvent.class.getName(), event);
				}
				
				if(name.equals(SetEReferenceEvent.class.getName())) {
				
				}

			}
		}
	}

	public void exportModelHistory(File filePath) throws FileNotFoundException {
		PrintWriter printer = new PrintWriter(filePath);

		printer.println("<?xml version='1.0' encoding='ISO-8859-1' ?>");
		printer.println("<ModelHistory>");
		for (Entry<EObject, ObjectHistory> entry1 : this.geteObjectHistoryMap().entrySet()) {
			EObject eObject = entry1.getKey();
			ObjectHistory eObjectEventLineHistory = entry1.getValue();
			String id = resource.getURIFragment(eObject);
			if (id.equals("/-1") == false) {
				printer.printf("  <EObject id='%s' isMoved='%s'>" + System.lineSeparator(), id,
						eObjectEventLineHistory.isMoved() ? 1 : 0);

				for (Entry<String, EventHistory> entry2 : eObjectEventLineHistory.getEventHistoryMap().entrySet()) {
					String eventName = entry2.getKey();
					List<Line> lines = entry2.getValue();
					printer.printf("    <%s>" + System.lineSeparator(), eventName);
					for (Line line : lines) {
						printer.printf("      <Line lineNumber='%s'/>" + System.lineSeparator(), line.getEventNumber());
					}
					printer.printf("    </%s>" + System.lineSeparator(), eventName);
				}
				Map<EObject, AttributeHistory> attributeList = eObjectEventLineHistory.getAttributes();

				if (attributeList.size() > 0) {
					printer.println("    <Attributes>");
					for (Entry<EObject, AttributeHistory> entry2 : attributeList.entrySet()) {
						EAttribute eAttribute = (EAttribute) entry2.getKey();
						ObjectHistory eAttributeHistory = entry2.getValue();
						printer.printf("      <Attribute name='%s' isMoved='%s'>" + System.lineSeparator(),
								eAttribute.getName(), eAttributeHistory.isMoved() ? 1 : 0);

						for (Entry<String, EventHistory> entry3 : eAttributeHistory.getEventHistoryMap().entrySet()) {
							String eventName = entry3.getKey();
							List<Line> lines = entry3.getValue();
							printer.printf("        <%s>" + System.lineSeparator(), eventName);
							for (Line line : lines) {
								printer.printf("          <Line lineNumber='%s'/>" + System.lineSeparator(),
										line.getEventNumber());
							}
							printer.printf("        </%s>" + System.lineSeparator(), eventName);
						}

						printer.println("      </Attribute>");
					}
					printer.println("    </Attributes>");
				}

				// references
				Map<EObject, ReferenceHistory> referenceList = eObjectEventLineHistory.getReferences();
				if (referenceList.size() > 0) {
					printer.println("    <References>");
					for (Entry<EObject, ReferenceHistory> entry2 : referenceList.entrySet()) {
						EReference eReference = (EReference) entry2.getKey();
						ObjectHistory eReferenceHistory = entry2.getValue();

						printer.printf("      <Reference name='%s' isMoved='%s'>" + System.lineSeparator(),
								eReference.getName(), eReferenceHistory.isMoved() ? 1 : 0);
						for (Entry<String, EventHistory> entry3 : eReferenceHistory.getEventHistoryMap().entrySet()) {
							String eventName = entry3.getKey();
							List<Line> lines = entry3.getValue();
							printer.printf("        <%s>" + System.lineSeparator(), eventName);
							for (Line line : lines) {
								if (resource.getEObjectId((EObject) line.getValue()) != null) {
									printer.printf(
											"          <Line lineNumber='%s' value='%s'/>" + System.lineSeparator(),
											line.getEventNumber(), resource.getEObjectId((EObject) line.getValue()));
								}
							}
							printer.printf("        </%s>" + System.lineSeparator(), eventName);
						}
						printer.println("      </Reference>");

					}
					printer.println("    </References>");
				}

				printer.println("  </EObject>");
			}
		}
		printer.println("</ModelHistory>");
		printer.close();
	}

	public void printStructure() {
		System.out.println("MODEL HISTORY");
		System.out.println(this.getEObject());
		for (Entry<EObject, ObjectHistory> entry1 : this.geteObjectHistoryMap().entrySet()) {
			EObject eObject = entry1.getKey();
			ObjectHistory eObjectEventLineHistory = entry1.getValue();
			System.out.println("EObject: " + resource.getURIFragment(eObject) + " -------------------");
			System.out.println("IsMoved = " + eObjectEventLineHistory.isMoved());
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
				System.out.println("        IsMoved = " + eAttributeHistory.isMoved());
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
				System.out.println("        IsMoved = " + eReferenceHistory.isMoved());
				for (Entry<String, EventHistory> entry3 : eReferenceHistory.getEventHistoryMap().entrySet()) {
					String eventName = entry3.getKey();
					List<Line> lines = entry3.getValue();
					System.out.println("            " + eventName + " = " + lines);
				}
			}
		}
	}
}