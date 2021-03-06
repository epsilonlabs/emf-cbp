/**
 */
package org.eclipse.epsilon.cbp.comparison.model.node.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.epsilon.cbp.comparison.model.node.Node;
import org.eclipse.epsilon.cbp.comparison.model.node.NodePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getID <em>ID</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getDeep <em>Deep</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getDefName <em>Def Name</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getValues <em>Values</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getListValues <em>List Values</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getRefNode <em>Ref Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getValNode <em>Val Node</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getValNodes <em>Val Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getRefNodes <em>Ref Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getUorderedValNodes <em>Uordered Val Nodes</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.comparison.model.node.impl.NodeImpl#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NodeImpl extends EObjectImpl implements Node {
        /**
         * The default value of the '{@link #getID() <em>ID</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getID()
         * @generated
         * @ordered
         */
        protected static final String ID_EDEFAULT = null;

        /**
         * The cached value of the '{@link #getID() <em>ID</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getID()
         * @generated
         * @ordered
         */
        protected String id = ID_EDEFAULT;

        /**
         * The default value of the '{@link #getDeep() <em>Deep</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getDeep()
         * @generated
         * @ordered
         */
        protected static final Integer DEEP_EDEFAULT = new Integer(0);

        /**
         * The cached value of the '{@link #getDeep() <em>Deep</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getDeep()
         * @generated
         * @ordered
         */
        protected Integer deep = DEEP_EDEFAULT;

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
         * The default value of the '{@link #getDefName() <em>Def Name</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getDefName()
         * @generated
         * @ordered
         */
        protected static final String DEF_NAME_EDEFAULT = "Foo";

        /**
         * The cached value of the '{@link #getDefName() <em>Def Name</em>}' attribute.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getDefName()
         * @generated
         * @ordered
         */
        protected String defName = DEF_NAME_EDEFAULT;

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
         * The cached value of the '{@link #getListValues() <em>List Values</em>}' attribute list.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getListValues()
         * @generated
         * @ordered
         */
        protected EList<Integer> listValues;

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
         * The cached value of the '{@link #getUorderedValNodes() <em>Uordered Val Nodes</em>}' reference list.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getUorderedValNodes()
         * @generated
         * @ordered
         */
        protected EList<Node> uorderedValNodes;

        /**
         * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see #getChildren()
         * @generated
         * @ordered
         */
        protected EList<Node> children;

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
        public String getID() {
                return id;
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public void setID(String newID) {
                String oldID = id;
                id = newID;
                if (eNotificationRequired())
                        eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__ID, oldID, id));
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public Integer getDeep() {
                return deep;
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public void setDeep(Integer newDeep) {
                Integer oldDeep = deep;
                deep = newDeep;
                if (eNotificationRequired())
                        eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__DEEP, oldDeep, deep));
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
        public String getDefName() {
                return defName;
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public void setDefName(String newDefName) {
                String oldDefName = defName;
                defName = newDefName;
                if (eNotificationRequired())
                        eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__DEF_NAME, oldDefName, defName));
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
        public EList<Integer> getListValues() {
                if (listValues == null) {
                        listValues = new EDataTypeEList<Integer>(Integer.class, this, NodePackage.NODE__LIST_VALUES);
                }
                return listValues;
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
        public EList<Node> getUorderedValNodes() {
                if (uorderedValNodes == null) {
                        uorderedValNodes = new EObjectResolvingEList<Node>(Node.class, this, NodePackage.NODE__UORDERED_VAL_NODES);
                }
                return uorderedValNodes;
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public EList<Node> getChildren() {
                if (children == null) {
                        children = new EObjectContainmentWithInverseEList<Node>(Node.class, this, NodePackage.NODE__CHILDREN, NodePackage.NODE__PARENT);
                }
                return children;
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public Node getParent() {
                if (eContainerFeatureID() != NodePackage.NODE__PARENT) return null;
                return (Node)eInternalContainer();
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public NotificationChain basicSetParent(Node newParent, NotificationChain msgs) {
                msgs = eBasicSetContainer((InternalEObject)newParent, NodePackage.NODE__PARENT, msgs);
                return msgs;
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        public void setParent(Node newParent) {
                if (newParent != eInternalContainer() || (eContainerFeatureID() != NodePackage.NODE__PARENT && newParent != null)) {
                        if (EcoreUtil.isAncestor(this, newParent))
                                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
                        NotificationChain msgs = null;
                        if (eInternalContainer() != null)
                                msgs = eBasicRemoveFromContainer(msgs);
                        if (newParent != null)
                                msgs = ((InternalEObject)newParent).eInverseAdd(this, NodePackage.NODE__CHILDREN, Node.class, msgs);
                        msgs = basicSetParent(newParent, msgs);
                        if (msgs != null) msgs.dispatch();
                }
                else if (eNotificationRequired())
                        eNotify(new ENotificationImpl(this, Notification.SET, NodePackage.NODE__PARENT, newParent, newParent));
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        @SuppressWarnings("unchecked")
        @Override
        public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
                switch (featureID) {
                        case NodePackage.NODE__CHILDREN:
                                return ((InternalEList<InternalEObject>)(InternalEList<?>)getChildren()).basicAdd(otherEnd, msgs);
                        case NodePackage.NODE__PARENT:
                                if (eInternalContainer() != null)
                                        msgs = eBasicRemoveFromContainer(msgs);
                                return basicSetParent((Node)otherEnd, msgs);
                }
                return super.eInverseAdd(otherEnd, featureID, msgs);
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
                        case NodePackage.NODE__CHILDREN:
                                return ((InternalEList<?>)getChildren()).basicRemove(otherEnd, msgs);
                        case NodePackage.NODE__PARENT:
                                return basicSetParent(null, msgs);
                }
                return super.eInverseRemove(otherEnd, featureID, msgs);
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        @Override
        public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
                switch (eContainerFeatureID()) {
                        case NodePackage.NODE__PARENT:
                                return eInternalContainer().eInverseRemove(this, NodePackage.NODE__CHILDREN, Node.class, msgs);
                }
                return super.eBasicRemoveFromContainerFeature(msgs);
        }

        /**
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        @Override
        public Object eGet(int featureID, boolean resolve, boolean coreType) {
                switch (featureID) {
                        case NodePackage.NODE__ID:
                                return getID();
                        case NodePackage.NODE__DEEP:
                                return getDeep();
                        case NodePackage.NODE__NAME:
                                return getName();
                        case NodePackage.NODE__DEF_NAME:
                                return getDefName();
                        case NodePackage.NODE__VALUES:
                                return getValues();
                        case NodePackage.NODE__LIST_VALUES:
                                return getListValues();
                        case NodePackage.NODE__REF_NODE:
                                if (resolve) return getRefNode();
                                return basicGetRefNode();
                        case NodePackage.NODE__VAL_NODE:
                                return getValNode();
                        case NodePackage.NODE__VAL_NODES:
                                return getValNodes();
                        case NodePackage.NODE__REF_NODES:
                                return getRefNodes();
                        case NodePackage.NODE__UORDERED_VAL_NODES:
                                return getUorderedValNodes();
                        case NodePackage.NODE__CHILDREN:
                                return getChildren();
                        case NodePackage.NODE__PARENT:
                                return getParent();
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
                        case NodePackage.NODE__ID:
                                setID((String)newValue);
                                return;
                        case NodePackage.NODE__DEEP:
                                setDeep((Integer)newValue);
                                return;
                        case NodePackage.NODE__NAME:
                                setName((String)newValue);
                                return;
                        case NodePackage.NODE__DEF_NAME:
                                setDefName((String)newValue);
                                return;
                        case NodePackage.NODE__VALUES:
                                getValues().clear();
                                getValues().addAll((Collection<? extends Integer>)newValue);
                                return;
                        case NodePackage.NODE__LIST_VALUES:
                                getListValues().clear();
                                getListValues().addAll((Collection<? extends Integer>)newValue);
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
                        case NodePackage.NODE__UORDERED_VAL_NODES:
                                getUorderedValNodes().clear();
                                getUorderedValNodes().addAll((Collection<? extends Node>)newValue);
                                return;
                        case NodePackage.NODE__CHILDREN:
                                getChildren().clear();
                                getChildren().addAll((Collection<? extends Node>)newValue);
                                return;
                        case NodePackage.NODE__PARENT:
                                setParent((Node)newValue);
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
                        case NodePackage.NODE__ID:
                                setID(ID_EDEFAULT);
                                return;
                        case NodePackage.NODE__DEEP:
                                setDeep(DEEP_EDEFAULT);
                                return;
                        case NodePackage.NODE__NAME:
                                setName(NAME_EDEFAULT);
                                return;
                        case NodePackage.NODE__DEF_NAME:
                                setDefName(DEF_NAME_EDEFAULT);
                                return;
                        case NodePackage.NODE__VALUES:
                                getValues().clear();
                                return;
                        case NodePackage.NODE__LIST_VALUES:
                                getListValues().clear();
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
                        case NodePackage.NODE__UORDERED_VAL_NODES:
                                getUorderedValNodes().clear();
                                return;
                        case NodePackage.NODE__CHILDREN:
                                getChildren().clear();
                                return;
                        case NodePackage.NODE__PARENT:
                                setParent((Node)null);
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
                        case NodePackage.NODE__ID:
                                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
                        case NodePackage.NODE__DEEP:
                                return DEEP_EDEFAULT == null ? deep != null : !DEEP_EDEFAULT.equals(deep);
                        case NodePackage.NODE__NAME:
                                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
                        case NodePackage.NODE__DEF_NAME:
                                return DEF_NAME_EDEFAULT == null ? defName != null : !DEF_NAME_EDEFAULT.equals(defName);
                        case NodePackage.NODE__VALUES:
                                return values != null && !values.isEmpty();
                        case NodePackage.NODE__LIST_VALUES:
                                return listValues != null && !listValues.isEmpty();
                        case NodePackage.NODE__REF_NODE:
                                return refNode != null;
                        case NodePackage.NODE__VAL_NODE:
                                return valNode != null;
                        case NodePackage.NODE__VAL_NODES:
                                return valNodes != null && !valNodes.isEmpty();
                        case NodePackage.NODE__REF_NODES:
                                return refNodes != null && !refNodes.isEmpty();
                        case NodePackage.NODE__UORDERED_VAL_NODES:
                                return uorderedValNodes != null && !uorderedValNodes.isEmpty();
                        case NodePackage.NODE__CHILDREN:
                                return children != null && !children.isEmpty();
                        case NodePackage.NODE__PARENT:
                                return getParent() != null;
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
                result.append(" (ID: ");
                result.append(id);
                result.append(", deep: ");
                result.append(deep);
                result.append(", name: ");
                result.append(name);
                result.append(", defName: ");
                result.append(defName);
                result.append(", values: ");
                result.append(values);
                result.append(", listValues: ");
                result.append(listValues);
                result.append(')');
                return result.toString();
        }

} //NodeImpl
