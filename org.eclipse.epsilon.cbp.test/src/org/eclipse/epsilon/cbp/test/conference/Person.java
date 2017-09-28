/**
 */
package org.eclipse.epsilon.cbp.test.conference;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Person</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Person#getFullName <em>Full Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.conference.Person#getAffiliation <em>Affiliation</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getPerson()
 * @model
 * @generated
 */
public interface Person extends Node {
	/**
	 * Returns the value of the '<em><b>Full Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Full Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Full Name</em>' attribute.
	 * @see #setFullName(String)
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getPerson_FullName()
	 * @model
	 * @generated
	 */
	String getFullName();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Person#getFullName <em>Full Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Full Name</em>' attribute.
	 * @see #getFullName()
	 * @generated
	 */
	void setFullName(String value);

	/**
	 * Returns the value of the '<em><b>Affiliation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Affiliation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Affiliation</em>' attribute.
	 * @see #setAffiliation(String)
	 * @see org.eclipse.epsilon.cbp.test.conference.ConferencePackage#getPerson_Affiliation()
	 * @model
	 * @generated
	 */
	String getAffiliation();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.conference.Person#getAffiliation <em>Affiliation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Affiliation</em>' attribute.
	 * @see #getAffiliation()
	 * @generated
	 */
	void setAffiliation(String value);

} // Person
