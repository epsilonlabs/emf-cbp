/**
 */
package org.eclipse.epsilon.cbp.test.conference.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.epsilon.cbp.test.conference.ConferencePackage;
import org.eclipse.epsilon.cbp.test.conference.Person;
import org.eclipse.epsilon.cbp.test.conference.Talk;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Talk</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.TalkImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.TalkImpl#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.TalkImpl#getSpeaker <em>Speaker</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.impl.TalkImpl#getDiscussant <em>Discussant</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TalkImpl extends NodeImpl implements Talk {
	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final int DURATION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected int duration = DURATION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSpeaker() <em>Speaker</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpeaker()
	 * @generated
	 * @ordered
	 */
	protected Person speaker;

	/**
	 * The cached value of the '{@link #getDiscussant() <em>Discussant</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDiscussant()
	 * @generated
	 * @ordered
	 */
	protected Person discussant;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TalkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ConferencePackage.Literals.TALK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ConferencePackage.TALK__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDuration(int newDuration) {
		int oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ConferencePackage.TALK__DURATION, oldDuration, duration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person getSpeaker() {
		if (speaker != null && speaker.eIsProxy()) {
			InternalEObject oldSpeaker = (InternalEObject)speaker;
			speaker = (Person)eResolveProxy(oldSpeaker);
			if (speaker != oldSpeaker) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ConferencePackage.TALK__SPEAKER, oldSpeaker, speaker));
			}
		}
		return speaker;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person basicGetSpeaker() {
		return speaker;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSpeaker(Person newSpeaker) {
		Person oldSpeaker = speaker;
		speaker = newSpeaker;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ConferencePackage.TALK__SPEAKER, oldSpeaker, speaker));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person getDiscussant() {
		if (discussant != null && discussant.eIsProxy()) {
			InternalEObject oldDiscussant = (InternalEObject)discussant;
			discussant = (Person)eResolveProxy(oldDiscussant);
			if (discussant != oldDiscussant) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ConferencePackage.TALK__DISCUSSANT, oldDiscussant, discussant));
			}
		}
		return discussant;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Person basicGetDiscussant() {
		return discussant;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDiscussant(Person newDiscussant) {
		Person oldDiscussant = discussant;
		discussant = newDiscussant;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ConferencePackage.TALK__DISCUSSANT, oldDiscussant, discussant));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ConferencePackage.TALK__TITLE:
				return getTitle();
			case ConferencePackage.TALK__DURATION:
				return getDuration();
			case ConferencePackage.TALK__SPEAKER:
				if (resolve) return getSpeaker();
				return basicGetSpeaker();
			case ConferencePackage.TALK__DISCUSSANT:
				if (resolve) return getDiscussant();
				return basicGetDiscussant();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ConferencePackage.TALK__TITLE:
				setTitle((String)newValue);
				return;
			case ConferencePackage.TALK__DURATION:
				setDuration((Integer)newValue);
				return;
			case ConferencePackage.TALK__SPEAKER:
				setSpeaker((Person)newValue);
				return;
			case ConferencePackage.TALK__DISCUSSANT:
				setDiscussant((Person)newValue);
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
			case ConferencePackage.TALK__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case ConferencePackage.TALK__DURATION:
				setDuration(DURATION_EDEFAULT);
				return;
			case ConferencePackage.TALK__SPEAKER:
				setSpeaker((Person)null);
				return;
			case ConferencePackage.TALK__DISCUSSANT:
				setDiscussant((Person)null);
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
			case ConferencePackage.TALK__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case ConferencePackage.TALK__DURATION:
				return duration != DURATION_EDEFAULT;
			case ConferencePackage.TALK__SPEAKER:
				return speaker != null;
			case ConferencePackage.TALK__DISCUSSANT:
				return discussant != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (title: ");
		result.append(title);
		result.append(", duration: ");
		result.append(duration);
		result.append(')');
		return result.toString();
	}

} //TalkImpl
