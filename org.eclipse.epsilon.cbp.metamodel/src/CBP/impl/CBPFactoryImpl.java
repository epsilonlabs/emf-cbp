/**
 */
package CBP.impl;

import CBP.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CBPFactoryImpl extends EFactoryImpl implements CBPFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CBPFactory init() {
		try {
			CBPFactory theCBPFactory = (CBPFactory)EPackage.Registry.INSTANCE.getEFactory(CBPPackage.eNS_URI);
			if (theCBPFactory != null) {
				return theCBPFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new CBPFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CBPFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case CBPPackage.SESSION: return createSession();
			case CBPPackage.REGISTER: return createRegister();
			case CBPPackage.CREATE: return createCreate();
			case CBPPackage.DELETE: return createDelete();
			case CBPPackage.ADD_TO_RESOURCE: return createAddToResource();
			case CBPPackage.REMOVE_FROM_RESOURCE: return createRemoveFromResource();
			case CBPPackage.VALUE: return createValue();
			case CBPPackage.SET_EATTRIBUTE: return createSetEAttribute();
			case CBPPackage.UNSET_EATTRIBUTE: return createUnsetEAttribute();
			case CBPPackage.SET_EREFERENCE: return createSetEReference();
			case CBPPackage.UNSET_EREFERENCE: return createUnsetEReference();
			case CBPPackage.ADD_TO_EATTRIBUTE: return createAddToEAttribute();
			case CBPPackage.REMOVE_FROM_EATTRIBUTE: return createRemoveFromEAttribute();
			case CBPPackage.MOVE_IN_EATTRIBUTE: return createMoveInEAttribute();
			case CBPPackage.ADD_TO_EREFERENCE: return createAddToEReference();
			case CBPPackage.REMOVE_FROM_EREFERENCE: return createRemoveFromEReference();
			case CBPPackage.MOVE_IN_EREFERENCE: return createMoveInEReference();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Session createSession() {
		SessionImpl session = new SessionImpl();
		return session;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Register createRegister() {
		RegisterImpl register = new RegisterImpl();
		return register;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Create createCreate() {
		CreateImpl create = new CreateImpl();
		return create;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Delete createDelete() {
		DeleteImpl delete = new DeleteImpl();
		return delete;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AddToResource createAddToResource() {
		AddToResourceImpl addToResource = new AddToResourceImpl();
		return addToResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RemoveFromResource createRemoveFromResource() {
		RemoveFromResourceImpl removeFromResource = new RemoveFromResourceImpl();
		return removeFromResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Value createValue() {
		ValueImpl value = new ValueImpl();
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SetEAttribute createSetEAttribute() {
		SetEAttributeImpl setEAttribute = new SetEAttributeImpl();
		return setEAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UnsetEAttribute createUnsetEAttribute() {
		UnsetEAttributeImpl unsetEAttribute = new UnsetEAttributeImpl();
		return unsetEAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SetEReference createSetEReference() {
		SetEReferenceImpl setEReference = new SetEReferenceImpl();
		return setEReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UnsetEReference createUnsetEReference() {
		UnsetEReferenceImpl unsetEReference = new UnsetEReferenceImpl();
		return unsetEReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AddToEAttribute createAddToEAttribute() {
		AddToEAttributeImpl addToEAttribute = new AddToEAttributeImpl();
		return addToEAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RemoveFromEAttribute createRemoveFromEAttribute() {
		RemoveFromEAttributeImpl removeFromEAttribute = new RemoveFromEAttributeImpl();
		return removeFromEAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MoveInEAttribute createMoveInEAttribute() {
		MoveInEAttributeImpl moveInEAttribute = new MoveInEAttributeImpl();
		return moveInEAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AddToEReference createAddToEReference() {
		AddToEReferenceImpl addToEReference = new AddToEReferenceImpl();
		return addToEReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RemoveFromEReference createRemoveFromEReference() {
		RemoveFromEReferenceImpl removeFromEReference = new RemoveFromEReferenceImpl();
		return removeFromEReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MoveInEReference createMoveInEReference() {
		MoveInEReferenceImpl moveInEReference = new MoveInEReferenceImpl();
		return moveInEReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CBPPackage getCBPPackage() {
		return (CBPPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static CBPPackage getPackage() {
		return CBPPackage.eINSTANCE;
	}

} //CBPFactoryImpl
