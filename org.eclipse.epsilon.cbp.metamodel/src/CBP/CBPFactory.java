/**
 */
package CBP;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see CBP.CBPPackage
 * @generated
 */
public interface CBPFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CBPFactory eINSTANCE = CBP.impl.CBPFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Session</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Session</em>'.
	 * @generated
	 */
	Session createSession();

	/**
	 * Returns a new object of class '<em>Register</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Register</em>'.
	 * @generated
	 */
	Register createRegister();

	/**
	 * Returns a new object of class '<em>Create</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Create</em>'.
	 * @generated
	 */
	Create createCreate();

	/**
	 * Returns a new object of class '<em>Delete</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Delete</em>'.
	 * @generated
	 */
	Delete createDelete();

	/**
	 * Returns a new object of class '<em>Add To Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Add To Resource</em>'.
	 * @generated
	 */
	AddToResource createAddToResource();

	/**
	 * Returns a new object of class '<em>Remove From Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Remove From Resource</em>'.
	 * @generated
	 */
	RemoveFromResource createRemoveFromResource();

	/**
	 * Returns a new object of class '<em>Value</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Value</em>'.
	 * @generated
	 */
	Value createValue();

	/**
	 * Returns a new object of class '<em>Set EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Set EAttribute</em>'.
	 * @generated
	 */
	SetEAttribute createSetEAttribute();

	/**
	 * Returns a new object of class '<em>Unset EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Unset EAttribute</em>'.
	 * @generated
	 */
	UnsetEAttribute createUnsetEAttribute();

	/**
	 * Returns a new object of class '<em>Set EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Set EReference</em>'.
	 * @generated
	 */
	SetEReference createSetEReference();

	/**
	 * Returns a new object of class '<em>Unset EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Unset EReference</em>'.
	 * @generated
	 */
	UnsetEReference createUnsetEReference();

	/**
	 * Returns a new object of class '<em>Add To EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Add To EAttribute</em>'.
	 * @generated
	 */
	AddToEAttribute createAddToEAttribute();

	/**
	 * Returns a new object of class '<em>Remove From EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Remove From EAttribute</em>'.
	 * @generated
	 */
	RemoveFromEAttribute createRemoveFromEAttribute();

	/**
	 * Returns a new object of class '<em>Move In EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Move In EAttribute</em>'.
	 * @generated
	 */
	MoveInEAttribute createMoveInEAttribute();

	/**
	 * Returns a new object of class '<em>Add To EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Add To EReference</em>'.
	 * @generated
	 */
	AddToEReference createAddToEReference();

	/**
	 * Returns a new object of class '<em>Remove From EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Remove From EReference</em>'.
	 * @generated
	 */
	RemoveFromEReference createRemoveFromEReference();

	/**
	 * Returns a new object of class '<em>Move In EReference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Move In EReference</em>'.
	 * @generated
	 */
	MoveInEReference createMoveInEReference();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	CBPPackage getCBPPackage();

} //CBPFactory
