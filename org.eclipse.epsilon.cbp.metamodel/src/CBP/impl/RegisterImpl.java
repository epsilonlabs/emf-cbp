/**
 */
package CBP.impl;

import CBP.CBPPackage;
import CBP.Register;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Register</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link CBP.impl.RegisterImpl#getEpackage <em>Epackage</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RegisterImpl extends EObjectImpl implements Register {
	/**
	 * The default value of the '{@link #getEpackage() <em>Epackage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEpackage()
	 * @generated
	 * @ordered
	 */
	protected static final String EPACKAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEpackage() <em>Epackage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEpackage()
	 * @generated
	 * @ordered
	 */
	protected String epackage = EPACKAGE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RegisterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CBPPackage.Literals.REGISTER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEpackage() {
		return epackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEpackage(String newEpackage) {
		String oldEpackage = epackage;
		epackage = newEpackage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CBPPackage.REGISTER__EPACKAGE, oldEpackage, epackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CBPPackage.REGISTER__EPACKAGE:
				return getEpackage();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CBPPackage.REGISTER__EPACKAGE:
				setEpackage((String)newValue);
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
			case CBPPackage.REGISTER__EPACKAGE:
				setEpackage(EPACKAGE_EDEFAULT);
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
			case CBPPackage.REGISTER__EPACKAGE:
				return EPACKAGE_EDEFAULT == null ? epackage != null : !EPACKAGE_EDEFAULT.equals(epackage);
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
		result.append(" (epackage: ");
		result.append(epackage);
		result.append(')');
		return result.toString();
	}

} //RegisterImpl
