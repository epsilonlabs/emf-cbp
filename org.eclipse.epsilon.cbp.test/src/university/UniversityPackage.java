/**
 */
package university;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see university.UniversityFactory
 * @model kind="package"
 * @generated
 */
public interface UniversityPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "university";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://university/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "cbp";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	UniversityPackage eINSTANCE = university.impl.UniversityPackageImpl.init();

	/**
	 * The meta object id for the '{@link university.impl.UniversityImpl <em>University</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see university.impl.UniversityImpl
	 * @see university.impl.UniversityPackageImpl#getUniversity()
	 * @generated
	 */
	int UNIVERSITY = 0;

	/**
	 * The feature id for the '<em><b>Departments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIVERSITY__DEPARTMENTS = 0;

	/**
	 * The feature id for the '<em><b>Chancelor</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIVERSITY__CHANCELOR = 1;

	/**
	 * The feature id for the '<em><b>Codes</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIVERSITY__CODES = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIVERSITY__NAME = 3;

	/**
	 * The number of structural features of the '<em>University</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIVERSITY_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link university.impl.StudentImpl <em>Student</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see university.impl.StudentImpl
	 * @see university.impl.UniversityPackageImpl#getStudent()
	 * @generated
	 */
	int STUDENT = 1;

	/**
	 * The feature id for the '<em><b>Enrolled Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUDENT__ENROLLED_MODULES = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUDENT__ID = 1;

	/**
	 * The feature id for the '<em><b>Tutor</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUDENT__TUTOR = 2;

	/**
	 * The number of structural features of the '<em>Student</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STUDENT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link university.impl.StaffMemberImpl <em>Staff Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see university.impl.StaffMemberImpl
	 * @see university.impl.UniversityPackageImpl#getStaffMember()
	 * @generated
	 */
	int STAFF_MEMBER = 2;

	/**
	 * The feature id for the '<em><b>Staff Member Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAFF_MEMBER__STAFF_MEMBER_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Taught Modules</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAFF_MEMBER__TAUGHT_MODULES = 1;

	/**
	 * The feature id for the '<em><b>First name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAFF_MEMBER__FIRST_NAME = 2;

	/**
	 * The feature id for the '<em><b>Last name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAFF_MEMBER__LAST_NAME = 3;

	/**
	 * The number of structural features of the '<em>Staff Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STAFF_MEMBER_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link university.impl.DepartmentImpl <em>Department</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see university.impl.DepartmentImpl
	 * @see university.impl.UniversityPackageImpl#getDepartment()
	 * @generated
	 */
	int DEPARTMENT = 3;

	/**
	 * The feature id for the '<em><b>Staff</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPARTMENT__STAFF = 0;

	/**
	 * The feature id for the '<em><b>Students</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPARTMENT__STUDENTS = 1;

	/**
	 * The feature id for the '<em><b>Modules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPARTMENT__MODULES = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPARTMENT__NAME = 3;

	/**
	 * The number of structural features of the '<em>Department</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPARTMENT_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link university.impl.ModuleImpl <em>Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see university.impl.ModuleImpl
	 * @see university.impl.UniversityPackageImpl#getModule()
	 * @generated
	 */
	int MODULE = 4;

	/**
	 * The feature id for the '<em><b>Module Lecturers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__MODULE_LECTURERS = 0;

	/**
	 * The feature id for the '<em><b>Enrolled Students</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__ENROLLED_STUDENTS = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__NAME = 2;

	/**
	 * The number of structural features of the '<em>Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link university.StaffMemberType <em>Staff Member Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see university.StaffMemberType
	 * @see university.impl.UniversityPackageImpl#getStaffMemberType()
	 * @generated
	 */
	int STAFF_MEMBER_TYPE = 5;


	/**
	 * Returns the meta object for class '{@link university.University <em>University</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>University</em>'.
	 * @see university.University
	 * @generated
	 */
	EClass getUniversity();

	/**
	 * Returns the meta object for the containment reference list '{@link university.University#getDepartments <em>Departments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Departments</em>'.
	 * @see university.University#getDepartments()
	 * @see #getUniversity()
	 * @generated
	 */
	EReference getUniversity_Departments();

	/**
	 * Returns the meta object for the containment reference '{@link university.University#getChancelor <em>Chancelor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Chancelor</em>'.
	 * @see university.University#getChancelor()
	 * @see #getUniversity()
	 * @generated
	 */
	EReference getUniversity_Chancelor();

	/**
	 * Returns the meta object for the attribute list '{@link university.University#getCodes <em>Codes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Codes</em>'.
	 * @see university.University#getCodes()
	 * @see #getUniversity()
	 * @generated
	 */
	EAttribute getUniversity_Codes();

	/**
	 * Returns the meta object for the attribute '{@link university.University#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see university.University#getName()
	 * @see #getUniversity()
	 * @generated
	 */
	EAttribute getUniversity_Name();

	/**
	 * Returns the meta object for class '{@link university.Student <em>Student</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Student</em>'.
	 * @see university.Student
	 * @generated
	 */
	EClass getStudent();

	/**
	 * Returns the meta object for the reference list '{@link university.Student#getEnrolledModules <em>Enrolled Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Enrolled Modules</em>'.
	 * @see university.Student#getEnrolledModules()
	 * @see #getStudent()
	 * @generated
	 */
	EReference getStudent_EnrolledModules();

	/**
	 * Returns the meta object for the attribute '{@link university.Student#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see university.Student#getId()
	 * @see #getStudent()
	 * @generated
	 */
	EAttribute getStudent_Id();

	/**
	 * Returns the meta object for the reference '{@link university.Student#getTutor <em>Tutor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Tutor</em>'.
	 * @see university.Student#getTutor()
	 * @see #getStudent()
	 * @generated
	 */
	EReference getStudent_Tutor();

	/**
	 * Returns the meta object for class '{@link university.StaffMember <em>Staff Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Staff Member</em>'.
	 * @see university.StaffMember
	 * @generated
	 */
	EClass getStaffMember();

	/**
	 * Returns the meta object for the attribute '{@link university.StaffMember#getStaffMemberType <em>Staff Member Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Staff Member Type</em>'.
	 * @see university.StaffMember#getStaffMemberType()
	 * @see #getStaffMember()
	 * @generated
	 */
	EAttribute getStaffMember_StaffMemberType();

	/**
	 * Returns the meta object for the reference list '{@link university.StaffMember#getTaughtModules <em>Taught Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Taught Modules</em>'.
	 * @see university.StaffMember#getTaughtModules()
	 * @see #getStaffMember()
	 * @generated
	 */
	EReference getStaffMember_TaughtModules();

	/**
	 * Returns the meta object for the attribute '{@link university.StaffMember#getFirst_name <em>First name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>First name</em>'.
	 * @see university.StaffMember#getFirst_name()
	 * @see #getStaffMember()
	 * @generated
	 */
	EAttribute getStaffMember_First_name();

	/**
	 * Returns the meta object for the attribute '{@link university.StaffMember#getLast_name <em>Last name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last name</em>'.
	 * @see university.StaffMember#getLast_name()
	 * @see #getStaffMember()
	 * @generated
	 */
	EAttribute getStaffMember_Last_name();

	/**
	 * Returns the meta object for class '{@link university.Department <em>Department</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Department</em>'.
	 * @see university.Department
	 * @generated
	 */
	EClass getDepartment();

	/**
	 * Returns the meta object for the containment reference list '{@link university.Department#getStaff <em>Staff</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Staff</em>'.
	 * @see university.Department#getStaff()
	 * @see #getDepartment()
	 * @generated
	 */
	EReference getDepartment_Staff();

	/**
	 * Returns the meta object for the containment reference list '{@link university.Department#getStudents <em>Students</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Students</em>'.
	 * @see university.Department#getStudents()
	 * @see #getDepartment()
	 * @generated
	 */
	EReference getDepartment_Students();

	/**
	 * Returns the meta object for the containment reference list '{@link university.Department#getModules <em>Modules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Modules</em>'.
	 * @see university.Department#getModules()
	 * @see #getDepartment()
	 * @generated
	 */
	EReference getDepartment_Modules();

	/**
	 * Returns the meta object for the attribute '{@link university.Department#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see university.Department#getName()
	 * @see #getDepartment()
	 * @generated
	 */
	EAttribute getDepartment_Name();

	/**
	 * Returns the meta object for class '{@link university.Module <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module</em>'.
	 * @see university.Module
	 * @generated
	 */
	EClass getModule();

	/**
	 * Returns the meta object for the reference list '{@link university.Module#getModuleLecturers <em>Module Lecturers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Module Lecturers</em>'.
	 * @see university.Module#getModuleLecturers()
	 * @see #getModule()
	 * @generated
	 */
	EReference getModule_ModuleLecturers();

	/**
	 * Returns the meta object for the reference list '{@link university.Module#getEnrolledStudents <em>Enrolled Students</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Enrolled Students</em>'.
	 * @see university.Module#getEnrolledStudents()
	 * @see #getModule()
	 * @generated
	 */
	EReference getModule_EnrolledStudents();

	/**
	 * Returns the meta object for the attribute '{@link university.Module#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see university.Module#getName()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_Name();

	/**
	 * Returns the meta object for enum '{@link university.StaffMemberType <em>Staff Member Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Staff Member Type</em>'.
	 * @see university.StaffMemberType
	 * @generated
	 */
	EEnum getStaffMemberType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	UniversityFactory getUniversityFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link university.impl.UniversityImpl <em>University</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see university.impl.UniversityImpl
		 * @see university.impl.UniversityPackageImpl#getUniversity()
		 * @generated
		 */
		EClass UNIVERSITY = eINSTANCE.getUniversity();

		/**
		 * The meta object literal for the '<em><b>Departments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNIVERSITY__DEPARTMENTS = eINSTANCE.getUniversity_Departments();

		/**
		 * The meta object literal for the '<em><b>Chancelor</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNIVERSITY__CHANCELOR = eINSTANCE.getUniversity_Chancelor();

		/**
		 * The meta object literal for the '<em><b>Codes</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNIVERSITY__CODES = eINSTANCE.getUniversity_Codes();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNIVERSITY__NAME = eINSTANCE.getUniversity_Name();

		/**
		 * The meta object literal for the '{@link university.impl.StudentImpl <em>Student</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see university.impl.StudentImpl
		 * @see university.impl.UniversityPackageImpl#getStudent()
		 * @generated
		 */
		EClass STUDENT = eINSTANCE.getStudent();

		/**
		 * The meta object literal for the '<em><b>Enrolled Modules</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STUDENT__ENROLLED_MODULES = eINSTANCE.getStudent_EnrolledModules();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STUDENT__ID = eINSTANCE.getStudent_Id();

		/**
		 * The meta object literal for the '<em><b>Tutor</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STUDENT__TUTOR = eINSTANCE.getStudent_Tutor();

		/**
		 * The meta object literal for the '{@link university.impl.StaffMemberImpl <em>Staff Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see university.impl.StaffMemberImpl
		 * @see university.impl.UniversityPackageImpl#getStaffMember()
		 * @generated
		 */
		EClass STAFF_MEMBER = eINSTANCE.getStaffMember();

		/**
		 * The meta object literal for the '<em><b>Staff Member Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STAFF_MEMBER__STAFF_MEMBER_TYPE = eINSTANCE.getStaffMember_StaffMemberType();

		/**
		 * The meta object literal for the '<em><b>Taught Modules</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STAFF_MEMBER__TAUGHT_MODULES = eINSTANCE.getStaffMember_TaughtModules();

		/**
		 * The meta object literal for the '<em><b>First name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STAFF_MEMBER__FIRST_NAME = eINSTANCE.getStaffMember_First_name();

		/**
		 * The meta object literal for the '<em><b>Last name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STAFF_MEMBER__LAST_NAME = eINSTANCE.getStaffMember_Last_name();

		/**
		 * The meta object literal for the '{@link university.impl.DepartmentImpl <em>Department</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see university.impl.DepartmentImpl
		 * @see university.impl.UniversityPackageImpl#getDepartment()
		 * @generated
		 */
		EClass DEPARTMENT = eINSTANCE.getDepartment();

		/**
		 * The meta object literal for the '<em><b>Staff</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPARTMENT__STAFF = eINSTANCE.getDepartment_Staff();

		/**
		 * The meta object literal for the '<em><b>Students</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPARTMENT__STUDENTS = eINSTANCE.getDepartment_Students();

		/**
		 * The meta object literal for the '<em><b>Modules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPARTMENT__MODULES = eINSTANCE.getDepartment_Modules();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEPARTMENT__NAME = eINSTANCE.getDepartment_Name();

		/**
		 * The meta object literal for the '{@link university.impl.ModuleImpl <em>Module</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see university.impl.ModuleImpl
		 * @see university.impl.UniversityPackageImpl#getModule()
		 * @generated
		 */
		EClass MODULE = eINSTANCE.getModule();

		/**
		 * The meta object literal for the '<em><b>Module Lecturers</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODULE__MODULE_LECTURERS = eINSTANCE.getModule_ModuleLecturers();

		/**
		 * The meta object literal for the '<em><b>Enrolled Students</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODULE__ENROLLED_STUDENTS = eINSTANCE.getModule_EnrolledStudents();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__NAME = eINSTANCE.getModule_Name();

		/**
		 * The meta object literal for the '{@link university.StaffMemberType <em>Staff Member Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see university.StaffMemberType
		 * @see university.impl.UniversityPackageImpl#getStaffMemberType()
		 * @generated
		 */
		EEnum STAFF_MEMBER_TYPE = eINSTANCE.getStaffMemberType();

	}

} //UniversityPackage
