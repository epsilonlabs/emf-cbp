/**
 */
package CBP;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Create</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link CBP.Create#getEclass <em>Eclass</em>}</li>
 *   <li>{@link CBP.Create#getEpackage <em>Epackage</em>}</li>
 *   <li>{@link CBP.Create#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see CBP.CBPPackage#getCreate()
 * @model
 * @generated
 */
public interface Create extends EObject {
	/**
	 * Returns the value of the '<em><b>Eclass</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Eclass</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Eclass</em>' attribute.
	 * @see #setEclass(String)
	 * @see CBP.CBPPackage#getCreate_Eclass()
	 * @model
	 * @generated
	 */
	String getEclass();

	/**
	 * Sets the value of the '{@link CBP.Create#getEclass <em>Eclass</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Eclass</em>' attribute.
	 * @see #getEclass()
	 * @generated
	 */
	void setEclass(String value);

	/**
	 * Returns the value of the '<em><b>Epackage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Epackage</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Epackage</em>' attribute.
	 * @see #setEpackage(String)
	 * @see CBP.CBPPackage#getCreate_Epackage()
	 * @model
	 * @generated
	 */
	String getEpackage();

	/**
	 * Sets the value of the '{@link CBP.Create#getEpackage <em>Epackage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Epackage</em>' attribute.
	 * @see #getEpackage()
	 * @generated
	 */
	void setEpackage(String value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see CBP.CBPPackage#getCreate_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link CBP.Create#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // Create
