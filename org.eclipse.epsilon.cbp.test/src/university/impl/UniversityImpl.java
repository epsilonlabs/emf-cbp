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

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import university.Department;
import university.StaffMember;
import university.University;
import university.UniversityPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>University</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link university.impl.UniversityImpl#getDepartments <em>Departments</em>}</li>
 *   <li>{@link university.impl.UniversityImpl#getChancelor <em>Chancelor</em>}</li>
 *   <li>{@link university.impl.UniversityImpl#getCodes <em>Codes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UniversityImpl extends EObjectImpl implements University {
	/**
	 * The cached value of the '{@link #getDepartments() <em>Departments</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDepartments()
	 * @generated
	 * @ordered
	 */
	protected EList<Department> departments;

	/**
	 * The cached value of the '{@link #getChancelor() <em>Chancelor</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChancelor()
	 * @generated
	 * @ordered
	 */
	protected StaffMember chancelor;

	/**
	 * The cached value of the '{@link #getCodes() <em>Codes</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodes()
	 * @generated
	 * @ordered
	 */
	protected EList<String> codes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UniversityImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UniversityPackage.Literals.UNIVERSITY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Department> getDepartments() {
		if (departments == null) {
			departments = new EObjectContainmentEList<Department>(Department.class, this, UniversityPackage.UNIVERSITY__DEPARTMENTS);
		}
		return departments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StaffMember getChancelor() {
		return chancelor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetChancelor(StaffMember newChancelor, NotificationChain msgs) {
		StaffMember oldChancelor = chancelor;
		chancelor = newChancelor;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, UniversityPackage.UNIVERSITY__CHANCELOR, oldChancelor, newChancelor);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setChancelor(StaffMember newChancelor) {
		if (newChancelor != chancelor) {
			NotificationChain msgs = null;
			if (chancelor != null)
				msgs = ((InternalEObject)chancelor).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - UniversityPackage.UNIVERSITY__CHANCELOR, null, msgs);
			if (newChancelor != null)
				msgs = ((InternalEObject)newChancelor).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - UniversityPackage.UNIVERSITY__CHANCELOR, null, msgs);
			msgs = basicSetChancelor(newChancelor, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.UNIVERSITY__CHANCELOR, newChancelor, newChancelor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getCodes() {
		if (codes == null) {
			codes = new EDataTypeUniqueEList<String>(String.class, this, UniversityPackage.UNIVERSITY__CODES);
		}
		return codes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UniversityPackage.UNIVERSITY__DEPARTMENTS:
				return ((InternalEList<?>)getDepartments()).basicRemove(otherEnd, msgs);
			case UniversityPackage.UNIVERSITY__CHANCELOR:
				return basicSetChancelor(null, msgs);
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
			case UniversityPackage.UNIVERSITY__DEPARTMENTS:
				return getDepartments();
			case UniversityPackage.UNIVERSITY__CHANCELOR:
				return getChancelor();
			case UniversityPackage.UNIVERSITY__CODES:
				return getCodes();
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
			case UniversityPackage.UNIVERSITY__DEPARTMENTS:
				getDepartments().clear();
				getDepartments().addAll((Collection<? extends Department>)newValue);
				return;
			case UniversityPackage.UNIVERSITY__CHANCELOR:
				setChancelor((StaffMember)newValue);
				return;
			case UniversityPackage.UNIVERSITY__CODES:
				getCodes().clear();
				getCodes().addAll((Collection<? extends String>)newValue);
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
			case UniversityPackage.UNIVERSITY__DEPARTMENTS:
				getDepartments().clear();
				return;
			case UniversityPackage.UNIVERSITY__CHANCELOR:
				setChancelor((StaffMember)null);
				return;
			case UniversityPackage.UNIVERSITY__CODES:
				getCodes().clear();
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
			case UniversityPackage.UNIVERSITY__DEPARTMENTS:
				return departments != null && !departments.isEmpty();
			case UniversityPackage.UNIVERSITY__CHANCELOR:
				return chancelor != null;
			case UniversityPackage.UNIVERSITY__CODES:
				return codes != null && !codes.isEmpty();
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
		result.append(" (codes: ");
		result.append(codes);
		result.append(')');
		return result.toString();
	}

} //UniversityImpl
