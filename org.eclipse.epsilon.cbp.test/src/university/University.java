/**
 */
package university;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>University</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link university.University#getDepartments <em>Departments</em>}</li>
 *   <li>{@link university.University#getChancelor <em>Chancelor</em>}</li>
 *   <li>{@link university.University#getCodes <em>Codes</em>}</li>
 * </ul>
 *
 * @see university.UniversityPackage#getUniversity()
 * @model
 * @generated
 */
public interface University extends EObject {
	/**
	 * Returns the value of the '<em><b>Departments</b></em>' containment reference list.
	 * The list contents are of type {@link university.Department}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Departments</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Departments</em>' containment reference list.
	 * @see university.UniversityPackage#getUniversity_Departments()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<Department> getDepartments();

	/**
	 * Returns the value of the '<em><b>Chancelor</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chancelor</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chancelor</em>' containment reference.
	 * @see #setChancelor(StaffMember)
	 * @see university.UniversityPackage#getUniversity_Chancelor()
	 * @model containment="true"
	 * @generated
	 */
	StaffMember getChancelor();

	/**
	 * Sets the value of the '{@link university.University#getChancelor <em>Chancelor</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Chancelor</em>' containment reference.
	 * @see #getChancelor()
	 * @generated
	 */
	void setChancelor(StaffMember value);

	/**
	 * Returns the value of the '<em><b>Codes</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Codes</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Codes</em>' attribute list.
	 * @see university.UniversityPackage#getUniversity_Codes()
	 * @model
	 * @generated
	 */
	EList<String> getCodes();

} // University
