/**
 */
package org.eclipse.epsilon.cbp.test.conference;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Track</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Track#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Track#getTalks <em>Talks</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTrack()
 * @model
 * @generated
 */
public interface Track extends Slot {
	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTrack_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Track#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Talks</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.conference.Talk}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Talks</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Talks</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTrack_Talks()
	 * @model containment="true"
	 * @generated
	 */
	EList<Talk> getTalks();

} // Track
