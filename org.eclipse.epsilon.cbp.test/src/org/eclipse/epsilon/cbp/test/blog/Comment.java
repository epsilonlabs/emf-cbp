/**
 */
package org.eclipse.epsilon.cbp.test.blog;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Comment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Comment#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Comment#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Comment#getReplies <em>Replies</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Comment#getLiked <em>Liked</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Comment#getDisliked <em>Disliked</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.Comment#getFlags <em>Flags</em>}</li>
 * </ul>
 *
 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment()
 * @model
 * @generated
 */
public interface Comment extends EObject {
	/**
	 * Returns the value of the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Text</em>' attribute.
	 * @see #setText(String)
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment_Text()
	 * @model
	 * @generated
	 */
	String getText();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getText <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Text</em>' attribute.
	 * @see #getText()
	 * @generated
	 */
	void setText(String value);

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
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment_Author()
	 * @model
	 * @generated
	 */
	Author getAuthor();

	/**
	 * Sets the value of the '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getAuthor <em>Author</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' reference.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(Author value);

	/**
	 * Returns the value of the '<em><b>Replies</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Comment}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Replies</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Replies</em>' containment reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment_Replies()
	 * @model containment="true"
	 * @generated
	 */
	EList<Comment> getReplies();

	/**
	 * Returns the value of the '<em><b>Liked</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Member}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Liked</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Liked</em>' reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment_Liked()
	 * @model
	 * @generated
	 */
	EList<Member> getLiked();

	/**
	 * Returns the value of the '<em><b>Disliked</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Member}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Disliked</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Disliked</em>' reference list.
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment_Disliked()
	 * @model
	 * @generated
	 */
	EList<Member> getDisliked();

	/**
	 * Returns the value of the '<em><b>Flags</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.epsilon.cbp.test.blog.Flag}.
	 * The literals are from the enumeration {@link org.eclipse.epsilon.cbp.test.blog.Flag}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Flags</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Flags</em>' attribute list.
	 * @see org.eclipse.epsilon.cbp.test.blog.Flag
	 * @see org.eclipse.epsilon.cbp.test.blog.BlogPackage#getComment_Flags()
	 * @model
	 * @generated
	 */
	EList<Flag> getFlags();

} // Comment
