/**
 */
package org.eclipse.epsilon.cbp.test.employee;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Employee</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.Employee#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.Employee#getAccounts <em>Accounts</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.Employee#getPartner <em>Partner</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.Employee#getManages <em>Manages</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.employee.Employee#getRefManages <em>Ref Manages</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.employee.EmployeePackage#getEmployee()
 * @model
 * @generated
 */
public interface Employee extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.epsilon.cbp.test.employee.EmployeePackage#getEmployee_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.employee.Employee#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Accounts</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Accounts</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Accounts</em>' attribute list.
	 * @see org.eclipse.epsilon.cbp.test.employee.EmployeePackage#getEmployee_Accounts()
	 * @model
	 * @generated
	 */
	EList<Integer> getAccounts();

	/**
	 * Returns the value of the '<em><b>Partner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Partner</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Partner</em>' reference.
	 * @see #setPartner(Employee)
	 * @see org.eclipse.epsilon.cbp.test.employee.EmployeePackage#getEmployee_Partner()
	 * @model
	 * @generated
	 */
	Employee getPartner();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.employee.Employee#getPartner <em>Partner</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Partner</em>' reference.
	 * @see #getPartner()
	 * @generated
	 */
	void setPartner(Employee value);

	/**
	 * Returns the value of the '<em><b>Manages</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.employee.Employee}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Manages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Manages</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.employee.EmployeePackage#getEmployee_Manages()
	 * @model containment="true"
	 * @generated
	 */
	EList<Employee> getManages();

	/**
	 * Returns the value of the '<em><b>Ref Manages</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.employee.Employee}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ref Manages</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref Manages</em>' reference list.
	 * @see org.eclipse.epsilon.cbp.test.employee.EmployeePackage#getEmployee_RefManages()
	 * @model
	 * @generated
	 */
	EList<Employee> getRefManages();

} // Employee
