package org.eclipse.epsilon.cbp.hybrid;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class NeoChangeEventAdapter2 extends EContentAdapter {

	@Override
	public void notifyChanged(Notification n) {
//		if (n.getEventType() == Notification.ADD) {
//			System.out.println("ADD");
//		} else if (n.getEventType() == Notification.ADD_MANY) {
//			System.out.println("ADD MANY");
//		} else if (n.getEventType() == Notification.CREATE) {
//			System.out.println("CREATE");
//		} else if (n.getEventType() == Notification.EVENT_TYPE_COUNT) {
//			System.out.println("EVENT TYPE COUNT");
//		} else if (n.getEventType() == Notification.MOVE) {
//			System.out.println("MOVE");
//		} else if (n.getEventType() == Notification.NO_FEATURE_ID) {
//			System.out.println("NO FEATURE ID");
//		} else if (n.getEventType() == Notification.NO_INDEX) {
//			System.out.println("NO INDEX");
//		} else if (n.getEventType() == Notification.REMOVE) {
//			System.out.println("REMOVE");
//		} else if (n.getEventType() == Notification.REMOVE_MANY) {
//			System.out.println("REMOVE MANY");
//		} else if (n.getEventType() == Notification.REMOVING_ADAPTER) {
//			System.out.println("REMOVING ADAPTER");
//		} else if (n.getEventType() == Notification.RESOLVE) {
//			System.out.println("RESOLVE");
//		} else if (n.getEventType() == Notification.SET) {
//			System.out.println("SET");
//		} else if (n.getEventType() == Notification.UNSET) {
//			System.out.println("UNSET");
//		}
		System.out.println(n.getNotifier());
//		System.out.println(n.getFeature());
//		System.out.println(n.getOldValue());
//		System.out.println(n.getNewValue());
//		System.out.println();
		
		super.notifyChanged(n);
	}
}
