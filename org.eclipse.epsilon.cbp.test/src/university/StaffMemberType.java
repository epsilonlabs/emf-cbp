/**
 */
package university;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Staff Member Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see university.UniversityPackage#getStaffMemberType()
 * @model
 * @generated
 */
public enum StaffMemberType implements Enumerator {
	/**
	 * The '<em><b>Academic</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ACADEMIC_VALUE
	 * @generated
	 * @ordered
	 */
	ACADEMIC(0, "Academic", "Academic"),

	/**
	 * The '<em><b>Research</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_VALUE
	 * @generated
	 * @ordered
	 */
	RESEARCH(1, "Research", "Research"),

	/**
	 * The '<em><b>Technical</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TECHNICAL_VALUE
	 * @generated
	 * @ordered
	 */
	TECHNICAL(2, "Technical", "Technical"),

	/**
	 * The '<em><b>Admin</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ADMIN_VALUE
	 * @generated
	 * @ordered
	 */
	ADMIN(3, "Admin", "Admin"),

	/**
	 * The '<em><b>Honary</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #HONARY_VALUE
	 * @generated
	 * @ordered
	 */
	HONARY(4, "Honary", "Honary"),

	/**
	 * The '<em><b>Research Student</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_STUDENT_VALUE
	 * @generated
	 * @ordered
	 */
	RESEARCH_STUDENT(5, "ResearchStudent", "ResearchStudent"),

	/**
	 * The '<em><b>Other</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #OTHER_VALUE
	 * @generated
	 * @ordered
	 */
	OTHER(6, "Other", "Other");

	/**
	 * The '<em><b>Academic</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Academic</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ACADEMIC
	 * @model name="Academic"
	 * @generated
	 * @ordered
	 */
	public static final int ACADEMIC_VALUE = 0;

	/**
	 * The '<em><b>Research</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Research</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESEARCH
	 * @model name="Research"
	 * @generated
	 * @ordered
	 */
	public static final int RESEARCH_VALUE = 1;

	/**
	 * The '<em><b>Technical</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Technical</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #TECHNICAL
	 * @model name="Technical"
	 * @generated
	 * @ordered
	 */
	public static final int TECHNICAL_VALUE = 2;

	/**
	 * The '<em><b>Admin</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Admin</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ADMIN
	 * @model name="Admin"
	 * @generated
	 * @ordered
	 */
	public static final int ADMIN_VALUE = 3;

	/**
	 * The '<em><b>Honary</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Honary</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #HONARY
	 * @model name="Honary"
	 * @generated
	 * @ordered
	 */
	public static final int HONARY_VALUE = 4;

	/**
	 * The '<em><b>Research Student</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Research Student</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #RESEARCH_STUDENT
	 * @model name="ResearchStudent"
	 * @generated
	 * @ordered
	 */
	public static final int RESEARCH_STUDENT_VALUE = 5;

	/**
	 * The '<em><b>Other</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Other</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #OTHER
	 * @model name="Other"
	 * @generated
	 * @ordered
	 */
	public static final int OTHER_VALUE = 6;

	/**
	 * An array of all the '<em><b>Staff Member Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final StaffMemberType[] VALUES_ARRAY =
		new StaffMemberType[] {
			ACADEMIC,
			RESEARCH,
			TECHNICAL,
			ADMIN,
			HONARY,
			RESEARCH_STUDENT,
			OTHER,
		};

	/**
	 * A public read-only list of all the '<em><b>Staff Member Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<StaffMemberType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Staff Member Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StaffMemberType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StaffMemberType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Staff Member Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StaffMemberType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			StaffMemberType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Staff Member Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static StaffMemberType get(int value) {
		switch (value) {
			case ACADEMIC_VALUE: return ACADEMIC;
			case RESEARCH_VALUE: return RESEARCH;
			case TECHNICAL_VALUE: return TECHNICAL;
			case ADMIN_VALUE: return ADMIN;
			case HONARY_VALUE: return HONARY;
			case RESEARCH_STUDENT_VALUE: return RESEARCH_STUDENT;
			case OTHER_VALUE: return OTHER;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private StaffMemberType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}
	
} //StaffMemberType
