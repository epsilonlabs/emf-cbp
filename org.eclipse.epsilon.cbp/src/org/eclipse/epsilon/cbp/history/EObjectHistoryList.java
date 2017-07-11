package org.eclipse.epsilon.cbp.history;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;

/***
 * 
 * @author Alfa This is a Class to capture lines in the change-based model XML
 *         where an operation involves an object. It maps operations applied to
 *         an object to their respective lines in the change-based model XML.
 */
public class EObjectHistoryList {

	protected Map<EObject, EObjectEventLineHistory> eObjectHistoryList = new HashMap<>();

	
	public Map<EObject, EObjectEventLineHistory> geteObjectHistoryList() {
		return eObjectHistoryList;
	}


	public void add(EObject eObject, ChangeEvent<?> event, int line) {
		if (event instanceof RegisterEPackageEvent || event instanceof CreateEObjectEvent) {
			if (!eObjectHistoryList.containsKey(eObject)) {
				EObjectEventLineHistory eObjectHistory = new EObjectEventLineHistory(eObject);
				eObjectHistory.addEventLine(event, line);
				eObjectHistoryList.put(eObject, eObjectHistory);
			} else {
				eObjectHistoryList.get(eObject).addEventLine(event, line);
			}
		}else if (event instanceof AddToResourceEvent){
			eObjectHistoryList.get(eObject).addEventLine(event, line);
		}
		else if (event instanceof RemoveFromResourceEvent){
			eObjectHistoryList.get(eObject).addEventLine(event, line);
		}
	}
}
