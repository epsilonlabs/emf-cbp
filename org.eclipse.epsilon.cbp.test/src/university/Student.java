/**
 */
package university;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Student</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link university.Student#getEnrolledModules <em>Enrolled Modules</em>}</li>
 *   <li>{@link university.Student#getId <em>Id</em>}</li>
 *   <li>{@link university.Student#getTutor <em>Tutor</em>}</li>
 * </ul>
 *
 * @see university.UniversityPackage#getStudent()
 * @model
 * @generated
 */
public interface Student extends EObject {
	/**
	 * Returns the value of the '<em><b>Enrolled Modules</b></em>' reference list.
	 * The list contents are of type {@link university.Module}.
	 * It is bidirectional and its opposite is '{@link university.Module#getEnrolledStudents <em>Enrolled Students</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Enrolled Modules</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Enrolled Modules</em>' reference list.
	 * @see university.UniversityPackage#getStudent_EnrolledModules()
	 * @see university.Module#getEnrolledStudents
	 * @model opposite="enrolledStudents"
	 * @generated
	 */
	EList<Module> getEnrolledModules();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(int)
	 * @see university.UniversityPackage#getStudent_Id()
	 * @model
	 * @generated
	 */
	int getId();

	/**
	 * Sets the value of the '{@link university.Student#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(int value);

	/**
	 * Returns the value of the '<em><b>Tutor</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tutor</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tutor</em>' reference.
	 * @see #setTutor(StaffMember)
	 * @see university.UniversityPackage#getStudent_Tutor()
	 * @model
	 * @generated
	 */
	StaffMember getTutor();

	/**
	 * Sets the value of the '{@link university.Student#getTutor <em>Tutor</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tutor</em>' reference.
	 * @see #getTutor()
	 * @generated
	 */
	void setTutor(StaffMember value);

} // Student
