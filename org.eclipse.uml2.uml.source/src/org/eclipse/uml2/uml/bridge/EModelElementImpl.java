package org.eclipse.uml2.uml.bridge;

/**
 * Copyright (c) 2002-2009 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import fr.inria.atlanmod.neoemf.core.DefaultPersistentEObject;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>EModel
 * Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.ecore.impl.EModelElementImpl#getEAnnotations
 * <em>EAnnotations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated not
 */
public abstract class EModelElementImpl extends DefaultPersistentEObject implements EModelElement {

	private static final String[] ESCAPE = { "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09",
			"%0A", "%0B", "%0C", "%0D", "%0E", "%0F", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18",
			"%19", "%1A", "%1B", "%1C", "%1D", "%1E", "%1F", "%20", null, "%22", "%23", null, "%25", "%26", "%27", null,
			null, null, null, "%2C", null, null, "%2F", null, null, null, null, null, null, null, null, null, null,
			"%3A", null, "%3C", null, "%3E", null, };

	/**
	 * The cached value of the '{@link #getEAnnotations()
	 * <em>EAnnotations</em>}' containment reference list. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @see #getEAnnotations()
	 * @generated
	 * @ordered
	 */
	protected EList<EAnnotation> eAnnotations;

	/**
	 * An extensible set of bit flags.
	 */
	protected int eFlags;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EModelElementImpl() {
		super();
	}

	/**
	 * The bit of {@link #eFlags} that is used to represent being
	 * {@link #freeze() frozen}.
	 */
	protected static final int EFROZEN = 1;

	/**
	 * The last bit used by this class; derived classes may use bit values
	 * higher than this.
	 */
	protected static final int ELAST_EMODEL_ELEMENT_FLAG = EFROZEN;

	protected void freeze() {
		setFrozen();
	}

	protected void freeze(Object eModelElement) {
		if (eModelElement instanceof EModelElementImpl) {
			((EModelElementImpl) eModelElement).freeze();
		}
	}

	protected void setFrozen() {
		eFlags |= EFROZEN;
	}

	protected boolean isFrozen() {
		return (eFlags & EFROZEN) != 0;
	}

