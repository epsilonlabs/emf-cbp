/**
 */
package org.eclipse.epsilon.cbp.test.node;

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
 * @see org.eclipse.epsilon.cbp.test.node.NodeFactory
 * @model kind="package"
 * @generated
 */
public interface NodePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "node";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "node";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	NodePackage eINSTANCE = org.eclipse.epsilon.cbp.test.node.impl.NodePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl <em>Node</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.node.impl.NodeImpl
	 * @see org.eclipse.epsilon.cbp.test.node.impl.NodePackageImpl#getNode()
	 * @generated
	 */
	int NODE = 0;

	/**
	 * The feature id for the '<em><b>Deep</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__DEEP = 0;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__PARENT = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__NAME = 2;

	/**
	 * The feature id for the '<em><b>Def Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__DEF_NAME = 3;

	/**
	 * The feature id for the '<em><b>Values</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__VALUES = 4;

	/**
	 * The feature id for the '<em><b>List Values</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__LIST_VALUES = 5;

	/**
	 * The feature id for the '<em><b>Ref Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__REF_NODE = 6;

	/**
	 * The feature id for the '<em><b>Val Node</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__VAL_NODE = 7;

	/**
	 * The feature id for the '<em><b>Val Nodes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__VAL_NODES = 8;

	/**
	 * The feature id for the '<em><b>Ref Nodes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE__REF_NODES = 9;

	/**
	 * The number of structural features of the '<em>Node</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODE_FEATURE_COUNT = 10;


	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.node.Node <em>Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Node</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node
	 * @generated
	 */
	EClass getNode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.node.Node#getDeep <em>Deep</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deep</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getDeep()
	 * @see #getNode()
	 * @generated
	 */
	EAttribute getNode_Deep();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.epsilon.cbp.test.node.Node#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Parent</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getParent()
	 * @see #getNode()
	 * @generated
	 */
	EReference getNode_Parent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.node.Node#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getName()
	 * @see #getNode()
	 * @generated
	 */
	EAttribute getNode_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.node.Node#getDefName <em>Def Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Def Name</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getDefName()
	 * @see #getNode()
	 * @generated
	 */
	EAttribute getNode_DefName();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.epsilon.cbp.test.node.Node#getValues <em>Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Values</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getValues()
	 * @see #getNode()
	 * @generated
	 */
	EAttribute getNode_Values();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.epsilon.cbp.test.node.Node#getListValues <em>List Values</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>List Values</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getListValues()
	 * @see #getNode()
	 * @generated
	 */
	EAttribute getNode_ListValues();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.epsilon.cbp.test.node.Node#getRefNode <em>Ref Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Ref Node</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getRefNode()
	 * @see #getNode()
	 * @generated
	 */
	EReference getNode_RefNode();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.epsilon.cbp.test.node.Node#getValNode <em>Val Node</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Val Node</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getValNode()
	 * @see #getNode()
	 * @generated
	 */
	EReference getNode_ValNode();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.cbp.test.node.Node#getValNodes <em>Val Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Val Nodes</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getValNodes()
	 * @see #getNode()
	 * @generated
	 */
	EReference getNode_ValNodes();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.epsilon.cbp.test.node.Node#getRefNodes <em>Ref Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Ref Nodes</em>'.
	 * @see org.eclipse.epsilon.cbp.test.node.Node#getRefNodes()
	 * @see #getNode()
	 * @generated
	 */
	EReference getNode_RefNodes();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	NodeFactory getNodeFactory();

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
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl <em>Node</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.node.impl.NodeImpl
		 * @see org.eclipse.epsilon.cbp.test.node.impl.NodePackageImpl#getNode()
		 * @generated
		 */
		EClass NODE = eINSTANCE.getNode();

		/**
		 * The meta object literal for the '<em><b>Deep</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE__DEEP = eINSTANCE.getNode_Deep();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE__PARENT = eINSTANCE.getNode_Parent();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE__NAME = eINSTANCE.getNode_Name();

		/**
		 * The meta object literal for the '<em><b>Def Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE__DEF_NAME = eINSTANCE.getNode_DefName();

		/**
		 * The meta object literal for the '<em><b>Values</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE__VALUES = eINSTANCE.getNode_Values();

		/**
		 * The meta object literal for the '<em><b>List Values</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NODE__LIST_VALUES = eINSTANCE.getNode_ListValues();

		/**
		 * The meta object literal for the '<em><b>Ref Node</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE__REF_NODE = eINSTANCE.getNode_RefNode();

		/**
		 * The meta object literal for the '<em><b>Val Node</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE__VAL_NODE = eINSTANCE.getNode_ValNode();

		/**
		 * The meta object literal for the '<em><b>Val Nodes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE__VAL_NODES = eINSTANCE.getNode_ValNodes();

		/**
		 * The meta object literal for the '<em><b>Ref Nodes</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODE__REF_NODES = eINSTANCE.getNode_RefNodes();

	}

} //NodePackage
