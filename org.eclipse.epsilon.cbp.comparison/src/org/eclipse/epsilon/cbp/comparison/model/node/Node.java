/**
 */
package org.eclipse.epsilon.cbp.comparison.model.node;

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
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getID <em>ID</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getDeep <em>Deep</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getDefName <em>Def Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getValues <em>Values</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getListValues <em>List Values</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getRefNode <em>Ref Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getValNode <em>Val Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getValNodes <em>Val Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getRefNodes <em>Ref Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getUorderedValNodes <em>Uordered Val Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode()
 * @model
 * @generated
 */
public interface Node extends EObject {
        /**
         * Returns the value of the '<em><b>ID</b></em>' attribute.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>ID</em>' attribute isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>ID</em>' attribute.
         * @see #setID(String)
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_ID()
         * @model
         * @generated
         */
        String getID();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getID <em>ID</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @param value the new value of the '<em>ID</em>' attribute.
         * @see #getID()
         * @generated
         */
        void setID(String value);

        /**
         * Returns the value of the '<em><b>Deep</b></em>' attribute.
         * The default value is <code>"0"</code>.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Deep</em>' attribute isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Deep</em>' attribute.
         * @see #setDeep(Integer)
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_Deep()
         * @model default="0"
         * @generated
         */
        Integer getDeep();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getDeep <em>Deep</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @param value the new value of the '<em>Deep</em>' attribute.
         * @see #getDeep()
         * @generated
         */
        void setDeep(Integer value);

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
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_Name()
         * @model
         * @generated
         */
        String getName();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getName <em>Name</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @param value the new value of the '<em>Name</em>' attribute.
         * @see #getName()
         * @generated
         */
        void setName(String value);

        /**
         * Returns the value of the '<em><b>Def Name</b></em>' attribute.
         * The default value is <code>"Foo"</code>.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Def Name</em>' attribute isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Def Name</em>' attribute.
         * @see #setDefName(String)
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_DefName()
         * @model default="Foo"
         * @generated
         */
        String getDefName();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getDefName <em>Def Name</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @param value the new value of the '<em>Def Name</em>' attribute.
         * @see #getDefName()
         * @generated
         */
        void setDefName(String value);

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
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_Values()
         * @model
         * @generated
         */
        EList<Integer> getValues();

        /**
         * Returns the value of the '<em><b>List Values</b></em>' attribute list.
         * The list contents are of type {@link java.lang.Integer}.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>List Values</em>' attribute list isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>List Values</em>' attribute list.
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_ListValues()
         * @model unique="false"
         * @generated
         */
        EList<Integer> getListValues();

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
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_RefNode()
         * @model
         * @generated
         */
        Node getRefNode();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getRefNode <em>Ref Node</em>}' reference.
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
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_ValNode()
         * @model containment="true"
         * @generated
         */
        Node getValNode();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getValNode <em>Val Node</em>}' containment reference.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @param value the new value of the '<em>Val Node</em>' containment reference.
         * @see #getValNode()
         * @generated
         */
        void setValNode(Node value);

        /**
         * Returns the value of the '<em><b>Val Nodes</b></em>' containment reference list.
         * The list contents are of type {@link org.eclipse.epsilon.cbp.comparison.model.node.Node}.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Val Nodes</em>' containment reference list isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Val Nodes</em>' containment reference list.
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_ValNodes()
         * @model containment="true"
         * @generated
         */
        EList<Node> getValNodes();

        /**
         * Returns the value of the '<em><b>Ref Nodes</b></em>' reference list.
         * The list contents are of type {@link org.eclipse.epsilon.cbp.comparison.model.node.Node}.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Ref Nodes</em>' reference list isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Ref Nodes</em>' reference list.
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_RefNodes()
         * @model
         * @generated
         */
        EList<Node> getRefNodes();

        /**
         * Returns the value of the '<em><b>Uordered Val Nodes</b></em>' reference list.
         * The list contents are of type {@link org.eclipse.epsilon.cbp.comparison.model.node.Node}.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Uordered Val Nodes</em>' reference list isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Uordered Val Nodes</em>' reference list.
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_UorderedValNodes()
         * @model ordered="false"
         * @generated
         */
        EList<Node> getUorderedValNodes();

        /**
         * Returns the value of the '<em><b>Children</b></em>' containment reference list.
         * The list contents are of type {@link org.eclipse.epsilon.cbp.comparison.model.node.Node}.
         * It is bidirectional and its opposite is '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getParent <em>Parent</em>}'.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Children</em>' containment reference list.
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_Children()
         * @see org.eclipse.epsilon.cbp.comparison.model.node.Node#getParent
         * @model opposite="parent" containment="true"
         * @generated
         */
        EList<Node> getChildren();

        /**
         * Returns the value of the '<em><b>Parent</b></em>' container reference.
         * It is bidirectional and its opposite is '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getChildren <em>Children</em>}'.
         * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>Parent</em>' container reference isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
         * @return the value of the '<em>Parent</em>' container reference.
         * @see #setParent(Node)
         * @see org.eclipse.epsilon.cbp.comparison.model.node.NodePackage#getNode_Parent()
         * @see org.eclipse.epsilon.cbp.comparison.model.node.Node#getChildren
         * @model opposite="children" transient="false"
         * @generated
         */
        Node getParent();

        /**
         * Sets the value of the '{@link org.eclipse.epsilon.cbp.comparison.model.node.Node#getParent <em>Parent</em>}' container reference.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @param value the new value of the '<em>Parent</em>' container reference.
         * @see #getParent()
         * @generated
         */
        void setParent(Node value);

} // Node
