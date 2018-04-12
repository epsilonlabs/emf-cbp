/**
 */
package CBP;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see CBP.CBPFactory
 * @model kind="package"
 * @generated
 */
public interface CBPPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "CBP";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://github.com/epsilonlabs/emf-cbp/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "cbp";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CBPPackage eINSTANCE = CBP.impl.CBPPackageImpl.init();

	/**
	 * The meta object id for the '{@link CBP.impl.SessionImpl <em>Session</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.SessionImpl
	 * @see CBP.impl.CBPPackageImpl#getSession()
	 * @generated
	 */
	int SESSION = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SESSION__ID = 0;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SESSION__TIME = 1;

	/**
	 * The number of structural features of the '<em>Session</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SESSION_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link CBP.impl.RegisterImpl <em>Register</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.RegisterImpl
	 * @see CBP.impl.CBPPackageImpl#getRegister()
	 * @generated
	 */
	int REGISTER = 1;

	/**
	 * The feature id for the '<em><b>Epackage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGISTER__EPACKAGE = 0;

	/**
	 * The number of structural features of the '<em>Register</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REGISTER_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link CBP.impl.CreateImpl <em>Create</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.CreateImpl
	 * @see CBP.impl.CBPPackageImpl#getCreate()
	 * @generated
	 */
	int CREATE = 2;

	/**
	 * The feature id for the '<em><b>Eclass</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE__ECLASS = 0;

	/**
	 * The feature id for the '<em><b>Epackage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE__EPACKAGE = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE__ID = 2;

	/**
	 * The number of structural features of the '<em>Create</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CREATE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link CBP.impl.DeleteImpl <em>Delete</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.DeleteImpl
	 * @see CBP.impl.CBPPackageImpl#getDelete()
	 * @generated
	 */
	int DELETE = 3;

	/**
	 * The feature id for the '<em><b>Eclass</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE__ECLASS = 0;

	/**
	 * The feature id for the '<em><b>Epackage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE__EPACKAGE = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE__ID = 2;

	/**
	 * The number of structural features of the '<em>Delete</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link CBP.impl.AddToResourceImpl <em>Add To Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.AddToResourceImpl
	 * @see CBP.impl.CBPPackageImpl#getAddToResource()
	 * @generated
	 */
	int ADD_TO_RESOURCE = 4;

	/**
	 * The feature id for the '<em><b>Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_RESOURCE__POSITION = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_RESOURCE__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Add To Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_RESOURCE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link CBP.impl.RemoveFromResourceImpl <em>Remove From Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.RemoveFromResourceImpl
	 * @see CBP.impl.CBPPackageImpl#getRemoveFromResource()
	 * @generated
	 */
	int REMOVE_FROM_RESOURCE = 5;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_RESOURCE__VALUE = 0;

	/**
	 * The number of structural features of the '<em>Remove From Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_RESOURCE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link CBP.impl.ValueImpl <em>Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.ValueImpl
	 * @see CBP.impl.CBPPackageImpl#getValue()
	 * @generated
	 */
	int VALUE = 6;

	/**
	 * The feature id for the '<em><b>Literal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE__LITERAL = 0;

	/**
	 * The feature id for the '<em><b>Eobject</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE__EOBJECT = 1;

	/**
	 * The number of structural features of the '<em>Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link CBP.impl.SetEAttributeImpl <em>Set EAttribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.SetEAttributeImpl
	 * @see CBP.impl.CBPPackageImpl#getSetEAttribute()
	 * @generated
	 */
	int SET_EATTRIBUTE = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EATTRIBUTE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EATTRIBUTE__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Set EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EATTRIBUTE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link CBP.impl.UnsetEAttributeImpl <em>Unset EAttribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.UnsetEAttributeImpl
	 * @see CBP.impl.CBPPackageImpl#getUnsetEAttribute()
	 * @generated
	 */
	int UNSET_EATTRIBUTE = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNSET_EATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNSET_EATTRIBUTE__TARGET = 1;

	/**
	 * The number of structural features of the '<em>Unset EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNSET_EATTRIBUTE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link CBP.impl.SetEReferenceImpl <em>Set EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.SetEReferenceImpl
	 * @see CBP.impl.CBPPackageImpl#getSetEReference()
	 * @generated
	 */
	int SET_EREFERENCE = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EREFERENCE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EREFERENCE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EREFERENCE__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Set EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SET_EREFERENCE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link CBP.impl.UnsetEReferenceImpl <em>Unset EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.UnsetEReferenceImpl
	 * @see CBP.impl.CBPPackageImpl#getUnsetEReference()
	 * @generated
	 */
	int UNSET_EREFERENCE = 10;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNSET_EREFERENCE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNSET_EREFERENCE__TARGET = 1;

	/**
	 * The number of structural features of the '<em>Unset EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNSET_EREFERENCE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link CBP.impl.AddToEAttributeImpl <em>Add To EAttribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.AddToEAttributeImpl
	 * @see CBP.impl.CBPPackageImpl#getAddToEAttribute()
	 * @generated
	 */
	int ADD_TO_EATTRIBUTE = 11;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EATTRIBUTE__POSITION = 1;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EATTRIBUTE__TARGET = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EATTRIBUTE__VALUE = 3;

	/**
	 * The number of structural features of the '<em>Add To EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EATTRIBUTE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link CBP.impl.RemoveFromEAttributeImpl <em>Remove From EAttribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.RemoveFromEAttributeImpl
	 * @see CBP.impl.CBPPackageImpl#getRemoveFromEAttribute()
	 * @generated
	 */
	int REMOVE_FROM_EATTRIBUTE = 12;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EATTRIBUTE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EATTRIBUTE__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Remove From EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EATTRIBUTE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link CBP.impl.MoveInEAttributeImpl <em>Move In EAttribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.MoveInEAttributeImpl
	 * @see CBP.impl.CBPPackageImpl#getMoveInEAttribute()
	 * @generated
	 */
	int MOVE_IN_EATTRIBUTE = 13;

	/**
	 * The feature id for the '<em><b>From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EATTRIBUTE__FROM = 0;

	/**
	 * The feature id for the '<em><b>To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EATTRIBUTE__TO = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EATTRIBUTE__NAME = 2;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EATTRIBUTE__TARGET = 3;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EATTRIBUTE__VALUE = 4;

	/**
	 * The number of structural features of the '<em>Move In EAttribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EATTRIBUTE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link CBP.impl.AddToEReferenceImpl <em>Add To EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.AddToEReferenceImpl
	 * @see CBP.impl.CBPPackageImpl#getAddToEReference()
	 * @generated
	 */
	int ADD_TO_EREFERENCE = 14;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EREFERENCE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EREFERENCE__POSITION = 1;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EREFERENCE__TARGET = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EREFERENCE__VALUE = 3;

	/**
	 * The number of structural features of the '<em>Add To EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADD_TO_EREFERENCE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link CBP.impl.RemoveFromEReferenceImpl <em>Remove From EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.RemoveFromEReferenceImpl
	 * @see CBP.impl.CBPPackageImpl#getRemoveFromEReference()
	 * @generated
	 */
	int REMOVE_FROM_EREFERENCE = 15;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EREFERENCE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EREFERENCE__TARGET = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EREFERENCE__VALUE = 2;

	/**
	 * The number of structural features of the '<em>Remove From EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REMOVE_FROM_EREFERENCE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link CBP.impl.MoveInEReferenceImpl <em>Move In EReference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see CBP.impl.MoveInEReferenceImpl
	 * @see CBP.impl.CBPPackageImpl#getMoveInEReference()
	 * @generated
	 */
	int MOVE_IN_EREFERENCE = 16;

	/**
	 * The feature id for the '<em><b>From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EREFERENCE__FROM = 0;

	/**
	 * The feature id for the '<em><b>To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EREFERENCE__TO = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EREFERENCE__NAME = 2;

	/**
	 * The feature id for the '<em><b>Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EREFERENCE__TARGET = 3;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EREFERENCE__VALUE = 4;

	/**
	 * The number of structural features of the '<em>Move In EReference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MOVE_IN_EREFERENCE_FEATURE_COUNT = 5;


	/**
	 * Returns the meta object for class '{@link CBP.Session <em>Session</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Session</em>'.
	 * @see CBP.Session
	 * @generated
	 */
	EClass getSession();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Session#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see CBP.Session#getId()
	 * @see #getSession()
	 * @generated
	 */
	EAttribute getSession_Id();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Session#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see CBP.Session#getTime()
	 * @see #getSession()
	 * @generated
	 */
	EAttribute getSession_Time();

	/**
	 * Returns the meta object for class '{@link CBP.Register <em>Register</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Register</em>'.
	 * @see CBP.Register
	 * @generated
	 */
	EClass getRegister();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Register#getEpackage <em>Epackage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Epackage</em>'.
	 * @see CBP.Register#getEpackage()
	 * @see #getRegister()
	 * @generated
	 */
	EAttribute getRegister_Epackage();

	/**
	 * Returns the meta object for class '{@link CBP.Create <em>Create</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Create</em>'.
	 * @see CBP.Create
	 * @generated
	 */
	EClass getCreate();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Create#getEclass <em>Eclass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Eclass</em>'.
	 * @see CBP.Create#getEclass()
	 * @see #getCreate()
	 * @generated
	 */
	EAttribute getCreate_Eclass();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Create#getEpackage <em>Epackage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Epackage</em>'.
	 * @see CBP.Create#getEpackage()
	 * @see #getCreate()
	 * @generated
	 */
	EAttribute getCreate_Epackage();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Create#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see CBP.Create#getId()
	 * @see #getCreate()
	 * @generated
	 */
	EAttribute getCreate_Id();

	/**
	 * Returns the meta object for class '{@link CBP.Delete <em>Delete</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Delete</em>'.
	 * @see CBP.Delete
	 * @generated
	 */
	EClass getDelete();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Delete#getEclass <em>Eclass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Eclass</em>'.
	 * @see CBP.Delete#getEclass()
	 * @see #getDelete()
	 * @generated
	 */
	EAttribute getDelete_Eclass();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Delete#getEpackage <em>Epackage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Epackage</em>'.
	 * @see CBP.Delete#getEpackage()
	 * @see #getDelete()
	 * @generated
	 */
	EAttribute getDelete_Epackage();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Delete#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see CBP.Delete#getId()
	 * @see #getDelete()
	 * @generated
	 */
	EAttribute getDelete_Id();

	/**
	 * Returns the meta object for class '{@link CBP.AddToResource <em>Add To Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Add To Resource</em>'.
	 * @see CBP.AddToResource
	 * @generated
	 */
	EClass getAddToResource();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToResource#getPosition <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position</em>'.
	 * @see CBP.AddToResource#getPosition()
	 * @see #getAddToResource()
	 * @generated
	 */
	EAttribute getAddToResource_Position();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.AddToResource#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.AddToResource#getValue()
	 * @see #getAddToResource()
	 * @generated
	 */
	EReference getAddToResource_Value();

	/**
	 * Returns the meta object for class '{@link CBP.RemoveFromResource <em>Remove From Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Remove From Resource</em>'.
	 * @see CBP.RemoveFromResource
	 * @generated
	 */
	EClass getRemoveFromResource();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.RemoveFromResource#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.RemoveFromResource#getValue()
	 * @see #getRemoveFromResource()
	 * @generated
	 */
	EReference getRemoveFromResource_Value();

	/**
	 * Returns the meta object for class '{@link CBP.Value <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Value</em>'.
	 * @see CBP.Value
	 * @generated
	 */
	EClass getValue();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Value#getLiteral <em>Literal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Literal</em>'.
	 * @see CBP.Value#getLiteral()
	 * @see #getValue()
	 * @generated
	 */
	EAttribute getValue_Literal();

	/**
	 * Returns the meta object for the attribute '{@link CBP.Value#getEobject <em>Eobject</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Eobject</em>'.
	 * @see CBP.Value#getEobject()
	 * @see #getValue()
	 * @generated
	 */
	EAttribute getValue_Eobject();

	/**
	 * Returns the meta object for class '{@link CBP.SetEAttribute <em>Set EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Set EAttribute</em>'.
	 * @see CBP.SetEAttribute
	 * @generated
	 */
	EClass getSetEAttribute();

	/**
	 * Returns the meta object for the attribute '{@link CBP.SetEAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.SetEAttribute#getName()
	 * @see #getSetEAttribute()
	 * @generated
	 */
	EAttribute getSetEAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.SetEAttribute#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.SetEAttribute#getTarget()
	 * @see #getSetEAttribute()
	 * @generated
	 */
	EAttribute getSetEAttribute_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.SetEAttribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.SetEAttribute#getValue()
	 * @see #getSetEAttribute()
	 * @generated
	 */
	EReference getSetEAttribute_Value();

	/**
	 * Returns the meta object for class '{@link CBP.UnsetEAttribute <em>Unset EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Unset EAttribute</em>'.
	 * @see CBP.UnsetEAttribute
	 * @generated
	 */
	EClass getUnsetEAttribute();

	/**
	 * Returns the meta object for the attribute '{@link CBP.UnsetEAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.UnsetEAttribute#getName()
	 * @see #getUnsetEAttribute()
	 * @generated
	 */
	EAttribute getUnsetEAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.UnsetEAttribute#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.UnsetEAttribute#getTarget()
	 * @see #getUnsetEAttribute()
	 * @generated
	 */
	EAttribute getUnsetEAttribute_Target();

	/**
	 * Returns the meta object for class '{@link CBP.SetEReference <em>Set EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Set EReference</em>'.
	 * @see CBP.SetEReference
	 * @generated
	 */
	EClass getSetEReference();

	/**
	 * Returns the meta object for the attribute '{@link CBP.SetEReference#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.SetEReference#getName()
	 * @see #getSetEReference()
	 * @generated
	 */
	EAttribute getSetEReference_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.SetEReference#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.SetEReference#getTarget()
	 * @see #getSetEReference()
	 * @generated
	 */
	EAttribute getSetEReference_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.SetEReference#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.SetEReference#getValue()
	 * @see #getSetEReference()
	 * @generated
	 */
	EReference getSetEReference_Value();

	/**
	 * Returns the meta object for class '{@link CBP.UnsetEReference <em>Unset EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Unset EReference</em>'.
	 * @see CBP.UnsetEReference
	 * @generated
	 */
	EClass getUnsetEReference();

	/**
	 * Returns the meta object for the attribute '{@link CBP.UnsetEReference#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.UnsetEReference#getName()
	 * @see #getUnsetEReference()
	 * @generated
	 */
	EAttribute getUnsetEReference_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.UnsetEReference#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.UnsetEReference#getTarget()
	 * @see #getUnsetEReference()
	 * @generated
	 */
	EAttribute getUnsetEReference_Target();

	/**
	 * Returns the meta object for class '{@link CBP.AddToEAttribute <em>Add To EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Add To EAttribute</em>'.
	 * @see CBP.AddToEAttribute
	 * @generated
	 */
	EClass getAddToEAttribute();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToEAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.AddToEAttribute#getName()
	 * @see #getAddToEAttribute()
	 * @generated
	 */
	EAttribute getAddToEAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToEAttribute#getPosition <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position</em>'.
	 * @see CBP.AddToEAttribute#getPosition()
	 * @see #getAddToEAttribute()
	 * @generated
	 */
	EAttribute getAddToEAttribute_Position();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToEAttribute#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.AddToEAttribute#getTarget()
	 * @see #getAddToEAttribute()
	 * @generated
	 */
	EAttribute getAddToEAttribute_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.AddToEAttribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.AddToEAttribute#getValue()
	 * @see #getAddToEAttribute()
	 * @generated
	 */
	EReference getAddToEAttribute_Value();

	/**
	 * Returns the meta object for class '{@link CBP.RemoveFromEAttribute <em>Remove From EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Remove From EAttribute</em>'.
	 * @see CBP.RemoveFromEAttribute
	 * @generated
	 */
	EClass getRemoveFromEAttribute();

	/**
	 * Returns the meta object for the attribute '{@link CBP.RemoveFromEAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.RemoveFromEAttribute#getName()
	 * @see #getRemoveFromEAttribute()
	 * @generated
	 */
	EAttribute getRemoveFromEAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.RemoveFromEAttribute#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.RemoveFromEAttribute#getTarget()
	 * @see #getRemoveFromEAttribute()
	 * @generated
	 */
	EAttribute getRemoveFromEAttribute_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.RemoveFromEAttribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.RemoveFromEAttribute#getValue()
	 * @see #getRemoveFromEAttribute()
	 * @generated
	 */
	EReference getRemoveFromEAttribute_Value();

	/**
	 * Returns the meta object for class '{@link CBP.MoveInEAttribute <em>Move In EAttribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Move In EAttribute</em>'.
	 * @see CBP.MoveInEAttribute
	 * @generated
	 */
	EClass getMoveInEAttribute();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEAttribute#getFrom <em>From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>From</em>'.
	 * @see CBP.MoveInEAttribute#getFrom()
	 * @see #getMoveInEAttribute()
	 * @generated
	 */
	EAttribute getMoveInEAttribute_From();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEAttribute#getTo <em>To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>To</em>'.
	 * @see CBP.MoveInEAttribute#getTo()
	 * @see #getMoveInEAttribute()
	 * @generated
	 */
	EAttribute getMoveInEAttribute_To();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.MoveInEAttribute#getName()
	 * @see #getMoveInEAttribute()
	 * @generated
	 */
	EAttribute getMoveInEAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEAttribute#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.MoveInEAttribute#getTarget()
	 * @see #getMoveInEAttribute()
	 * @generated
	 */
	EAttribute getMoveInEAttribute_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.MoveInEAttribute#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.MoveInEAttribute#getValue()
	 * @see #getMoveInEAttribute()
	 * @generated
	 */
	EReference getMoveInEAttribute_Value();

	/**
	 * Returns the meta object for class '{@link CBP.AddToEReference <em>Add To EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Add To EReference</em>'.
	 * @see CBP.AddToEReference
	 * @generated
	 */
	EClass getAddToEReference();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToEReference#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.AddToEReference#getName()
	 * @see #getAddToEReference()
	 * @generated
	 */
	EAttribute getAddToEReference_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToEReference#getPosition <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position</em>'.
	 * @see CBP.AddToEReference#getPosition()
	 * @see #getAddToEReference()
	 * @generated
	 */
	EAttribute getAddToEReference_Position();

	/**
	 * Returns the meta object for the attribute '{@link CBP.AddToEReference#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.AddToEReference#getTarget()
	 * @see #getAddToEReference()
	 * @generated
	 */
	EAttribute getAddToEReference_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.AddToEReference#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.AddToEReference#getValue()
	 * @see #getAddToEReference()
	 * @generated
	 */
	EReference getAddToEReference_Value();

	/**
	 * Returns the meta object for class '{@link CBP.RemoveFromEReference <em>Remove From EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Remove From EReference</em>'.
	 * @see CBP.RemoveFromEReference
	 * @generated
	 */
	EClass getRemoveFromEReference();

	/**
	 * Returns the meta object for the attribute '{@link CBP.RemoveFromEReference#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.RemoveFromEReference#getName()
	 * @see #getRemoveFromEReference()
	 * @generated
	 */
	EAttribute getRemoveFromEReference_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.RemoveFromEReference#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.RemoveFromEReference#getTarget()
	 * @see #getRemoveFromEReference()
	 * @generated
	 */
	EAttribute getRemoveFromEReference_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.RemoveFromEReference#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.RemoveFromEReference#getValue()
	 * @see #getRemoveFromEReference()
	 * @generated
	 */
	EReference getRemoveFromEReference_Value();

	/**
	 * Returns the meta object for class '{@link CBP.MoveInEReference <em>Move In EReference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Move In EReference</em>'.
	 * @see CBP.MoveInEReference
	 * @generated
	 */
	EClass getMoveInEReference();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEReference#getFrom <em>From</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>From</em>'.
	 * @see CBP.MoveInEReference#getFrom()
	 * @see #getMoveInEReference()
	 * @generated
	 */
	EAttribute getMoveInEReference_From();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEReference#getTo <em>To</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>To</em>'.
	 * @see CBP.MoveInEReference#getTo()
	 * @see #getMoveInEReference()
	 * @generated
	 */
	EAttribute getMoveInEReference_To();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEReference#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see CBP.MoveInEReference#getName()
	 * @see #getMoveInEReference()
	 * @generated
	 */
	EAttribute getMoveInEReference_Name();

	/**
	 * Returns the meta object for the attribute '{@link CBP.MoveInEReference#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target</em>'.
	 * @see CBP.MoveInEReference#getTarget()
	 * @see #getMoveInEReference()
	 * @generated
	 */
	EAttribute getMoveInEReference_Target();

	/**
	 * Returns the meta object for the containment reference '{@link CBP.MoveInEReference#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see CBP.MoveInEReference#getValue()
	 * @see #getMoveInEReference()
	 * @generated
	 */
	EReference getMoveInEReference_Value();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CBPFactory getCBPFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link CBP.impl.SessionImpl <em>Session</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.SessionImpl
		 * @see CBP.impl.CBPPackageImpl#getSession()
		 * @generated
		 */
		EClass SESSION = eINSTANCE.getSession();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SESSION__ID = eINSTANCE.getSession_Id();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SESSION__TIME = eINSTANCE.getSession_Time();

		/**
		 * The meta object literal for the '{@link CBP.impl.RegisterImpl <em>Register</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.RegisterImpl
		 * @see CBP.impl.CBPPackageImpl#getRegister()
		 * @generated
		 */
		EClass REGISTER = eINSTANCE.getRegister();

		/**
		 * The meta object literal for the '<em><b>Epackage</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REGISTER__EPACKAGE = eINSTANCE.getRegister_Epackage();

		/**
		 * The meta object literal for the '{@link CBP.impl.CreateImpl <em>Create</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.CreateImpl
		 * @see CBP.impl.CBPPackageImpl#getCreate()
		 * @generated
		 */
		EClass CREATE = eINSTANCE.getCreate();

		/**
		 * The meta object literal for the '<em><b>Eclass</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CREATE__ECLASS = eINSTANCE.getCreate_Eclass();

		/**
		 * The meta object literal for the '<em><b>Epackage</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CREATE__EPACKAGE = eINSTANCE.getCreate_Epackage();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CREATE__ID = eINSTANCE.getCreate_Id();

		/**
		 * The meta object literal for the '{@link CBP.impl.DeleteImpl <em>Delete</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.DeleteImpl
		 * @see CBP.impl.CBPPackageImpl#getDelete()
		 * @generated
		 */
		EClass DELETE = eINSTANCE.getDelete();

		/**
		 * The meta object literal for the '<em><b>Eclass</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETE__ECLASS = eINSTANCE.getDelete_Eclass();

		/**
		 * The meta object literal for the '<em><b>Epackage</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETE__EPACKAGE = eINSTANCE.getDelete_Epackage();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETE__ID = eINSTANCE.getDelete_Id();

		/**
		 * The meta object literal for the '{@link CBP.impl.AddToResourceImpl <em>Add To Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.AddToResourceImpl
		 * @see CBP.impl.CBPPackageImpl#getAddToResource()
		 * @generated
		 */
		EClass ADD_TO_RESOURCE = eINSTANCE.getAddToResource();

		/**
		 * The meta object literal for the '<em><b>Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_RESOURCE__POSITION = eINSTANCE.getAddToResource_Position();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADD_TO_RESOURCE__VALUE = eINSTANCE.getAddToResource_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.RemoveFromResourceImpl <em>Remove From Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.RemoveFromResourceImpl
		 * @see CBP.impl.CBPPackageImpl#getRemoveFromResource()
		 * @generated
		 */
		EClass REMOVE_FROM_RESOURCE = eINSTANCE.getRemoveFromResource();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REMOVE_FROM_RESOURCE__VALUE = eINSTANCE.getRemoveFromResource_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.ValueImpl <em>Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.ValueImpl
		 * @see CBP.impl.CBPPackageImpl#getValue()
		 * @generated
		 */
		EClass VALUE = eINSTANCE.getValue();

		/**
		 * The meta object literal for the '<em><b>Literal</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE__LITERAL = eINSTANCE.getValue_Literal();

		/**
		 * The meta object literal for the '<em><b>Eobject</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE__EOBJECT = eINSTANCE.getValue_Eobject();

		/**
		 * The meta object literal for the '{@link CBP.impl.SetEAttributeImpl <em>Set EAttribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.SetEAttributeImpl
		 * @see CBP.impl.CBPPackageImpl#getSetEAttribute()
		 * @generated
		 */
		EClass SET_EATTRIBUTE = eINSTANCE.getSetEAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SET_EATTRIBUTE__NAME = eINSTANCE.getSetEAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SET_EATTRIBUTE__TARGET = eINSTANCE.getSetEAttribute_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SET_EATTRIBUTE__VALUE = eINSTANCE.getSetEAttribute_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.UnsetEAttributeImpl <em>Unset EAttribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.UnsetEAttributeImpl
		 * @see CBP.impl.CBPPackageImpl#getUnsetEAttribute()
		 * @generated
		 */
		EClass UNSET_EATTRIBUTE = eINSTANCE.getUnsetEAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNSET_EATTRIBUTE__NAME = eINSTANCE.getUnsetEAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNSET_EATTRIBUTE__TARGET = eINSTANCE.getUnsetEAttribute_Target();

		/**
		 * The meta object literal for the '{@link CBP.impl.SetEReferenceImpl <em>Set EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.SetEReferenceImpl
		 * @see CBP.impl.CBPPackageImpl#getSetEReference()
		 * @generated
		 */
		EClass SET_EREFERENCE = eINSTANCE.getSetEReference();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SET_EREFERENCE__NAME = eINSTANCE.getSetEReference_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SET_EREFERENCE__TARGET = eINSTANCE.getSetEReference_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SET_EREFERENCE__VALUE = eINSTANCE.getSetEReference_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.UnsetEReferenceImpl <em>Unset EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.UnsetEReferenceImpl
		 * @see CBP.impl.CBPPackageImpl#getUnsetEReference()
		 * @generated
		 */
		EClass UNSET_EREFERENCE = eINSTANCE.getUnsetEReference();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNSET_EREFERENCE__NAME = eINSTANCE.getUnsetEReference_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNSET_EREFERENCE__TARGET = eINSTANCE.getUnsetEReference_Target();

		/**
		 * The meta object literal for the '{@link CBP.impl.AddToEAttributeImpl <em>Add To EAttribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.AddToEAttributeImpl
		 * @see CBP.impl.CBPPackageImpl#getAddToEAttribute()
		 * @generated
		 */
		EClass ADD_TO_EATTRIBUTE = eINSTANCE.getAddToEAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_EATTRIBUTE__NAME = eINSTANCE.getAddToEAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_EATTRIBUTE__POSITION = eINSTANCE.getAddToEAttribute_Position();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_EATTRIBUTE__TARGET = eINSTANCE.getAddToEAttribute_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADD_TO_EATTRIBUTE__VALUE = eINSTANCE.getAddToEAttribute_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.RemoveFromEAttributeImpl <em>Remove From EAttribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.RemoveFromEAttributeImpl
		 * @see CBP.impl.CBPPackageImpl#getRemoveFromEAttribute()
		 * @generated
		 */
		EClass REMOVE_FROM_EATTRIBUTE = eINSTANCE.getRemoveFromEAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REMOVE_FROM_EATTRIBUTE__NAME = eINSTANCE.getRemoveFromEAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REMOVE_FROM_EATTRIBUTE__TARGET = eINSTANCE.getRemoveFromEAttribute_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REMOVE_FROM_EATTRIBUTE__VALUE = eINSTANCE.getRemoveFromEAttribute_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.MoveInEAttributeImpl <em>Move In EAttribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.MoveInEAttributeImpl
		 * @see CBP.impl.CBPPackageImpl#getMoveInEAttribute()
		 * @generated
		 */
		EClass MOVE_IN_EATTRIBUTE = eINSTANCE.getMoveInEAttribute();

		/**
		 * The meta object literal for the '<em><b>From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EATTRIBUTE__FROM = eINSTANCE.getMoveInEAttribute_From();

		/**
		 * The meta object literal for the '<em><b>To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EATTRIBUTE__TO = eINSTANCE.getMoveInEAttribute_To();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EATTRIBUTE__NAME = eINSTANCE.getMoveInEAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EATTRIBUTE__TARGET = eINSTANCE.getMoveInEAttribute_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOVE_IN_EATTRIBUTE__VALUE = eINSTANCE.getMoveInEAttribute_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.AddToEReferenceImpl <em>Add To EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.AddToEReferenceImpl
		 * @see CBP.impl.CBPPackageImpl#getAddToEReference()
		 * @generated
		 */
		EClass ADD_TO_EREFERENCE = eINSTANCE.getAddToEReference();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_EREFERENCE__NAME = eINSTANCE.getAddToEReference_Name();

		/**
		 * The meta object literal for the '<em><b>Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_EREFERENCE__POSITION = eINSTANCE.getAddToEReference_Position();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADD_TO_EREFERENCE__TARGET = eINSTANCE.getAddToEReference_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADD_TO_EREFERENCE__VALUE = eINSTANCE.getAddToEReference_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.RemoveFromEReferenceImpl <em>Remove From EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.RemoveFromEReferenceImpl
		 * @see CBP.impl.CBPPackageImpl#getRemoveFromEReference()
		 * @generated
		 */
		EClass REMOVE_FROM_EREFERENCE = eINSTANCE.getRemoveFromEReference();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REMOVE_FROM_EREFERENCE__NAME = eINSTANCE.getRemoveFromEReference_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REMOVE_FROM_EREFERENCE__TARGET = eINSTANCE.getRemoveFromEReference_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REMOVE_FROM_EREFERENCE__VALUE = eINSTANCE.getRemoveFromEReference_Value();

		/**
		 * The meta object literal for the '{@link CBP.impl.MoveInEReferenceImpl <em>Move In EReference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see CBP.impl.MoveInEReferenceImpl
		 * @see CBP.impl.CBPPackageImpl#getMoveInEReference()
		 * @generated
		 */
		EClass MOVE_IN_EREFERENCE = eINSTANCE.getMoveInEReference();

		/**
		 * The meta object literal for the '<em><b>From</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EREFERENCE__FROM = eINSTANCE.getMoveInEReference_From();

		/**
		 * The meta object literal for the '<em><b>To</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EREFERENCE__TO = eINSTANCE.getMoveInEReference_To();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EREFERENCE__NAME = eINSTANCE.getMoveInEReference_Name();

		/**
		 * The meta object literal for the '<em><b>Target</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MOVE_IN_EREFERENCE__TARGET = eINSTANCE.getMoveInEReference_Target();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MOVE_IN_EREFERENCE__VALUE = eINSTANCE.getMoveInEReference_Value();

	}

} //CBPPackage
