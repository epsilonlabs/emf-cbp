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
 * An implementation of the model object '<em><b>Student</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link university.impl.StudentImpl#getEnrolledModules <em>Enrolled Modules</em>}</li>
 *   <li>{@link university.impl.StudentImpl#getId <em>Id</em>}</li>
 *   <li>{@link university.impl.StudentImpl#getTutor <em>Tutor</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StudentImpl extends EObjectImpl implements Student {
	/**
	 * The cached value of the '{@link #getEnrolledModules() <em>Enrolled Modules</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnrolledModules()
	 * @generated
	 * @ordered
	 */
	protected EList<Module> enrolledModules;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final int ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected int id = ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTutor() <em>Tutor</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTutor()
	 * @generated
	 * @ordered
	 */
	protected StaffMember tutor;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StudentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UniversityPackage.Literals.STUDENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Module> getEnrolledModules() {
		if (enrolledModules == null) {
			enrolledModules = new EObjectWithInverseResolvingEList.ManyInverse<Module>(Module.class, this, UniversityPackage.STUDENT__ENROLLED_MODULES, UniversityPackage.MODULE__ENROLLED_STUDENTS);
		}
		return enrolledModules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(int newId) {
		int oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.STUDENT__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StaffMember getTutor() {
		if (tutor != null && tutor.eIsProxy()) {
			InternalEObject oldTutor = (InternalEObject)tutor;
			tutor = (StaffMember)eResolveProxy(oldTutor);
			if (tutor != oldTutor) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, UniversityPackage.STUDENT__TUTOR, oldTutor, tutor));
			}
		}
		return tutor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StaffMember basicGetTutor() {
		return tutor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTutor(StaffMember newTutor) {
		StaffMember oldTutor = tutor;
		tutor = newTutor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UniversityPackage.STUDENT__TUTOR, oldTutor, tutor));
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
			case UniversityPackage.STUDENT__ENROLLED_MODULES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getEnrolledModules()).basicAdd(otherEnd, msgs);
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
			case UniversityPackage.STUDENT__ENROLLED_MODULES:
				return ((InternalEList<?>)getEnrolledModules()).basicRemove(otherEnd, msgs);
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
			case UniversityPackage.STUDENT__ENROLLED_MODULES:
				return getEnrolledModules();
			case UniversityPackage.STUDENT__ID:
				return getId();
			case UniversityPackage.STUDENT__TUTOR:
				if (resolve) return getTutor();
				return basicGetTutor();
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
			case UniversityPackage.STUDENT__ENROLLED_MODULES:
				getEnrolledModules().clear();
				getEnrolledModules().addAll((Collection<? extends Module>)newValue);
				return;
			case UniversityPackage.STUDENT__ID:
				setId((Integer)newValue);
				return;
			case UniversityPackage.STUDENT__TUTOR:
				setTutor((StaffMember)newValue);
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
			case UniversityPackage.STUDENT__ENROLLED_MODULES:
				getEnrolledModules().clear();
				return;
			case UniversityPackage.STUDENT__ID:
				setId(ID_EDEFAULT);
				return;
			case UniversityPackage.STUDENT__TUTOR:
				setTutor((StaffMember)null);
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
			case UniversityPackage.STUDENT__ENROLLED_MODULES:
				return enrolledModules != null && !enrolledModules.isEmpty();
			case UniversityPackage.STUDENT__ID:
				return id != ID_EDEFAULT;
			case UniversityPackage.STUDENT__TUTOR:
				return tutor != null;
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
		result.append(" (id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //StudentImpl
