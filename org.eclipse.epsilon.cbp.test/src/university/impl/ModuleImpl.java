/**
 */
package university.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import university.Module;
import university.StaffMember;
import university.Student;
import university.UniversityPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link university.impl.ModuleImpl#getModuleLecturers <em>Module Lecturers</em>}</li>
 *   <li>{@link university.impl.ModuleImpl#getEnrolledStudents <em>Enrolled Students</em>}</li>
 *   <li>{@link university.impl.ModuleImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ModuleImpl extends EObjectImpl implements Module {
	/**
	 * The cached value of the '{@link #getModuleLecturers() <em>Module Lecturers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleLecturers()
	 * @generated
	 * @ordered
	 */
	protected EList<StaffMember> moduleLecturers;

	/**
	 * The cached value of the '{@link #getEnrolledStudents() <em>Enrolled Students</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnrolledStudents()
	 * @generated
	 * @ordered
	 */
	protected EList<Student> enrolledStudents;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UniversityPackage.Literals.MODULE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<StaffMember> getModuleLecturers() {
		if (moduleLecturers == null) {
			moduleLecturers = new EObjectWithInverseResolvingEList.ManyInverse<StaffMember>(StaffMember.class, this, UniversityPackage.MODULE__MODULE_LECTURERS, UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES);
		}
		return moduleLecturers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Student> getEnrolledStudents() {
		if (enrolledStudents == null) {
			enrolledStudents = new EObjectWithInverseResolvingEList.ManyInverse<Student>(Student.class, this, UniversityPackage.MODULE__ENROLLED_STUDENTS, UniversityPackage.STUDENT__ENROLLED_MODULES);
		}
		return enrolledStudents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.MODULE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UniversityPackage.MODULE__MODULE_LECTURERS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getModuleLecturers()).basicAdd(otherEnd, msgs);
			case UniversityPackage.MODULE__ENROLLED_STUDENTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getEnrolledStudents()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UniversityPackage.MODULE__MODULE_LECTURERS:
				return ((InternalEList<?>)getModuleLecturers()).basicRemove(otherEnd, msgs);
			case UniversityPackage.MODULE__ENROLLED_STUDENTS:
				return ((InternalEList<?>)getEnrolledStudents()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case UniversityPackage.MODULE__MODULE_LECTURERS:
				return getModuleLecturers();
			case UniversityPackage.MODULE__ENROLLED_STUDENTS:
				return getEnrolledStudents();
			case UniversityPackage.MODULE__NAME:
				return getName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case UniversityPackage.MODULE__MODULE_LECTURERS:
				getModuleLecturers().clear();
				getModuleLecturers().addAll((Collection<? extends StaffMember>)newValue);
				return;
			case UniversityPackage.MODULE__ENROLLED_STUDENTS:
				getEnrolledStudents().clear();
				getEnrolledStudents().addAll((Collection<? extends Student>)newValue);
				return;
			case UniversityPackage.MODULE__NAME:
				setName((String)newValue);
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
			case UniversityPackage.MODULE__MODULE_LECTURERS:
				getModuleLecturers().clear();
				return;
			case UniversityPackage.MODULE__ENROLLED_STUDENTS:
				getEnrolledStudents().clear();
				return;
			case UniversityPackage.MODULE__NAME:
				setName(NAME_EDEFAULT);
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
			case UniversityPackage.MODULE__MODULE_LECTURERS:
				return moduleLecturers != null && !moduleLecturers.isEmpty();
			case UniversityPackage.MODULE__ENROLLED_STUDENTS:
				return enrolledStudents != null && !enrolledStudents.isEmpty();
			case UniversityPackage.MODULE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
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
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //ModuleImpl