	@Override
	protected void eSetDirectResource(Internal resource) {
		assert !isFrozen() : "A frozen model should not be modified";
		super.eSetDirectResource(resource);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return EcorePackage.Literals.EMODEL_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<EAnnotation> getEAnnotations() {
		if (eAnnotations == null) {
			eAnnotations = new EObjectContainmentWithInverseEList<EAnnotation>(EAnnotation.class, this,
					EcorePackage.EMODEL_ELEMENT__EANNOTATIONS, EcorePackage.EANNOTATION__EMODEL_ELEMENT);
		}
		return eAnnotations;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public EAnnotation getEAnnotation(String source) {
		if (eAnnotations != null) {
			if (eAnnotations instanceof BasicEList<?>) {
				int size = eAnnotations.size();
				if (size > 0) {
					EAnnotation[] eAnnotationArray = (EAnnotation[]) ((BasicEList<?>) eAnnotations).data();
					if (source == null) {
						for (int i = 0; i < size; ++i) {
							EAnnotation eAnnotation = eAnnotationArray[i];
							if (eAnnotation.getSource() == null) {
								return eAnnotation;
							}
						}
					} else {
						for (int i = 0; i < size; ++i) {
							EAnnotation eAnnotation = eAnnotationArray[i];
							if (source.equals(eAnnotation.getSource())) {
								return eAnnotation;
							}
						}
					}
				}
			} else {
				if (source == null) {
					for (EAnnotation eAnnotation : eAnnotations) {
						if (eAnnotation.getSource() == null) {
							return eAnnotation;
						}
					}
				} else {
					for (EAnnotation eAnnotation : eAnnotations) {
						if (source.equals(eAnnotation.getSource())) {
							return eAnnotation;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getEAnnotations()).basicAdd(otherEnd, msgs);
		}
		return eDynamicInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
			return ((InternalEList<?>) getEAnnotations()).basicRemove(otherEnd, msgs);
		}
		return eDynamicInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
			return getEAnnotations();
		}
		return eDynamicGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
			getEAnnotations().clear();
			getEAnnotations().addAll((Collection<? extends EAnnotation>) newValue);
			return;
		}
		eDynamicSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
			getEAnnotations().clear();
			return;
		}
		eDynamicUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case EcorePackage.EMODEL_ELEMENT__EANNOTATIONS:
			return eAnnotations != null && !eAnnotations.isEmpty();
		}
		return eDynamicIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
		case EcorePackage.EMODEL_ELEMENT___GET_EANNOTATION__STRING:
			return getEAnnotation((String) arguments.get(0));
		}
		return eDynamicInvoke(operationID, arguments);
	}

	@Override
	public String eURIFragmentSegment(EStructuralFeature eStructuralFeature, EObject eObject) {
		if (eObject instanceof ENamedElement) {
			ENamedElement eNamedElement = (ENamedElement) eObject;
			String name = eNamedElement.getName();
			int count = 0;
			for (Iterator<? extends Object> i = ((InternalEList<EObject>) eContents()).basicIterator(); i.hasNext();) {
				Object otherEObject = i.next();
				if (otherEObject == eObject) {
					break;
				}
				if (otherEObject instanceof ENamedElement) {
					ENamedElement otherENamedElement = (ENamedElement) otherEObject;
					String otherName = otherENamedElement.getName();
					if (name == null ? otherName == null : name.equals(otherName)) {
						++count;
					}
				}
			}
			name = name == null ? "%" : eEncodeValue(name);
			return count > 0 ? name + "." + count : name;
		} else if (eObject instanceof EAnnotation) {
			EAnnotation eAnnotation = (EAnnotation) eObject;
			String source = eAnnotation.getSource();
			int count = 0;
			for (Iterator<? extends Object> i = ((InternalEList<EObject>) eContents()).basicIterator(); i.hasNext();) {
				Object otherEObject = i.next();
				if (otherEObject == eObject) {
					break;
				}
				if (otherEObject instanceof EAnnotation) {
					EAnnotation otherEAnnotation = (EAnnotation) otherEObject;
					String otherSource = otherEAnnotation.getSource();
					if (source == null ? otherSource == null : source.equals(otherSource)) {
						++count;
					}
				}
			}

			StringBuffer result = new StringBuffer(source == null ? 6 : source.length() + 5);
			result.append('%');
			result.append(source == null ? "%" : URI.encodeSegment(source, false));
			result.append('%');
			if (count > 0) {
				result.append('.');
				result.append(count);
			}
			return result.toString();
		}
		return super.eURIFragmentSegment(eStructuralFeature, eObject);
	}

	@Override
	public EObject eObjectForURIFragmentSegment(String uriFragmentSegment) {
		int length = uriFragmentSegment.length();
		if (length > 0) {
			// Is the first character a special character, i.e., something other
			// than '@'?
			//
			char firstCharacter = uriFragmentSegment.charAt(0);
			if (firstCharacter != '@') {
				// Is it the start of a source URI of an annotation?
				//
				if (firstCharacter == '%') {
					// Find the closing '%' and make sure it's not just the
					// opening '%'
					//
					int index = uriFragmentSegment.lastIndexOf("%");
					boolean hasCount = false;
					if (index != 0
							&& (index == length - 1 || (hasCount = uriFragmentSegment.charAt(index + 1) == '.'))) {
						// Decode all encoded characters.
						//
						String encodedSource = uriFragmentSegment.substring(1, index);
						String source = "%".equals(encodedSource) ? null : URI.decode(encodedSource);

						// Check for a count, i.e., a '.' followed by a number.
						//
						int count = 0;
						if (hasCount) {
							try {
								count = Integer.parseInt(uriFragmentSegment.substring(index + 2));
							} catch (NumberFormatException exception) {
								throw new WrappedException(exception);
							}
						}

						// Look for the annotation with the matching source.
						//
						for (Object object : eContents()) {
							if (object instanceof EAnnotation) {
								EAnnotation eAnnotation = (EAnnotation) object;
								String otherSource = eAnnotation.getSource();
								if ((source == null ? otherSource == null : source.equals(otherSource))
										&& count-- == 0) {
									return eAnnotation;
								}
							}
						}
						return null;
					}
				}

				// Look for trailing count.
				//
				int index = uriFragmentSegment.lastIndexOf(".");
				String name = index == -1 ? uriFragmentSegment : uriFragmentSegment.substring(0, index);
				int count = 0;
				if (index != -1) {
					try {
						count = Integer.parseInt(uriFragmentSegment.substring(index + 1));
					} catch (NumberFormatException exception) {
						// Interpret it as part of the name.
						//
						name = uriFragmentSegment;
					}
				}

				name = "%".equals(name) ? null : URI.decode(name);

				// Look for a matching named element.
				//
				for (Object object : eContents()) {
					if (object instanceof ENamedElement) {
						ENamedElement eNamedElement = (ENamedElement) object;
						String otherName = eNamedElement.getName();
						if ((name == null ? otherName == null : name.equals(otherName)) && count-- == 0) {
							return eNamedElement;
						}
					}
				}

				return null;
			}
		}

		return super.eObjectForURIFragmentSegment(uriFragmentSegment);
	}

	static String eEncodeValue(String value) {

		int length = value.length();
		StringBuilder result = null;
		for (int i = 0; i < length; ++i) {
			char character = value.charAt(i);
			if (character < ESCAPE.length) {
				String escape = ESCAPE[character];
				if (escape != null) {
					if (result == null) {
						result = new StringBuilder(length + 2);
						result.append(value, 0, i);
					}
					result.append(escape);
					continue;
				}
			}
			if (result != null) {
				result.append(character);
			}
		}
		return result == null ? value : result.toString();
	}

	// CONTAINER ----
	public static class Dynamic extends DefaultPersistentEObject {
		public static final class BasicEMapEntry<K, V> extends Dynamic implements BasicEMap.Entry<K, V> {
			protected int hash = -1;
			protected EStructuralFeature keyFeature;
			protected EStructuralFeature valueFeature;

			/**
			 * Creates a dynamic EObject.
			 */
			public BasicEMapEntry() {
				super();
			}

			/**
			 * Creates a dynamic EObject.
			 */
			public BasicEMapEntry(EClass eClass) {
				super(eClass);
			}

			@SuppressWarnings("unchecked")
			public K getKey() {
				return (K) eGet(keyFeature);
			}

			public void setKey(Object key) {
				eSet(keyFeature, key);
			}

			public int getHash() {
				if (hash == -1) {
					Object theKey = getKey();
					hash = (theKey == null ? 0 : theKey.hashCode());
				}
				return hash;
			}

			public void setHash(int hash) {
				this.hash = hash;
			}

			@SuppressWarnings("unchecked")
			public V getValue() {
				return (V) eGet(valueFeature);
			}

			public V setValue(V value) {
				@SuppressWarnings("unchecked")
				V result = (V) eGet(valueFeature);
				eSet(valueFeature, value);
				return result;
			}

			@Override
			public void eSetClass(EClass eClass) {
				super.eSetClass(eClass);
				keyFeature = eClass.getEStructuralFeature("key");
				valueFeature = eClass.getEStructuralFeature("value");
			}
		}

		protected EClass eClass;
		protected Object[] eSettings;

		public Dynamic() {
			super();
		}

		public Dynamic(EClass eClass) {
			super();
			eSetClass(eClass);
		}

		@Override
		public EClass eClass() {
			return eClass;
		}

		@Override
		protected EClass eDynamicClass() {
			return eClass();
		}

		@Override
		public void eSetClass(EClass eClass) {
			this.eClass = eClass;
		}

		@Override
		protected boolean eHasSettings() {
			return eSettings != null;
		}

		@Override
		protected Object[] eBasicSettings() {
			return eSettings;
		}

		@Override
		protected void eBasicSetSettings(Object[] settings) {
			this.eSettings = settings;
		}
	}

	protected InternalEObject eContainer;

	@Override
	public InternalEObject eInternalContainer() {
		eContainer = super.eInternalContainer();
		return eContainer;
	}

	@Override
	protected void eBasicSetContainer(InternalEObject newContainer) {
//		super.eBasicSetContainer(newContainer);
		eContainer = newContainer;
	}

}
