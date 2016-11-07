/**
 */
package university;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Department</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link university.Department#getStaff <em>Staff</em>}</li>
 *   <li>{@link university.Department#getStudents <em>Students</em>}</li>
 *   <li>{@link university.Department#getModules <em>Modules</em>}</li>
 * </ul>
 *
 * @see university.UniversityPackage#getDepartment()
 * @model
 * @generated
 */
public interface Department extends EObject {
	/**
	 * Returns the value of the '<em><b>Staff</b></em>' containment reference list.
	 * The list contents are of type {@link university.StaffMember}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Staff</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Staff</em>' containment reference list.
	 * @see university.UniversityPackage#getDepartment_Staff()
	 * @model containment="true"
	 * @generated
	 */
	EList<StaffMember> getStaff();

	/**
	 * Returns the value of the '<em><b>Students</b></em>' containment reference list.
	 * The list contents are of type {@link university.Student}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Students</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Students</em>' containment reference list.
	 * @see university.UniversityPackage#getDepartment_Students()
	 * @model containment="true"
	 * @generated
	 */
	EList<Student> getStudents();

	/**
	 * Returns the value of the '<em><b>Modules</b></em>' reference list.
	 * The list contents are of type {@link university.Module}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Modules</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modules</em>' reference list.
	 * @see university.UniversityPackage#getDepartment_Modules()
	 * @model
	 * @generated
	 */
	EList<Module> getModules();

} // Department
