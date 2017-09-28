/**
 */
package org.eclipse.epsilon.cbp.test.conference;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Talk</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Talk#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Talk#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Talk#getSpeaker <em>Speaker</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Talk#getDiscussant <em>Discussant</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTalk()
 * @model
 * @generated
 */
public interface Talk extends Node {
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
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTalk_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Talk#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Duration</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Duration</em>' attribute.
	 * @see #setDuration(int)
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTalk_Duration()
	 * @model
	 * @generated
	 */
	int getDuration();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Talk#getDuration <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	void setDuration(int value);

	/**
	 * Returns the value of the '<em><b>Speaker</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Speaker</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Speaker</em>' reference.
	 * @see #setSpeaker(Person)
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTalk_Speaker()
	 * @model
	 * @generated
	 */
	Person getSpeaker();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Talk#getSpeaker <em>Speaker</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Speaker</em>' reference.
	 * @see #getSpeaker()
	 * @generated
	 */
	void setSpeaker(Person value);

	/**
	 * Returns the value of the '<em><b>Discussant</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Discussant</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Discussant</em>' reference.
	 * @see #setDiscussant(Person)
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getTalk_Discussant()
	 * @model
	 * @generated
	 */
	Person getDiscussant();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Talk#getDiscussant <em>Discussant</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Discussant</em>' reference.
	 * @see #getDiscussant()
	 * @generated
	 */
	void setDiscussant(Person value);

} // Talk
