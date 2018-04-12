/**
 */
package CBP.impl;

import CBP.CBPPackage;
import CBP.Create;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Create</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link CBP.impl.CreateImpl#getEclass <em>Eclass</em>}</li>
 *   <li>{@link CBP.impl.CreateImpl#getEpackage <em>Epackage</em>}</li>
 *   <li>{@link CBP.impl.CreateImpl#getId <em>Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CreateImpl extends EObjectImpl implements Create {
	/**
	 * The default value of the '{@link #getEclass() <em>Eclass</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEclass()
	 * @generated
	 * @ordered
	 */
	protected static final String ECLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEclass() <em>Eclass</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEclass()
	 * @generated
	 * @ordered
	 */
	protected String eclass = ECLASS_EDEFAULT;

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
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CreateImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CBPPackage.Literals.CREATE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEclass() {
		return eclass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEclass(String newEclass) {
		String oldEclass = eclass;
		eclass = newEclass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CBPPackage.CREATE__ECLASS, oldEclass, eclass));
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
			eNotify(new ENotificationImpl(this, Notification.SET, CBPPackage.CREATE__EPACKAGE, oldEpackage, epackage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CBPPackage.CREATE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CBPPackage.CREATE__ECLASS:
				return getEclass();
			case CBPPackage.CREATE__EPACKAGE:
				return getEpackage();
			case CBPPackage.CREATE__ID:
				return getId();
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
			case CBPPackage.CREATE__ECLASS:
				setEclass((String)newValue);
				return;
			case CBPPackage.CREATE__EPACKAGE:
				setEpackage((String)newValue);
				return;
			case CBPPackage.CREATE__ID:
				setId((String)newValue);
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
			case CBPPackage.CREATE__ECLASS:
				setEclass(ECLASS_EDEFAULT);
				return;
			case CBPPackage.CREATE__EPACKAGE:
				setEpackage(EPACKAGE_EDEFAULT);
				return;
			case CBPPackage.CREATE__ID:
				setId(ID_EDEFAULT);
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
			case CBPPackage.CREATE__ECLASS:
				return ECLASS_EDEFAULT == null ? eclass != null : !ECLASS_EDEFAULT.equals(eclass);
			case CBPPackage.CREATE__EPACKAGE:
				return EPACKAGE_EDEFAULT == null ? epackage != null : !EPACKAGE_EDEFAULT.equals(epackage);
			case CBPPackage.CREATE__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(" (eclass: ");
		result.append(eclass);
		result.append(", epackage: ");
		result.append(epackage);
		result.append(", id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //CreateImpl
