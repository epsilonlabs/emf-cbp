/**
 */
package org.eclipse.epsilon.cbp.test.conference.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.epsilon.cbp.test.conference.Conference;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.conference.Day;
import org.eclipse.epsilon.cbp.test.conference.Person;
import org.eclipse.epsilon.cbp.test.conference.Room;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Conference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.ConferenceImpl#getParticipants <em>Participants</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.ConferenceImpl#getRooms <em>Rooms</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.ConferenceImpl#getDays <em>Days</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConferenceImpl extends NodeImpl implements Conference {
	/**
	 * The cached value of the '{@link #getParticipants() <em>Participants</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParticipants()
	 * @generated
	 * @ordered
	 */
	protected EList<Person> participants;

	/**
	 * The cached value of the '{@link #getRooms() <em>Rooms</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRooms()
	 * @generated
	 * @ordered
	 */
	protected EList<Room> rooms;

	/**
	 * The cached value of the '{@link #getDays() <em>Days</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDays()
	 * @generated
	 * @ordered
	 */
	protected EList<Day> days;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConferenceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ConferencePackage.Literals.CONFERENCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Person> getParticipants() {
		if (participants == null) {
			participants = new EObjectContainmentEList<Person>(Person.class, this, ConferencePackage.CONFERENCE__PARTICIPANTS);
		}
		return participants;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Room> getRooms() {
		if (rooms == null) {
			rooms = new EObjectContainmentEList<Room>(Room.class, this, ConferencePackage.CONFERENCE__ROOMS);
		}
		return rooms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Day> getDays() {
		if (days == null) {
			days = new EObjectContainmentEList<Day>(Day.class, this, ConferencePackage.CONFERENCE__DAYS);
		}
		return days;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ConferencePackage.CONFERENCE__PARTICIPANTS:
				return ((InternalEList<?>)getParticipants()).basicRemove(otherEnd, msgs);
			case ConferencePackage.CONFERENCE__ROOMS:
				return ((InternalEList<?>)getRooms()).basicRemove(otherEnd, msgs);
			case ConferencePackage.CONFERENCE__DAYS:
				return ((InternalEList<?>)getDays()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ConferencePackage.CONFERENCE__PARTICIPANTS:
				return getParticipants();
			case ConferencePackage.CONFERENCE__ROOMS:
				return getRooms();
			case ConferencePackage.CONFERENCE__DAYS:
				return getDays();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ConferencePackage.CONFERENCE__PARTICIPANTS:
				getParticipants().clear();
				getParticipants().addAll((Collection<? extends Person>)newValue);
				return;
			case ConferencePackage.CONFERENCE__ROOMS:
				getRooms().clear();
				getRooms().addAll((Collection<? extends Room>)newValue);
				return;
			case ConferencePackage.CONFERENCE__DAYS:
				getDays().clear();
				getDays().addAll((Collection<? extends Day>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ConferencePackage.CONFERENCE__PARTICIPANTS:
				getParticipants().clear();
				return;
			case ConferencePackage.CONFERENCE__ROOMS:
				getRooms().clear();
				return;
			case ConferencePackage.CONFERENCE__DAYS:
				getDays().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ConferencePackage.CONFERENCE__PARTICIPANTS:
				return participants != null && !participants.isEmpty();
			case ConferencePackage.CONFERENCE__ROOMS:
				return rooms != null && !rooms.isEmpty();
			case ConferencePackage.CONFERENCE__DAYS:
				return days != null && !days.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ConferenceImpl
