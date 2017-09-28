/**
 */
package org.eclipse.epsilon.cbp.test.conference;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Conference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Conference#getParticipants <em>Participants</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Conference#getRooms <em>Rooms</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Conference#getDays <em>Days</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getConference()
 * @model
 * @generated
 */
public interface Conference extends Node {
	/**
	 * Returns the value of the '<em><b>Participants</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.conference.Person}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Participants</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Participants</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getConference_Participants()
	 * @model containment="true"
	 * @generated
	 */
	EList<Person> getParticipants();

	/**
	 * Returns the value of the '<em><b>Rooms</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.conference.Room}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rooms</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rooms</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getConference_Rooms()
	 * @model containment="true"
	 * @generated
	 */
	EList<Room> getRooms();

	/**
	 * Returns the value of the '<em><b>Days</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.conference.Day}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Days</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Days</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getConference_Days()
	 * @model containment="true"
	 * @generated
	 */
	EList<Day> getDays();

} // Conference
