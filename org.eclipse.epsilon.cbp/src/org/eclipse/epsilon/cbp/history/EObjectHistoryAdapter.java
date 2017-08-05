package org.eclipse.epsilon.cbp.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
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

public class EObjectHistoryAdapter {

	protected Map<EObject, EObjectHistory> modelHistory = new HashMap<>();
	protected Set<Integer> ignoreList;

	private void addLinesToIgnoreList(List<Line> lines) {
		for (Line line : lines) {
			ignoreList.add(line.getLineNumber());
		}
	}

	public EObjectHistoryAdapter(Set<Integer> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public Map<EObject, EObjectHistory> geteObjectHistoryList() {
		return modelHistory;
	}

	public void add(EObject eObject, ChangeEvent<?> event, int line) {
		this.add(eObject, event, line, null);
	}

	@SuppressWarnings("unchecked")
	public void add(EObject eObject, ChangeEvent<?> event, int line, Object value) {
		// REGISTER AND CREATE
		if (event instanceof RegisterEPackageEvent || event instanceof CreateEObjectEvent) {
			if (!modelHistory.containsKey(eObject)) {
				EObjectHistory eObjectHistory = new EObjectHistory(eObject);
				eObjectHistory.addEventRecord(event, line);
				modelHistory.put(eObject, eObjectHistory);
			} else {
				modelHistory.get(eObject).addEventRecord(event, line);
			}
		}
		// RESOURCE
		else if (event instanceof AddToResourceEvent || event instanceof RemoveFromResourceEvent) {
			modelHistory.get(eObject).addEventRecord(event, line);

		}
		// OBJECT DELETION
		else if (event instanceof DeleteEObjectEvent) {
			this.handleEObjectDeletion(eObject, event, line);
		}
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
		Map<EObject, EObjectHistory> attributeList = modelHistory.get(eObject).getAttributes();
		if (!attributeList.containsKey(eAttribute)) {
			EObjectHistory eAttributeHistory = new EObjectHistory(eObject);
			eAttributeHistory.addEventRecord(event, lineNumber, value);
			attributeList.put(eAttribute, eAttributeHistory);
		} else {
			attributeList.get(eAttribute).addEventRecord(event, lineNumber, value);
		}

		// ignoring Set and Unset Attribute
		if (event instanceof SetEAttributeEvent || event instanceof UnsetEAttributeEvent) {
			Map<String, List<Line>> eventLinesMap = attributeList.get(eAttribute).getEventRecords();
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
			EObjectHistory eAttributeHistory = attributeList.get(eAttribute);
			if (event instanceof MoveWithinEAttributeEvent) {
				eAttributeHistory.setMoved(true);
			}
			Map<String, List<Line>> eventLinesMap = eAttributeHistory.getEventRecords();
			String addAttributeName = AddToEAttributeEvent.class.getSimpleName();
			String removeAttributeName = RemoveFromEAttributeEvent.class.getSimpleName();
			String moveAttributeName = MoveWithinEAttributeEvent.class.getSimpleName();

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
					List<Line> temp = new ArrayList<Line>();
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
					int addAttributeLastLine = -1;
					int removeAttributeLastLine = -1;
					List<Line> valueAddAttributeLines = new ArrayList<Line>();
					List<Line> valueRemoveAttributeLines = new ArrayList<>();

					if (eventLinesMap.containsKey(addAttributeName)) {
						for (Line line : eventLinesMap.get(addAttributeName)) {
							if (line.getValue().equals(value)) {
								valueAddAttributeLines.add(line);
							}
						}
						if (valueAddAttributeLines.size() > 0)
							addAttributeLastLine = valueAddAttributeLines.get(valueAddAttributeLines.size() - 1)
									.getLineNumber();
					}
					if (eventLinesMap.containsKey(removeAttributeName)) {
						for (Line line : eventLinesMap.get(removeAttributeName)) {
							if (line.getValue().equals(value)) {
								valueRemoveAttributeLines.add(line);
							}
						}
						if (valueRemoveAttributeLines.size() > 0)
							removeAttributeLastLine = valueRemoveAttributeLines
									.get(valueRemoveAttributeLines.size() - 1).getLineNumber();
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

	private void handleEReference(EObject eObject, ChangeEvent<?> event, int lineNumber, EObject value) {
		EReference eReferenceTarget = ((EReferenceEvent) event).getEReference();
		Map<EObject, EObjectHistory> referenceList = modelHistory.get(eObject).getReferences();
		if (!referenceList.containsKey(eReferenceTarget)) {
			EObjectHistory eReferenceHistory = new EObjectHistory(eObject);
			eReferenceHistory.addEventRecord(event, lineNumber, value);
			referenceList.put(eReferenceTarget, eReferenceHistory);
		} else {
			referenceList.get(eReferenceTarget).addEventRecord(event, lineNumber, value);
		}

		// ignoring Set and Unset Reference
		if (event instanceof SetEReferenceEvent || event instanceof UnsetEReferenceEvent) {
			Map<String, List<Line>> eventLinesMap = referenceList.get(eReferenceTarget).getEventRecords();
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
			if (!modelHistory.containsKey(value)) {
				EObjectHistory eReferenceObjectHistory = new EObjectHistory(value);
				eReferenceObjectHistory.addEventRecord(event, lineNumber);
				modelHistory.put(eReferenceTarget, eReferenceObjectHistory);
			} else {
				modelHistory.get(value).addEventRecord(event, lineNumber);
			}

			EObjectHistory eReferenceHistory = referenceList.get(eReferenceTarget);
			EObjectHistory valueHistory = modelHistory.get(value);
			if (event instanceof MoveWithinEReferenceEvent) {
				eReferenceHistory.setMoved(true);
				List<Line> lines = eReferenceHistory.getEventRecords().get(AddToEReferenceEvent.class.getSimpleName());
				Set<EObject> eObjectList = new HashSet<>();
				for (Line line : lines) {
					eObjectList.add((EObject) line.getValue());
				}
				for (EObject item : eObjectList) {
					modelHistory.get(item).setMoved(true);
				}
			}

			Map<String, List<Line>> targetEventLinesMap = referenceList.get(eReferenceTarget).getEventRecords();
			String addReferenceName = AddToEReferenceEvent.class.getSimpleName();
			String removeReferenceName = RemoveFromEReferenceEvent.class.getSimpleName();
			String moveWithinReferenceName = MoveWithinEReferenceEvent.class.getSimpleName();

			List<Line> addReferenceLines = null;
			List<Line> removeReferenceLines = null;
			List<Line> moveWithinReferenceLines = null;

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
				List<Line> lines = eReferenceHistory.getEventRecords().get(AddToEReferenceEvent.class.getSimpleName());
				Set<EObject> eObjectList = new HashSet<>();
				for (Line line : lines) {
					eObjectList.add((EObject) line.getValue());
				}
				for (EObject item : eObjectList) {
					modelHistory.get(item).setMoved(false);
				}

				if (addReferenceLines != null)
					this.addLinesToIgnoreList(addReferenceLines);
				if (removeReferenceLines != null)
					this.addLinesToIgnoreList(removeReferenceLines);
				if (moveWithinReferenceLines != null)
					this.addLinesToIgnoreList(moveWithinReferenceLines);

				// for (Line l : removeReferenceLines) {
				// this.handleEObjectDeletion((EObject) l.getValue(), event,
				// l.getLineNumber());
				// }
			} else if (delta == 1) {
				eReferenceHistory.setMoved(false);

				List<Line> lines = eReferenceHistory.getEventRecords().get(AddToEReferenceEvent.class.getSimpleName());
				Set<EObject> eObjectList = new HashSet<>();
				for (Line line : lines) {
					eObjectList.add((EObject) line.getValue());
				}
				for (EObject item : eObjectList) {
					modelHistory.get(item).setMoved(false);
				}

				if (addReferenceLines != null) {
					List<Line> temp = new ArrayList<Line>();
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

				// if (eReferenceTarget.isContainment()) {
				// for (Line l : removeReferenceLines) {
				// this.handleEObjectDeletion((EObject) l.getValue(), event,
				// l.getLineNumber());
				// }
				// }
			}
			// else if (delta != 0 && delta != 1) {
			// if (eReferenceHistory.isMoved() == false) {
			// // // METHOD 1
			// // // --------------------------------------------------
			// // int addReferenceLastLine = -1;
			// // int removeReferenceLastLine = -1;
			// // int addResourceLastLine = -1;
			// // int removeResourceLastLine = -1;
			// //
			// // List<Line> valueAddReferenceLines = new
			// // ArrayList<Line>();
			// // List<Line> valueRemoveReferenceLines = new ArrayList<>();
			// //
			// // if (targetEventLinesMap.containsKey(addReferenceName)) {
			// // for (Line line :
			// // targetEventLinesMap.get(addReferenceName)) {
			// // if (line.getValue().equals(value)) {
			// // valueAddReferenceLines.add(line);
			// // }
			// // }
			// // if (valueAddReferenceLines.size() > 0)
			// // addReferenceLastLine =
			// // valueAddReferenceLines.get(valueAddReferenceLines.size()
			// // - 1)
			// // .getLineNumber();
			// // }
			// // if (targetEventLinesMap.containsKey(removeReferenceName))
			// // {
			// // for (Line line :
			// // targetEventLinesMap.get(removeReferenceName)) {
			// // if (line.getValue().equals(value)) {
			// // valueRemoveReferenceLines.add(line);
			// // }
			// // }
			// // if (valueRemoveReferenceLines.size() > 0)
			// // removeReferenceLastLine = valueRemoveReferenceLines
			// // .get(valueRemoveReferenceLines.size() -
			// // 1).getLineNumber();
			// // }
			// //
			// // if (addReferenceLastLine > removeReferenceLastLine) {
			// // if (valueAddReferenceLines != null)
			// // this.addLinesToIgnoreList(
			// // valueAddReferenceLines.subList(0,
			// // valueAddReferenceLines.size() - 1));
			// // if (valueRemoveReferenceLines != null)
			// // this.addLinesToIgnoreList(valueRemoveReferenceLines);
			// // } else if (addReferenceLastLine <
			// // removeReferenceLastLine) {
			// // if (valueAddReferenceLines != null)
			// // this.addLinesToIgnoreList(valueAddReferenceLines);
			// // if (valueRemoveReferenceLines != null)
			// // this.addLinesToIgnoreList(valueRemoveReferenceLines);
			// // }
			// //
			// // // ---------------------------------------------------
			// }
			// }

			// VALUE OBJECT
			// METHOD 2 ---------------------------------------------
			else if (delta != 0 && delta != 1) {
				if (valueHistory.isMoved() == false) {
					int addReferenceLastLine = -1;
					int removeReferenceLastLine = -1;
					int addResourceLastLine = -1;
					int removeResourceLastLine = -1;

					Map<String, List<Line>> valueEventLinesMap = modelHistory.get(value).getEventRecords();

					String addResourceName = AddToResourceEvent.class.getSimpleName();
					String removeResourceName = RemoveFromResourceEvent.class.getSimpleName();

					List<Line> addResourceLines = null;
					List<Line> removeResourceLines = null;

					if (valueEventLinesMap.containsKey(addReferenceName)) {
						addReferenceLines = valueEventLinesMap.get(addReferenceName);
						addReferenceLastLine = addReferenceLines.get(addReferenceLines.size() - 1).getLineNumber();
					}
					if (valueEventLinesMap.containsKey(removeReferenceName)) {
						removeReferenceLines = valueEventLinesMap.get(removeReferenceName);
						removeReferenceLastLine = removeReferenceLines.get(removeReferenceLines.size() - 1)
								.getLineNumber();
					}
					if (valueEventLinesMap.containsKey(addResourceName)) {
						addResourceLines = valueEventLinesMap.get(addResourceName);
						addResourceLastLine = addResourceLines.get(addResourceLines.size() - 1).getLineNumber();
					}
					if (valueEventLinesMap.containsKey(removeResourceName)) {
						removeResourceLines = valueEventLinesMap.get(removeResourceName);
						removeResourceLastLine = removeResourceLines.get(removeResourceLines.size() - 1)
								.getLineNumber();
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

	private void handleEObjectDeletion(EObject eObject, ChangeEvent<?> event, int line) {
		
//		String name = (String) eObject.eGet(eObject.eClass().getEStructuralFeature("name"));
//		if (name.equals("e94")){
//			System.out.println(name);
//		}
		EObjectHistory eObjectHistory = modelHistory.get(eObject);
		eObjectHistory.addEventRecord(event, line);
		if (eObjectHistory.isMoved() == false) {
			EObjectHistory deletedEObjectHistory = modelHistory.get(eObject);

			// get all current object's references' lines
			Map<EObject, EObjectHistory> references = deletedEObjectHistory.getReferences();
			for (Entry<EObject, EObjectHistory> referenceEntry : references.entrySet()) {
				EObjectHistory referenceHistory = referenceEntry.getValue();

				for (Entry<String, List<Line>> eventLines : referenceHistory.getEventRecords().entrySet()) {
					List<Line> lines = eventLines.getValue();

					// if (((EReference)
					// referenceEntry.getKey()).isContainment()) {
					// for (Line row : lines) {
					// this.handleEObjectDeletion((EObject) row.getValue(),
					// event, row.getLineNumber());
					// }
					// }

					this.addLinesToIgnoreList(lines);
				}
			}

			// get all current object's attributes' lines
			Map<EObject, EObjectHistory> attributes = deletedEObjectHistory.getAttributes();
			for (Entry<EObject, EObjectHistory> attributeEntry : attributes.entrySet()) {
				EObjectHistory attributeHistory = attributeEntry.getValue();
				for (Entry<String, List<Line>> eventLines : attributeHistory.getEventRecords().entrySet()) {
					List<Line> lines = eventLines.getValue();
					this.addLinesToIgnoreList(lines);
				}
			}

			// get all current object's lines
			for (Entry<String, List<Line>> eventLines : deletedEObjectHistory.getEventRecords().entrySet()) {
				String key = eventLines.getKey();
				if (key.equals(RemoveFromResourceEvent.class.getSimpleName())
						|| key.equals(AddToResourceEvent.class.getSimpleName())
						|| key.equals(AddToEReferenceEvent.class.getSimpleName())
						|| key.equals(RemoveFromEReferenceEvent.class.getSimpleName())
						|| key.equals(DeleteEObjectEvent.class.getSimpleName())
						|| key.equals(CreateEObjectEvent.class.getSimpleName())) {
					List<Line> lines = eventLines.getValue();
					this.addLinesToIgnoreList(lines);
				}
			}
		}
	}
}
