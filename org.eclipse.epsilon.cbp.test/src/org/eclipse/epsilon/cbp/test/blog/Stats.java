/**
 */
package org.eclipse.epsilon.cbp.test.blog;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Stats</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Stats#getPageloads <em>Pageloads</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Stats#getVisitors <em>Visitors</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getStats()
 * @model
 * @generated
 */
public interface Stats extends EObject {
	/**
	 * Returns the value of the '<em><b>Pageloads</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Pageloads</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Pageloads</em>' attribute.
	 * @see #setPageloads(int)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getStats_Pageloads()
	 * @model
	 * @generated
	 */
	int getPageloads();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Stats#getPageloads <em>Pageloads</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Pageloads</em>' attribute.
	 * @see #getPageloads()
	 * @generated
	 */
	void setPageloads(int value);

	/**
	 * Returns the value of the '<em><b>Visitors</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Visitors</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Visitors</em>' attribute.
	 * @see #setVisitors(int)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getStats_Visitors()
	 * @model
	 * @generated
	 */
	int getVisitors();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Stats#getVisitors <em>Visitors</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Visitors</em>' attribute.
	 * @see #getVisitors()
	 * @generated
	 */
	void setVisitors(int value);

} // Stats
