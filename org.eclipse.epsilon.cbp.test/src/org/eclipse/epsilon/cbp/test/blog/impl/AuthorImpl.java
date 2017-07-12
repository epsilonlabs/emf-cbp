/**
 */
package org.eclipse.epsilon.cbp.test.blog.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.epsilon.cbp.test.blog.Author;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Author</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class AuthorImpl extends PersonImpl implements Author {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AuthorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BlogPackage.Literals.AUTHOR;
	}

} //AuthorImpl
