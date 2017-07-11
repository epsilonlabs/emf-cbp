package org.eclipse.epsilon.cbp.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.ChangeEvent;

/***
 * 
 * @author Alfa
 * This is a Class to capture lines in the change-based model XML where an operation involves an object.
 * It maps operations applied to an object to their respective lines in the change-based model XML.    
 */
public class EObjectEventLineHistory {
	 
	protected EObject eObject = null;
	protected EObjectEventLineHistory parent = null;
	protected Map<EObject, EObjectEventLineHistory> attributes = new HashMap<EObject, EObjectEventLineHistory>(); 
	protected Map<EObject, EObjectEventLineHistory> references = new HashMap<EObject, EObjectEventLineHistory>();
	protected Map<ChangeEvent<?>, List<Integer>> eventLinesMap = new HashMap<ChangeEvent<?>, List<Integer>>();

	public EObjectEventLineHistory(EObject eObject) {
		this.eObject = eObject;
	}
	
	public EObjectEventLineHistory(EObject eObject, EObjectEventLineHistory parent) {
		this.eObject = eObject;
		this.parent = parent;
	}

	public EObject geteObject() {
		return eObject;
	}

	public void seteObject(EObject eObject) {
		this.eObject = eObject;
	}

	public EObjectEventLineHistory getParent() {
		return parent;
	}

	public void setParent(EObjectEventLineHistory parent) {
		this.parent = parent;
	}

	public Map<EObject, EObjectEventLineHistory> getAttributes() {
		return attributes;
	}

	public Map<EObject, EObjectEventLineHistory> getReferences() {
		return references;
	}

	public Map<ChangeEvent<?>, List<Integer>> getEventLinesMap() {
		return eventLinesMap;
	}
	
	public void addEventLine(ChangeEvent<?> event, int line){
		if (!this.getEventLinesMap().containsKey(event)){
			List<Integer> lines = new ArrayList<Integer>(Arrays.asList(line));
			this.getEventLinesMap().put(event, lines);
		}else{
			this.getEventLinesMap().get(event).add(line);
		}
	}
}
