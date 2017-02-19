/**
 */
package org.eclipse.epsilon.cbp.test.blog.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.epsilon.cbp.test.blog.Author;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.eclipse.epsilon.cbp.test.blog.Comment;
import org.eclipse.epsilon.cbp.test.blog.Flag;
import org.eclipse.epsilon.cbp.test.blog.Member;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Comment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl#getReplies <em>Replies</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl#getLiked <em>Liked</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl#getDisliked <em>Disliked</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl#getFlags <em>Flags</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CommentImpl extends MinimalEObjectImpl.Container implements Comment {
	/**
	 * The default value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected static final String TEXT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected String text = TEXT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAuthor()
	 * @generated
	 * @ordered
	 */
	protected Author author;

	/**
	 * The cached value of the '{@link #getReplies() <em>Replies</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReplies()
	 * @generated
	 * @ordered
	 */
	protected EList<Comment> replies;

	/**
	 * The cached value of the '{@link #getLiked() <em>Liked</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLiked()
	 * @generated
	 * @ordered
	 */
	protected EList<Member> liked;

	/**
	 * The cached value of the '{@link #getDisliked() <em>Disliked</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisliked()
	 * @generated
	 * @ordered
	 */
	protected EList<Member> disliked;

	/**
	 * The cached value of the '{@link #getFlags() <em>Flags</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFlags()
	 * @generated
	 * @ordered
	 */
	protected EList<Flag> flags;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CommentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BlogPackage.Literals.COMMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getText() {
		return text;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setText(String newText) {
		String oldText = text;
		text = newText;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.COMMENT__TEXT, oldText, text));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Author getAuthor() {
		if (author != null && author.eIsProxy()) {
			InternalEObject oldAuthor = (InternalEObject)author;
			author = (Author)eResolveProxy(oldAuthor);
			if (author != oldAuthor) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BlogPackage.COMMENT__AUTHOR, oldAuthor, author));
			}
		}
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Author basicGetAuthor() {
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAuthor(Author newAuthor) {
		Author oldAuthor = author;
		author = newAuthor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.COMMENT__AUTHOR, oldAuthor, author));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Comment> getReplies() {
		if (replies == null) {
			replies = new EObjectContainmentEList<Comment>(Comment.class, this, BlogPackage.COMMENT__REPLIES);
		}
		return replies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Member> getLiked() {
		if (liked == null) {
			liked = new EObjectResolvingEList<Member>(Member.class, this, BlogPackage.COMMENT__LIKED);
		}
		return liked;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Member> getDisliked() {
		if (disliked == null) {
			disliked = new EObjectResolvingEList<Member>(Member.class, this, BlogPackage.COMMENT__DISLIKED);
		}
		return disliked;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Flag> getFlags() {
		if (flags == null) {
			flags = new EDataTypeUniqueEList<Flag>(Flag.class, this, BlogPackage.COMMENT__FLAGS);
		}
		return flags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BlogPackage.COMMENT__REPLIES:
				return ((InternalEList<?>)getReplies()).basicRemove(otherEnd, msgs);
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
			case BlogPackage.COMMENT__TEXT:
				return getText();
			case BlogPackage.COMMENT__AUTHOR:
				if (resolve) return getAuthor();
				return basicGetAuthor();
			case BlogPackage.COMMENT__REPLIES:
				return getReplies();
			case BlogPackage.COMMENT__LIKED:
				return getLiked();
			case BlogPackage.COMMENT__DISLIKED:
				return getDisliked();
			case BlogPackage.COMMENT__FLAGS:
				return getFlags();
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
			case BlogPackage.COMMENT__TEXT:
				setText((String)newValue);
				return;
			case BlogPackage.COMMENT__AUTHOR:
				setAuthor((Author)newValue);
				return;
			case BlogPackage.COMMENT__REPLIES:
				getReplies().clear();
				getReplies().addAll((Collection<? extends Comment>)newValue);
				return;
			case BlogPackage.COMMENT__LIKED:
				getLiked().clear();
				getLiked().addAll((Collection<? extends Member>)newValue);
				return;
			case BlogPackage.COMMENT__DISLIKED:
				getDisliked().clear();
				getDisliked().addAll((Collection<? extends Member>)newValue);
				return;
			case BlogPackage.COMMENT__FLAGS:
				getFlags().clear();
				getFlags().addAll((Collection<? extends Flag>)newValue);
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
			case BlogPackage.COMMENT__TEXT:
				setText(TEXT_EDEFAULT);
				return;
			case BlogPackage.COMMENT__AUTHOR:
				setAuthor((Author)null);
				return;
			case BlogPackage.COMMENT__REPLIES:
				getReplies().clear();
				return;
			case BlogPackage.COMMENT__LIKED:
				getLiked().clear();
				return;
			case BlogPackage.COMMENT__DISLIKED:
				getDisliked().clear();
				return;
			case BlogPackage.COMMENT__FLAGS:
				getFlags().clear();
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
			case BlogPackage.COMMENT__TEXT:
				return TEXT_EDEFAULT == null ? text != null : !TEXT_EDEFAULT.equals(text);
			case BlogPackage.COMMENT__AUTHOR:
				return author != null;
			case BlogPackage.COMMENT__REPLIES:
				return replies != null && !replies.isEmpty();
			case BlogPackage.COMMENT__LIKED:
				return liked != null && !liked.isEmpty();
			case BlogPackage.COMMENT__DISLIKED:
				return disliked != null && !disliked.isEmpty();
			case BlogPackage.COMMENT__FLAGS:
				return flags != null && !flags.isEmpty();
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
		result.append(" (text: ");
		result.append(text);
		result.append(", flags: ");
		result.append(flags);
		result.append(')');
		return result.toString();
	}

} //CommentImpl
