/**
 */
package org.eclipse.epsilon.cbp.test.node;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.Node#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.Node#getValues <em>Values</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.Node#getRefNode <em>Ref Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.Node#getValNode <em>Val Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.Node#getValNodes <em>Val Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.Node#getRefNodes <em>Ref Nodes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode()
 * @model
 * @generated
 */
public interface Node extends EObject {
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
	 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.node.Node#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Values</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Values</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Values</em>' attribute list.
	 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode_Values()
	 * @model
	 * @generated
	 */
	EList<Integer> getValues();

	/**
	 * Returns the value of the '<em><b>Ref Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ref Node</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref Node</em>' reference.
	 * @see #setRefNode(Node)
	 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode_RefNode()
	 * @model
	 * @generated
	 */
	Node getRefNode();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.node.Node#getRefNode <em>Ref Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ref Node</em>' reference.
	 * @see #getRefNode()
	 * @generated
	 */
	void setRefNode(Node value);

	/**
	 * Returns the value of the '<em><b>Val Node</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Val Node</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Val Node</em>' containment reference.
	 * @see #setValNode(Node)
	 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode_ValNode()
	 * @model containment="true"
	 * @generated
	 */
	Node getValNode();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.node.Node#getValNode <em>Val Node</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Val Node</em>' containment reference.
	 * @see #getValNode()
	 * @generated
	 */
	void setValNode(Node value);

	/**
	 * Returns the value of the '<em><b>Val Nodes</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.node.Node}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Val Nodes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Val Nodes</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode_ValNodes()
	 * @model containment="true"
	 * @generated
	 */
	EList<Node> getValNodes();

	/**
	 * Returns the value of the '<em><b>Ref Nodes</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.node.Node}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ref Nodes</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ref Nodes</em>' reference list.
	 * @see org.eclipse.epsilon.cbp.test.node.NodePackage#getNode_RefNodes()
	 * @model
	 * @generated
	 */
	EList<Node> getRefNodes();

} // Node
