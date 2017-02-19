/**
 */
package org.eclipse.epsilon.cbp.test.blog;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Post</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getRatings <em>Ratings</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getStats <em>Stats</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Post#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost()
 * @model
 * @generated
 */
public interface Post extends EObject {
	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Post#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tags</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' attribute list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Tags()
	 * @model
	 * @generated
	 */
	EList<String> getTags();

	/**
	 * Returns the value of the '<em><b>Ratings</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ratings</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ratings</em>' attribute list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Ratings()
	 * @model
	 * @generated
	 */
	EList<Integer> getRatings();

	/**
	 * Returns the value of the '<em><b>Comments</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Comment}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Comments</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Comments</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Comments()
	 * @model containment="true"
	 * @generated
	 */
	EList<Comment> getComments();

	/**
	 * Returns the value of the '<em><b>Author</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Author</em>' reference.
	 * @see #setAuthor(Author)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Author()
	 * @model
	 * @generated
	 */
	Author getAuthor();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Post#getAuthor <em>Author</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' reference.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(Author value);

	/**
	 * Returns the value of the '<em><b>Stats</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stats</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stats</em>' containment reference.
	 * @see #setStats(Stats)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Stats()
	 * @model containment="true"
	 * @generated
	 */
	Stats getStats();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Post#getStats <em>Stats</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stats</em>' containment reference.
	 * @see #getStats()
	 * @generated
	 */
	void setStats(Stats value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.epsilon.cbp.test.blog.PostType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.epsilon.cbp.test.blog.PostType
	 * @see #setType(PostType)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getPost_Type()
	 * @model
	 * @generated
	 */
	PostType getType();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Post#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see org.eclipse.epsilon.cbp.test.blog.PostType
	 * @see #getType()
	 * @generated
	 */
	void setType(PostType value);

} // Post
