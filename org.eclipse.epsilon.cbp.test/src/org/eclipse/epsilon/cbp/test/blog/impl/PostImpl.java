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
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.epsilon.cbp.test.blog.Author;
import org.eclipse.epsilon.cbp.test.blog.BlogPackage;
import org.eclipse.epsilon.cbp.test.blog.Comment;
import org.eclipse.epsilon.cbp.test.blog.Post;
import org.eclipse.epsilon.cbp.test.blog.PostType;
import org.eclipse.epsilon.cbp.test.blog.Stats;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Post</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getSubtitle <em>Subtitle</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getRatings <em>Ratings</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getStats <em>Stats</em>}</li>
 *   <li>{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl#getType <em>Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PostImpl extends EObjectImpl implements Post {
	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSubtitle() <em>Subtitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubtitle()
	 * @generated
	 * @ordered
	 */
	protected static final String SUBTITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSubtitle() <em>Subtitle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubtitle()
	 * @generated
	 * @ordered
	 */
	protected String subtitle = SUBTITLE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTags() <em>Tags</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTags()
	 * @generated
	 * @ordered
	 */
	protected EList<String> tags;

	/**
	 * The cached value of the '{@link #getRatings() <em>Ratings</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRatings()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> ratings;

	/**
	 * The cached value of the '{@link #getComments() <em>Comments</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComments()
	 * @generated
	 * @ordered
	 */
	protected EList<Comment> comments;

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
	 * The cached value of the '{@link #getStats() <em>Stats</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStats()
	 * @generated
	 * @ordered
	 */
	protected Stats stats;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final PostType TYPE_EDEFAULT = PostType.REGULAR;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected PostType type = TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PostImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return BlogPackage.Literals.POST;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.POST__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSubtitle() {
		return subtitle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubtitle(String newSubtitle) {
		String oldSubtitle = subtitle;
		subtitle = newSubtitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.POST__SUBTITLE, oldSubtitle, subtitle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getTags() {
		if (tags == null) {
			tags = new EDataTypeUniqueEList<String>(String.class, this, BlogPackage.POST__TAGS);
		}
		return tags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getRatings() {
		if (ratings == null) {
			ratings = new EDataTypeUniqueEList<Integer>(Integer.class, this, BlogPackage.POST__RATINGS);
		}
		return ratings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Comment> getComments() {
		if (comments == null) {
			comments = new EObjectContainmentEList<Comment>(Comment.class, this, BlogPackage.POST__COMMENTS);
		}
		return comments;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, BlogPackage.POST__AUTHOR, oldAuthor, author));
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
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.POST__AUTHOR, oldAuthor, author));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Stats getStats() {
		return stats;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetStats(Stats newStats, NotificationChain msgs) {
		Stats oldStats = stats;
		stats = newStats;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BlogPackage.POST__STATS, oldStats, newStats);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStats(Stats newStats) {
		if (newStats != stats) {
			NotificationChain msgs = null;
			if (stats != null)
				msgs = ((InternalEObject)stats).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BlogPackage.POST__STATS, null, msgs);
			if (newStats != null)
				msgs = ((InternalEObject)newStats).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BlogPackage.POST__STATS, null, msgs);
			msgs = basicSetStats(newStats, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.POST__STATS, newStats, newStats));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PostType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(PostType newType) {
		PostType oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, BlogPackage.POST__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case BlogPackage.POST__COMMENTS:
				return ((InternalEList<?>)getComments()).basicRemove(otherEnd, msgs);
			case BlogPackage.POST__STATS:
				return basicSetStats(null, msgs);
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
			case BlogPackage.POST__TITLE:
				return getTitle();
			case BlogPackage.POST__SUBTITLE:
				return getSubtitle();
			case BlogPackage.POST__TAGS:
				return getTags();
			case BlogPackage.POST__RATINGS:
				return getRatings();
			case BlogPackage.POST__COMMENTS:
				return getComments();
			case BlogPackage.POST__AUTHOR:
				if (resolve) return getAuthor();
				return basicGetAuthor();
			case BlogPackage.POST__STATS:
				return getStats();
			case BlogPackage.POST__TYPE:
				return getType();
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
			case BlogPackage.POST__TITLE:
				setTitle((String)newValue);
				return;
			case BlogPackage.POST__SUBTITLE:
				setSubtitle((String)newValue);
				return;
			case BlogPackage.POST__TAGS:
				getTags().clear();
				getTags().addAll((Collection<? extends String>)newValue);
				return;
			case BlogPackage.POST__RATINGS:
				getRatings().clear();
				getRatings().addAll((Collection<? extends Integer>)newValue);
				return;
			case BlogPackage.POST__COMMENTS:
				getComments().clear();
				getComments().addAll((Collection<? extends Comment>)newValue);
				return;
			case BlogPackage.POST__AUTHOR:
				setAuthor((Author)newValue);
				return;
			case BlogPackage.POST__STATS:
				setStats((Stats)newValue);
				return;
			case BlogPackage.POST__TYPE:
				setType((PostType)newValue);
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
			case BlogPackage.POST__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case BlogPackage.POST__SUBTITLE:
				setSubtitle(SUBTITLE_EDEFAULT);
				return;
			case BlogPackage.POST__TAGS:
				getTags().clear();
				return;
			case BlogPackage.POST__RATINGS:
				getRatings().clear();
				return;
			case BlogPackage.POST__COMMENTS:
				getComments().clear();
				return;
			case BlogPackage.POST__AUTHOR:
				setAuthor((Author)null);
				return;
			case BlogPackage.POST__STATS:
				setStats((Stats)null);
				return;
			case BlogPackage.POST__TYPE:
				setType(TYPE_EDEFAULT);
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
			case BlogPackage.POST__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case BlogPackage.POST__SUBTITLE:
				return SUBTITLE_EDEFAULT == null ? subtitle != null : !SUBTITLE_EDEFAULT.equals(subtitle);
			case BlogPackage.POST__TAGS:
				return tags != null && !tags.isEmpty();
			case BlogPackage.POST__RATINGS:
				return ratings != null && !ratings.isEmpty();
			case BlogPackage.POST__COMMENTS:
				return comments != null && !comments.isEmpty();
			case BlogPackage.POST__AUTHOR:
				return author != null;
			case BlogPackage.POST__STATS:
				return stats != null;
			case BlogPackage.POST__TYPE:
				return type != TYPE_EDEFAULT;
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
		result.append(" (title: ");
		result.append(title);
		result.append(", subtitle: ");
		result.append(subtitle);
		result.append(", tags: ");
		result.append(tags);
		result.append(", ratings: ");
		result.append(ratings);
		result.append(", type: ");
		result.append(type);
		result.append(')');
		return result.toString();
	}

} //PostImpl
