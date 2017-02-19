/**
 */
package org.eclipse.epsilon.cbp.test.blog;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Blog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Blog#getPosts <em>Posts</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Blog#getMembers <em>Members</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Blog#getAuthors <em>Authors</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getBlog()
 * @model
 * @generated
 */
public interface Blog extends EObject {
	/**
	 * Returns the value of the '<em><b>Posts</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Post}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Posts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Posts</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getBlog_Posts()
	 * @model containment="true"
	 * @generated
	 */
	EList<Post> getPosts();

	/**
	 * Returns the value of the '<em><b>Members</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Member}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Members</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Members</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getBlog_Members()
	 * @model containment="true"
	 * @generated
	 */
	EList<Member> getMembers();

	/**
	 * Returns the value of the '<em><b>Authors</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Author}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Authors</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Authors</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getBlog_Authors()
	 * @model containment="true"
	 * @generated
	 */
	EList<Author> getAuthors();

} // Blog
