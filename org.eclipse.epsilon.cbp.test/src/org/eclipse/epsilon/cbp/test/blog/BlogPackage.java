/**
 */
package org.eclipse.epsilon.cbp.test.blog;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.epsilon.cbp.test.blog.BlogFactory
 * @model kind="package"
 * @generated
 */
public interface BlogPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "blog";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "blog";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	BlogPackage eINSTANCE = org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.BlogImpl <em>Blog</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getBlog()
	 * @generated
	 */
	int BLOG = 0;

	/**
	 * The feature id for the '<em><b>Posts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BLOG__POSTS = 0;

	/**
	 * The feature id for the '<em><b>Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BLOG__MEMBERS = 1;

	/**
	 * The feature id for the '<em><b>Authors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BLOG__AUTHORS = 2;

	/**
	 * The number of structural features of the '<em>Blog</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BLOG_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Blog</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BLOG_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl <em>Post</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.PostImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getPost()
	 * @generated
	 */
	int POST = 1;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__TITLE = 0;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__TAGS = 1;

	/**
	 * The feature id for the '<em><b>Ratings</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__RATINGS = 2;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__COMMENTS = 3;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__AUTHOR = 4;

	/**
	 * The feature id for the '<em><b>Stats</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__STATS = 5;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST__TYPE = 6;

	/**
	 * The number of structural features of the '<em>Post</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_FEATURE_COUNT = 7;

	/**
	 * The number of operations of the '<em>Post</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.StatsImpl <em>Stats</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.StatsImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getStats()
	 * @generated
	 */
	int STATS = 2;

	/**
	 * The feature id for the '<em><b>Pageloads</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STATS__PAGELOADS = 0;

	/**
	 * The feature id for the '<em><b>Visitors</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STATS__VISITORS = 1;

	/**
	 * The number of structural features of the '<em>Stats</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STATS_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Stats</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STATS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl <em>Comment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getComment()
	 * @generated
	 */
	int COMMENT = 3;

	/**
	 * The feature id for the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__TEXT = 0;

	/**
	 * The feature id for the '<em><b>Author</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__AUTHOR = 1;

	/**
	 * The feature id for the '<em><b>Replies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__REPLIES = 2;

	/**
	 * The feature id for the '<em><b>Liked</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__LIKED = 3;

	/**
	 * The feature id for the '<em><b>Disliked</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__DISLIKED = 4;

	/**
	 * The feature id for the '<em><b>Flags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT__FLAGS = 5;

	/**
	 * The number of structural features of the '<em>Comment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Comment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMMENT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.PersonImpl <em>Person</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.PersonImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getPerson()
	 * @generated
	 */
	int PERSON = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON__NAME = 0;

	/**
	 * The number of structural features of the '<em>Person</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Person</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSON_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.AuthorImpl <em>Author</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.AuthorImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getAuthor()
	 * @generated
	 */
	int AUTHOR = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUTHOR__NAME = PERSON__NAME;

	/**
	 * The number of structural features of the '<em>Author</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUTHOR_FEATURE_COUNT = PERSON_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Author</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AUTHOR_OPERATION_COUNT = PERSON_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.MemberImpl <em>Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.MemberImpl
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getMember()
	 * @generated
	 */
	int MEMBER = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER__NAME = PERSON__NAME;

	/**
	 * The number of structural features of the '<em>Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_FEATURE_COUNT = PERSON_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MEMBER_OPERATION_COUNT = PERSON_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.PostType <em>Post Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.PostType
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getPostType()
	 * @generated
	 */
	int POST_TYPE = 7;

	/**
	 * The meta object id for the '{@link org.eclipse.epsilon.cbp.test.blog.Flag <em>Flag</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.epsilon.cbp.test.blog.Flag
	 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getFlag()
	 * @generated
	 */
	int FLAG = 8;


	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Blog <em>Blog</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Blog</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Blog
	 * @generated
	 */
	EClass getBlog();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.cbp.test.blog.Blog#getPosts <em>Posts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Posts</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Blog#getPosts()
	 * @see #getBlog()
	 * @generated
	 */
	EReference getBlog_Posts();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.cbp.test.blog.Blog#getMembers <em>Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Members</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Blog#getMembers()
	 * @see #getBlog()
	 * @generated
	 */
	EReference getBlog_Members();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.cbp.test.blog.Blog#getAuthors <em>Authors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Authors</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Blog#getAuthors()
	 * @see #getBlog()
	 * @generated
	 */
	EReference getBlog_Authors();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Post <em>Post</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Post</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post
	 * @generated
	 */
	EClass getPost();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.blog.Post#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getTitle()
	 * @see #getPost()
	 * @generated
	 */
	EAttribute getPost_Title();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.epsilon.cbp.test.blog.Post#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getTags()
	 * @see #getPost()
	 * @generated
	 */
	EAttribute getPost_Tags();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.epsilon.cbp.test.blog.Post#getRatings <em>Ratings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Ratings</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getRatings()
	 * @see #getPost()
	 * @generated
	 */
	EAttribute getPost_Ratings();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.cbp.test.blog.Post#getComments <em>Comments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Comments</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getComments()
	 * @see #getPost()
	 * @generated
	 */
	EReference getPost_Comments();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.epsilon.cbp.test.blog.Post#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Author</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getAuthor()
	 * @see #getPost()
	 * @generated
	 */
	EReference getPost_Author();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.epsilon.cbp.test.blog.Post#getStats <em>Stats</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Stats</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getStats()
	 * @see #getPost()
	 * @generated
	 */
	EReference getPost_Stats();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.blog.Post#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Post#getType()
	 * @see #getPost()
	 * @generated
	 */
	EAttribute getPost_Type();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Stats <em>Stats</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Stats</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Stats
	 * @generated
	 */
	EClass getStats();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.blog.Stats#getPageloads <em>Pageloads</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Pageloads</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Stats#getPageloads()
	 * @see #getStats()
	 * @generated
	 */
	EAttribute getStats_Pageloads();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.blog.Stats#getVisitors <em>Visitors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Visitors</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Stats#getVisitors()
	 * @see #getStats()
	 * @generated
	 */
	EAttribute getStats_Visitors();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Comment <em>Comment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Comment</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment
	 * @generated
	 */
	EClass getComment();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getText <em>Text</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Text</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment#getText()
	 * @see #getComment()
	 * @generated
	 */
	EAttribute getComment_Text();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Author</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment#getAuthor()
	 * @see #getComment()
	 * @generated
	 */
	EReference getComment_Author();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getReplies <em>Replies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Replies</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment#getReplies()
	 * @see #getComment()
	 * @generated
	 */
	EReference getComment_Replies();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getLiked <em>Liked</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Liked</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment#getLiked()
	 * @see #getComment()
	 * @generated
	 */
	EReference getComment_Liked();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getDisliked <em>Disliked</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Disliked</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment#getDisliked()
	 * @see #getComment()
	 * @generated
	 */
	EReference getComment_Disliked();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.epsilon.cbp.test.blog.Comment#getFlags <em>Flags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Flags</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Comment#getFlags()
	 * @see #getComment()
	 * @generated
	 */
	EAttribute getComment_Flags();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Person <em>Person</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Person</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Person
	 * @generated
	 */
	EClass getPerson();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.epsilon.cbp.test.blog.Person#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Person#getName()
	 * @see #getPerson()
	 * @generated
	 */
	EAttribute getPerson_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Author <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Author</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Author
	 * @generated
	 */
	EClass getAuthor();

	/**
	 * Returns the meta object for class '{@link org.eclipse.epsilon.cbp.test.blog.Member <em>Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Member</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Member
	 * @generated
	 */
	EClass getMember();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.epsilon.cbp.test.blog.PostType <em>Post Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Post Type</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.PostType
	 * @generated
	 */
	EEnum getPostType();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.epsilon.cbp.test.blog.Flag <em>Flag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Flag</em>'.
	 * @see org.eclipse.epsilon.cbp.test.blog.Flag
	 * @generated
	 */
	EEnum getFlag();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	BlogFactory getBlogFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.BlogImpl <em>Blog</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getBlog()
		 * @generated
		 */
		EClass BLOG = eINSTANCE.getBlog();

		/**
		 * The meta object literal for the '<em><b>Posts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BLOG__POSTS = eINSTANCE.getBlog_Posts();

		/**
		 * The meta object literal for the '<em><b>Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BLOG__MEMBERS = eINSTANCE.getBlog_Members();

		/**
		 * The meta object literal for the '<em><b>Authors</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BLOG__AUTHORS = eINSTANCE.getBlog_Authors();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.PostImpl <em>Post</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.PostImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getPost()
		 * @generated
		 */
		EClass POST = eINSTANCE.getPost();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute POST__TITLE = eINSTANCE.getPost_Title();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute POST__TAGS = eINSTANCE.getPost_Tags();

		/**
		 * The meta object literal for the '<em><b>Ratings</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute POST__RATINGS = eINSTANCE.getPost_Ratings();

		/**
		 * The meta object literal for the '<em><b>Comments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference POST__COMMENTS = eINSTANCE.getPost_Comments();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference POST__AUTHOR = eINSTANCE.getPost_Author();

		/**
		 * The meta object literal for the '<em><b>Stats</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference POST__STATS = eINSTANCE.getPost_Stats();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute POST__TYPE = eINSTANCE.getPost_Type();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.StatsImpl <em>Stats</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.StatsImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getStats()
		 * @generated
		 */
		EClass STATS = eINSTANCE.getStats();

		/**
		 * The meta object literal for the '<em><b>Pageloads</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STATS__PAGELOADS = eINSTANCE.getStats_Pageloads();

		/**
		 * The meta object literal for the '<em><b>Visitors</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STATS__VISITORS = eINSTANCE.getStats_Visitors();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl <em>Comment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.CommentImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getComment()
		 * @generated
		 */
		EClass COMMENT = eINSTANCE.getComment();

		/**
		 * The meta object literal for the '<em><b>Text</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMMENT__TEXT = eINSTANCE.getComment_Text();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMENT__AUTHOR = eINSTANCE.getComment_Author();

		/**
		 * The meta object literal for the '<em><b>Replies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMENT__REPLIES = eINSTANCE.getComment_Replies();

		/**
		 * The meta object literal for the '<em><b>Liked</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMENT__LIKED = eINSTANCE.getComment_Liked();

		/**
		 * The meta object literal for the '<em><b>Disliked</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMMENT__DISLIKED = eINSTANCE.getComment_Disliked();

		/**
		 * The meta object literal for the '<em><b>Flags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMMENT__FLAGS = eINSTANCE.getComment_Flags();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.PersonImpl <em>Person</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.PersonImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getPerson()
		 * @generated
		 */
		EClass PERSON = eINSTANCE.getPerson();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSON__NAME = eINSTANCE.getPerson_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.AuthorImpl <em>Author</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.AuthorImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getAuthor()
		 * @generated
		 */
		EClass AUTHOR = eINSTANCE.getAuthor();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.impl.MemberImpl <em>Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.MemberImpl
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getMember()
		 * @generated
		 */
		EClass MEMBER = eINSTANCE.getMember();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.PostType <em>Post Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.PostType
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getPostType()
		 * @generated
		 */
		EEnum POST_TYPE = eINSTANCE.getPostType();

		/**
		 * The meta object literal for the '{@link org.eclipse.epsilon.cbp.test.blog.Flag <em>Flag</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.epsilon.cbp.test.blog.Flag
		 * @see org.eclipse.epsilon.cbp.test.blog.impl.BlogPackageImpl#getFlag()
		 * @generated
		 */
		EEnum FLAG = eINSTANCE.getFlag();

	}

} //BlogPackage
