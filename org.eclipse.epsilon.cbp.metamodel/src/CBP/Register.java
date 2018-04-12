/**
 */
package CBP;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Register</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link CBP.Register#getEpackage <em>Epackage</em>}</li>
 * </ul>
 *
 * @see CBP.CBPPackage#getRegister()
 * @model
 * @generated
 */
public interface Register extends EObject {
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
	 * @see CBP.CBPPackage#getRegister_Epackage()
	 * @model
	 * @generated
	 */
	String getEpackage();

	/**
	 * Sets the value of the '{@link CBP.Register#getEpackage <em>Epackage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Epackage</em>' attribute.
	 * @see #getEpackage()
	 * @generated
	 */
	void setEpackage(String value);

} // Register
