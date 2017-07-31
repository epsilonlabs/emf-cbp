/**
 */
package org.eclipse.epsilon.cbp.test.employee.impl;

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
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.epsilon.cbp.test.employee.Employee;
import org.eclipse.epsilon.cbp.test.employee.EmployeePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Employee</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.impl.EmployeeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.impl.EmployeeImpl#getAccounts <em>Accounts</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.impl.EmployeeImpl#getPartner <em>Partner</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.impl.EmployeeImpl#getManages <em>Manages</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.impl.EmployeeImpl#getRefManages <em>Ref Manages</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EmployeeImpl extends EObjectImpl implements Employee {
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
	 * The cached value of the '{@link #getAccounts() <em>Accounts</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAccounts()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> accounts;

	/**
	 * The cached value of the '{@link #getPartner() <em>Partner</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPartner()
	 * @generated
	 * @ordered
	 */
	protected Employee partner;

	/**
	 * The cached value of the '{@link #getManages() <em>Manages</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getManages()
	 * @generated
	 * @ordered
	 */
	protected EList<Employee> manages;

	/**
	 * The cached value of the '{@link #getRefManages() <em>Ref Manages</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefManages()
	 * @generated
	 * @ordered
	 */
	protected EList<Employee> refManages;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EmployeeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EmployeePackage.Literals.EMPLOYEE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, EmployeePackage.EMPLOYEE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getAccounts() {
		if (accounts == null) {
			accounts = new EDataTypeUniqueEList<Integer>(Integer.class, this, EmployeePackage.EMPLOYEE__ACCOUNTS);
		}
		return accounts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Employee getPartner() {
		if (partner != null && partner.eIsProxy()) {
			InternalEObject oldPartner = (InternalEObject)partner;
			partner = (Employee)eResolveProxy(oldPartner);
			if (partner != oldPartner) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, EmployeePackage.EMPLOYEE__PARTNER, oldPartner, partner));
			}
		}
		return partner;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Employee basicGetPartner() {
		return partner;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPartner(Employee newPartner) {
		Employee oldPartner = partner;
		partner = newPartner;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, EmployeePackage.EMPLOYEE__PARTNER, oldPartner, partner));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Employee> getManages() {
		if (manages == null) {
			manages = new EObjectContainmentEList<Employee>(Employee.class, this, EmployeePackage.EMPLOYEE__MANAGES);
		}
		return manages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Employee> getRefManages() {
		if (refManages == null) {
			refManages = new EObjectResolvingEList<Employee>(Employee.class, this, EmployeePackage.EMPLOYEE__REF_MANAGES);
		}
		return refManages;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case EmployeePackage.EMPLOYEE__MANAGES:
				return ((InternalEList<?>)getManages()).basicRemove(otherEnd, msgs);
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
			case EmployeePackage.EMPLOYEE__NAME:
				return getName();
			case EmployeePackage.EMPLOYEE__ACCOUNTS:
				return getAccounts();
			case EmployeePackage.EMPLOYEE__PARTNER:
				if (resolve) return getPartner();
				return basicGetPartner();
			case EmployeePackage.EMPLOYEE__MANAGES:
				return getManages();
			case EmployeePackage.EMPLOYEE__REF_MANAGES:
				return getRefManages();
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
			case EmployeePackage.EMPLOYEE__NAME:
				setName((String)newValue);
				return;
			case EmployeePackage.EMPLOYEE__ACCOUNTS:
				getAccounts().clear();
				getAccounts().addAll((Collection<? extends Integer>)newValue);
				return;
			case EmployeePackage.EMPLOYEE__PARTNER:
				setPartner((Employee)newValue);
				return;
			case EmployeePackage.EMPLOYEE__MANAGES:
				getManages().clear();
				getManages().addAll((Collection<? extends Employee>)newValue);
				return;
			case EmployeePackage.EMPLOYEE__REF_MANAGES:
				getRefManages().clear();
				getRefManages().addAll((Collection<? extends Employee>)newValue);
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
			case EmployeePackage.EMPLOYEE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case EmployeePackage.EMPLOYEE__ACCOUNTS:
				getAccounts().clear();
				return;
			case EmployeePackage.EMPLOYEE__PARTNER:
				setPartner((Employee)null);
				return;
			case EmployeePackage.EMPLOYEE__MANAGES:
				getManages().clear();
				return;
			case EmployeePackage.EMPLOYEE__REF_MANAGES:
				getRefManages().clear();
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
			case EmployeePackage.EMPLOYEE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case EmployeePackage.EMPLOYEE__ACCOUNTS:
				return accounts != null && !accounts.isEmpty();
			case EmployeePackage.EMPLOYEE__PARTNER:
				return partner != null;
			case EmployeePackage.EMPLOYEE__MANAGES:
				return manages != null && !manages.isEmpty();
			case EmployeePackage.EMPLOYEE__REF_MANAGES:
				return refManages != null && !refManages.isEmpty();
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
		result.append(", accounts: ");
		result.append(accounts);
		result.append(')');
		return result.toString();
	}

} //EmployeeImpl
