package org.eclipse.epsilon.cbp.history;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.ChangeEvent;

/***
 * 
 * @author Alfa
 * This is a Class to capture lines in the change-based model XML where an operation involves an object.
 * It maps operations applied to an object to their respective lines in the change-based model XML.    
 */
public class EObjectEventLines {
	 
	protected EObject eObject = null;
	protected Map<EObject, EObjectEventLines> attributes = new HashMap<EObject, EObjectEventLines>(); 
	protected Map<EObject, EObjectEventLines> references = new HashMap<EObject, EObjectEventLines>();
	protected Map<String, List<Integer>> eventLinesMap = new HashMap<String, List<Integer>>();

	
	
	public EObjectEventLines(EObject eObject) {
		this.eObject = eObject;
	}
	
	public EObject geteObject() {
		return eObject;
	}
	
	public Map<EObject, EObjectEventLines> getAttributes() {
		return attributes;
	}

	public Map<EObject, EObjectEventLines> getReferences() {
		return references;
	}

	public Map<String, List<Integer>> getEventLinesMap() {
		return eventLinesMap;
	}
	
	public void addEventLine(ChangeEvent<?> event, int line){
		String eventName = event.getClass().getSimpleName();
		if (!this.getEventLinesMap().containsKey(eventName)){
			List<Integer> lines = new ArrayList<Integer>(Arrays.asList(line));
			this.getEventLinesMap().put(eventName, lines);
		}else{
			this.getEventLinesMap().get(eventName).add(line);
		}
	}
}
