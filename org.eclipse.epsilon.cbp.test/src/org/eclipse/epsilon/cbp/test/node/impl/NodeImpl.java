/**
 */
package org.eclipse.epsilon.cbp.test.node.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.epsilon.cbp.test.node.Node;
import org.eclipse.epsilon.cbp.test.node.NodePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl#getValues <em>Values</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl#getRefNode <em>Ref Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl#getValNode <em>Val Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl#getValNodes <em>Val Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.node.impl.NodeImpl#getRefNodes <em>Ref Nodes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NodeImpl extends EObjectImpl implements Node {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getValues() <em>Values</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValues()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> values;

	/**
	 * The cached value of the '{@link #getRefNode() <em>Ref Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefNode()
	 * @generated
	 * @ordered
	 */
	protected Node refNode;

	/**
	 * The cached value of the '{@link #getValNode() <em>Val Node</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValNode()
	 * @generated
	 * @ordered
	 */
	protected Node valNode;

	/**
	 * The cached value of the '{@link #getValNodes() <em>Val Nodes</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<Node> valNodes;

	/**
	 * The cached value of the '{@link #getRefNodes() <em>Ref Nodes</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<Node> refNodes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NodePackage.Literals.NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getValues() {
		if (values == null) {
			values = new EDataTypeUniqueEList<Integer>(Integer.class, this, NodePackage.NODE__VALUES);
		}
		return values;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Node getRefNode() {
		if (refNode != null && refNode.eIsProxy()) {
			InternalEObject oldRefNode = (InternalEObject)refNode;
			refNode = (Node)eResolveProxy(oldRefNode);
			if (refNode != oldRefNode) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, NodePackage.NODE__REF_NODE, oldRefNode, refNode));
			}
		}
		return refNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Node basicGetRefNode() {
		return refNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefNode(Node newRefNode) {
		Node oldRefNode = refNode;
		refNode = newRefNode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__REF_NODE, oldRefNode, refNode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Node getValNode() {
		return valNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetValNode(Node newValNode, NotificationChain msgs) {
		Node oldValNode = valNode;
		valNode = newValNode;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NodePackage.NODE__VAL_NODE, oldValNode, newValNode);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValNode(Node newValNode) {
		if (newValNode != valNode) {
			NotificationChain msgs = null;
			if (valNode != null)
				msgs = ((InternalEObject)valNode).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NodePackage.NODE__VAL_NODE, null, msgs);
			if (newValNode != null)
				msgs = ((InternalEObject)newValNode).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NodePackage.NODE__VAL_NODE, null, msgs);
			msgs = basicSetValNode(newValNode, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__VAL_NODE, newValNode, newValNode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Node> getValNodes() {
		if (valNodes == null) {
			valNodes = new EObjectContainmentEList<Node>(Node.class, this, NodePackage.NODE__VAL_NODES);
		}
		return valNodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Node> getRefNodes() {
		if (refNodes == null) {
			refNodes = new EObjectResolvingEList<Node>(Node.class, this, NodePackage.NODE__REF_NODES);
		}
		return refNodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case NodePackage.NODE__VAL_NODE:
				return basicSetValNode(null, msgs);
			case NodePackage.NODE__VAL_NODES:
				return ((InternalEList<?>)getValNodes()).basicRemove(otherEnd, msgs);
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
			case NodePackage.NODE__NAME:
				return getName();
			case NodePackage.NODE__VALUES:
				return getValues();
			case NodePackage.NODE__REF_NODE:
				if (resolve) return getRefNode();
				return basicGetRefNode();
			case NodePackage.NODE__VAL_NODE:
				return getValNode();
			case NodePackage.NODE__VAL_NODES:
				return getValNodes();
			case NodePackage.NODE__REF_NODES:
				return getRefNodes();
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
			case NodePackage.NODE__NAME:
				setName((String)newValue);
				return;
			case NodePackage.NODE__VALUES:
				getValues().clear();
				getValues().addAll((Collection<? extends Integer>)newValue);
				return;
			case NodePackage.NODE__REF_NODE:
				setRefNode((Node)newValue);
				return;
			case NodePackage.NODE__VAL_NODE:
				setValNode((Node)newValue);
				return;
			case NodePackage.NODE__VAL_NODES:
				getValNodes().clear();
				getValNodes().addAll((Collection<? extends Node>)newValue);
				return;
			case NodePackage.NODE__REF_NODES:
				getRefNodes().clear();
				getRefNodes().addAll((Collection<? extends Node>)newValue);
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
			case NodePackage.NODE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case NodePackage.NODE__VALUES:
				getValues().clear();
				return;
			case NodePackage.NODE__REF_NODE:
				setRefNode((Node)null);
				return;
			case NodePackage.NODE__VAL_NODE:
				setValNode((Node)null);
				return;
			case NodePackage.NODE__VAL_NODES:
				getValNodes().clear();
				return;
			case NodePackage.NODE__REF_NODES:
				getRefNodes().clear();
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
			case NodePackage.NODE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case NodePackage.NODE__VALUES:
				return values != null && !values.isEmpty();
			case NodePackage.NODE__REF_NODE:
				return refNode != null;
			case NodePackage.NODE__VAL_NODE:
				return valNode != null;
			case NodePackage.NODE__VAL_NODES:
				return valNodes != null && !valNodes.isEmpty();
			case NodePackage.NODE__REF_NODES:
				return refNodes != null && !refNodes.isEmpty();
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
		result.append(" (name: ");
		result.append(name);
		result.append(", values: ");
		result.append(values);
		result.append(')');
		return result.toString();
	}

} //NodeImpl
