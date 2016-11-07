/**
 */
package university;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link university.Module#getModuleLecturers <em>Module Lecturers</em>}</li>
 *   <li>{@link university.Module#getEnrolledStudents <em>Enrolled Students</em>}</li>
 * </ul>
 *
 * @see university.UniversityPackage#getModule()
 * @model
 * @generated
 */
public interface Module extends EObject {
	/**
	 * Returns the value of the '<em><b>Module Lecturers</b></em>' reference list.
	 * The list contents are of type {@link university.StaffMember}.
	 * It is bidirectional and its opposite is '{@link university.StaffMember#getTaughtModules <em>Taught Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Module Lecturers</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Module Lecturers</em>' reference list.
	 * @see university.UniversityPackage#getModule_ModuleLecturers()
	 * @see university.StaffMember#getTaughtModules
	 * @model opposite="taughtModules"
	 * @generated
	 */
	EList<StaffMember> getModuleLecturers();

	/**
	 * Returns the value of the '<em><b>Enrolled Students</b></em>' reference list.
	 * The list contents are of type {@link university.Student}.
	 * It is bidirectional and its opposite is '{@link university.Student#getEnrolledModules <em>Enrolled Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Enrolled Students</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enrolled Students</em>' reference list.
	 * @see university.UniversityPackage#getModule_EnrolledStudents()
	 * @see university.Student#getEnrolledModules
	 * @model opposite="enrolledModules"
	 * @generated
	 */
	EList<Student> getEnrolledStudents();

} // Module
