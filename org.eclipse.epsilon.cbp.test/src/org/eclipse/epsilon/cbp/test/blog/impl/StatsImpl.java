/**
 */
package org.eclipse.epsilon.cbp.test.blog.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.eclipse.epsilon.cbp.test.blog.Stats;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Stats</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.StatsImpl#getPageloads <em>Pageloads</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.StatsImpl#getVisitors <em>Visitors</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StatsImpl extends MinimalEObjectImpl.Container implements Stats {
	/**
	 * The default value of the '{@link #getPageloads() <em>Pageloads</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPageloads()
	 * @generated
	 * @ordered
	 */
	protected static final int PAGELOADS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPageloads() <em>Pageloads</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPageloads()
	 * @generated
	 * @ordered
	 */
	protected int pageloads = PAGELOADS_EDEFAULT;

	/**
	 * The default value of the '{@link #getVisitors() <em>Visitors</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVisitors()
	 * @generated
	 * @ordered
	 */
	protected static final int VISITORS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getVisitors() <em>Visitors</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVisitors()
	 * @generated
	 * @ordered
	 */
	protected int visitors = VISITORS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StatsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BlogPackage.Literals.STATS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getPageloads() {
		return pageloads;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPageloads(int newPageloads) {
		int oldPageloads = pageloads;
		pageloads = newPageloads;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.STATS__PAGELOADS, oldPageloads, pageloads));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getVisitors() {
		return visitors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVisitors(int newVisitors) {
		int oldVisitors = visitors;
		visitors = newVisitors;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.STATS__VISITORS, oldVisitors, visitors));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case BlogPackage.STATS__PAGELOADS:
				return getPageloads();
			case BlogPackage.STATS__VISITORS:
				return getVisitors();
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
			case BlogPackage.STATS__PAGELOADS:
				setPageloads((Integer)newValue);
				return;
			case BlogPackage.STATS__VISITORS:
				setVisitors((Integer)newValue);
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
			case BlogPackage.STATS__PAGELOADS:
				setPageloads(PAGELOADS_EDEFAULT);
				return;
			case BlogPackage.STATS__VISITORS:
				setVisitors(VISITORS_EDEFAULT);
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
			case BlogPackage.STATS__PAGELOADS:
				return pageloads != PAGELOADS_EDEFAULT;
			case BlogPackage.STATS__VISITORS:
				return visitors != VISITORS_EDEFAULT;
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
		result.append(" (pageloads: ");
		result.append(pageloads);
		result.append(", visitors: ");
		result.append(visitors);
		result.append(')');
		return result.toString();
	}

} //StatsImpl
