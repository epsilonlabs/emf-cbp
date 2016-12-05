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
import university.StaffMemberType;
import university.UniversityPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Staff Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link university.impl.StaffMemberImpl#getStaffMemberType <em>Staff Member Type</em>}</li>
 *   <li>{@link university.impl.StaffMemberImpl#getTaughtModules <em>Taught Modules</em>}</li>
 *   <li>{@link university.impl.StaffMemberImpl#getFirst_name <em>First name</em>}</li>
 *   <li>{@link university.impl.StaffMemberImpl#getLast_name <em>Last name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StaffMemberImpl extends EObjectImpl implements StaffMember {
	/**
	 * The default value of the '{@link #getStaffMemberType() <em>Staff Member Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStaffMemberType()
	 * @generated
	 * @ordered
	 */
	protected static final StaffMemberType STAFF_MEMBER_TYPE_EDEFAULT = StaffMemberType.NONE;

	/**
	 * The cached value of the '{@link #getStaffMemberType() <em>Staff Member Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStaffMemberType()
	 * @generated
	 * @ordered
	 */
	protected StaffMemberType staffMemberType = STAFF_MEMBER_TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTaughtModules() <em>Taught Modules</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTaughtModules()
	 * @generated
	 * @ordered
	 */
	protected EList<Module> taughtModules;

	/**
	 * The default value of the '{@link #getFirst_name() <em>First name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFirst_name()
	 * @generated
	 * @ordered
	 */
	protected static final String FIRST_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFirst_name() <em>First name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFirst_name()
	 * @generated
	 * @ordered
	 */
	protected String first_name = FIRST_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getLast_name() <em>Last name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLast_name()
	 * @generated
	 * @ordered
	 */
	protected static final String LAST_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLast_name() <em>Last name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLast_name()
	 * @generated
	 * @ordered
	 */
	protected String last_name = LAST_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StaffMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UniversityPackage.Literals.STAFF_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StaffMemberType getStaffMemberType() {
		return staffMemberType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStaffMemberType(StaffMemberType newStaffMemberType) {
		StaffMemberType oldStaffMemberType = staffMemberType;
		staffMemberType = newStaffMemberType == null ? STAFF_MEMBER_TYPE_EDEFAULT : newStaffMemberType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.STAFF_MEMBER__STAFF_MEMBER_TYPE, oldStaffMemberType, staffMemberType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Module> getTaughtModules() {
		if (taughtModules == null) {
			taughtModules = new EObjectWithInverseResolvingEList.ManyInverse<Module>(Module.class, this, UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES, UniversityPackage.MODULE__MODULE_LECTURERS);
		}
		return taughtModules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFirst_name() {
		return first_name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFirst_name(String newFirst_name) {
		String oldFirst_name = first_name;
		first_name = newFirst_name;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.STAFF_MEMBER__FIRST_NAME, oldFirst_name, first_name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLast_name() {
		return last_name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLast_name(String newLast_name) {
		String oldLast_name = last_name;
		last_name = newLast_name;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.STAFF_MEMBER__LAST_NAME, oldLast_name, last_name));
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
			case UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getTaughtModules()).basicAdd(otherEnd, msgs);
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
			case UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES:
				return ((InternalEList<?>)getTaughtModules()).basicRemove(otherEnd, msgs);
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
			case UniversityPackage.STAFF_MEMBER__STAFF_MEMBER_TYPE:
				return getStaffMemberType();
			case UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES:
				return getTaughtModules();
			case UniversityPackage.STAFF_MEMBER__FIRST_NAME:
				return getFirst_name();
			case UniversityPackage.STAFF_MEMBER__LAST_NAME:
				return getLast_name();
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
			case UniversityPackage.STAFF_MEMBER__STAFF_MEMBER_TYPE:
				setStaffMemberType((StaffMemberType)newValue);
				return;
			case UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES:
				getTaughtModules().clear();
				getTaughtModules().addAll((Collection<? extends Module>)newValue);
				return;
			case UniversityPackage.STAFF_MEMBER__FIRST_NAME:
				setFirst_name((String)newValue);
				return;
			case UniversityPackage.STAFF_MEMBER__LAST_NAME:
				setLast_name((String)newValue);
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
			case UniversityPackage.STAFF_MEMBER__STAFF_MEMBER_TYPE:
				setStaffMemberType(STAFF_MEMBER_TYPE_EDEFAULT);
				return;
			case UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES:
				getTaughtModules().clear();
				return;
			case UniversityPackage.STAFF_MEMBER__FIRST_NAME:
				setFirst_name(FIRST_NAME_EDEFAULT);
				return;
			case UniversityPackage.STAFF_MEMBER__LAST_NAME:
				setLast_name(LAST_NAME_EDEFAULT);
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
			case UniversityPackage.STAFF_MEMBER__STAFF_MEMBER_TYPE:
				return staffMemberType != STAFF_MEMBER_TYPE_EDEFAULT;
			case UniversityPackage.STAFF_MEMBER__TAUGHT_MODULES:
				return taughtModules != null && !taughtModules.isEmpty();
			case UniversityPackage.STAFF_MEMBER__FIRST_NAME:
				return FIRST_NAME_EDEFAULT == null ? first_name != null : !FIRST_NAME_EDEFAULT.equals(first_name);
			case UniversityPackage.STAFF_MEMBER__LAST_NAME:
				return LAST_NAME_EDEFAULT == null ? last_name != null : !LAST_NAME_EDEFAULT.equals(last_name);
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
		result.append(" (staffMemberType: ");
		result.append(staffMemberType);
		result.append(", first_name: ");
		result.append(first_name);
		result.append(", last_name: ");
		result.append(last_name);
		result.append(')');
		return result.toString();
	}

} //StaffMemberImpl
