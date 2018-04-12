/**
 */
package CBP.util;

import CBP.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see CBP.CBPPackage
 * @generated
 */
public class CBPAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static CBPPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CBPAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = CBPPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CBPSwitch<Adapter> modelSwitch =
		new CBPSwitch<Adapter>() {
			@Override
			public Adapter caseSession(Session object) {
				return createSessionAdapter();
			}
			@Override
			public Adapter caseRegister(Register object) {
				return createRegisterAdapter();
			}
			@Override
			public Adapter caseCreate(Create object) {
				return createCreateAdapter();
			}
			@Override
			public Adapter caseDelete(Delete object) {
				return createDeleteAdapter();
			}
			@Override
			public Adapter caseAddToResource(AddToResource object) {
				return createAddToResourceAdapter();
			}
			@Override
			public Adapter caseRemoveFromResource(RemoveFromResource object) {
				return createRemoveFromResourceAdapter();
			}
			@Override
			public Adapter caseValue(Value object) {
				return createValueAdapter();
			}
			@Override
			public Adapter caseSetEAttribute(SetEAttribute object) {
				return createSetEAttributeAdapter();
			}
			@Override
			public Adapter caseUnsetEAttribute(UnsetEAttribute object) {
				return createUnsetEAttributeAdapter();
			}
			@Override
			public Adapter caseSetEReference(SetEReference object) {
				return createSetEReferenceAdapter();
			}
			@Override
			public Adapter caseUnsetEReference(UnsetEReference object) {
				return createUnsetEReferenceAdapter();
			}
			@Override
			public Adapter caseAddToEAttribute(AddToEAttribute object) {
				return createAddToEAttributeAdapter();
			}
			@Override
			public Adapter caseRemoveFromEAttribute(RemoveFromEAttribute object) {
				return createRemoveFromEAttributeAdapter();
			}
			@Override
			public Adapter caseMoveInEAttribute(MoveInEAttribute object) {
				return createMoveInEAttributeAdapter();
			}
			@Override
			public Adapter caseAddToEReference(AddToEReference object) {
				return createAddToEReferenceAdapter();
			}
			@Override
			public Adapter caseRemoveFromEReference(RemoveFromEReference object) {
				return createRemoveFromEReferenceAdapter();
			}
			@Override
			public Adapter caseMoveInEReference(MoveInEReference object) {
				return createMoveInEReferenceAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link CBP.Session <em>Session</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.Session
	 * @generated
	 */
	public Adapter createSessionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.Register <em>Register</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.Register
	 * @generated
	 */
	public Adapter createRegisterAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.Create <em>Create</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.Create
	 * @generated
	 */
	public Adapter createCreateAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.Delete <em>Delete</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.Delete
	 * @generated
	 */
	public Adapter createDeleteAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.AddToResource <em>Add To Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.AddToResource
	 * @generated
	 */
	public Adapter createAddToResourceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.RemoveFromResource <em>Remove From Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.RemoveFromResource
	 * @generated
	 */
	public Adapter createRemoveFromResourceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.Value <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.Value
	 * @generated
	 */
	public Adapter createValueAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.SetEAttribute <em>Set EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.SetEAttribute
	 * @generated
	 */
	public Adapter createSetEAttributeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.UnsetEAttribute <em>Unset EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.UnsetEAttribute
	 * @generated
	 */
	public Adapter createUnsetEAttributeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.SetEReference <em>Set EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.SetEReference
	 * @generated
	 */
	public Adapter createSetEReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.UnsetEReference <em>Unset EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.UnsetEReference
	 * @generated
	 */
	public Adapter createUnsetEReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.AddToEAttribute <em>Add To EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.AddToEAttribute
	 * @generated
	 */
	public Adapter createAddToEAttributeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.RemoveFromEAttribute <em>Remove From EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.RemoveFromEAttribute
	 * @generated
	 */
	public Adapter createRemoveFromEAttributeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.MoveInEAttribute <em>Move In EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.MoveInEAttribute
	 * @generated
	 */
	public Adapter createMoveInEAttributeAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.AddToEReference <em>Add To EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.AddToEReference
	 * @generated
	 */
	public Adapter createAddToEReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.RemoveFromEReference <em>Remove From EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.RemoveFromEReference
	 * @generated
	 */
	public Adapter createRemoveFromEReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CBP.MoveInEReference <em>Move In EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CBP.MoveInEReference
	 * @generated
	 */
	public Adapter createMoveInEReferenceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //CBPAdapterFactory
