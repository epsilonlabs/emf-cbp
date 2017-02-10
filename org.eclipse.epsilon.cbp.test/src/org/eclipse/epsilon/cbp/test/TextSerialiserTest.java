package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPTextResourceImpl;
import org.eclipse.epsilon.cbp.util.PersistenceUtil;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.junit.Test;

import university.Department;
import university.StaffMember;
import university.StaffMemberType;
import university.University;
import university.UniversityFactory;

public class TextSerialiserTest {

	/*
	 * event has the format of: 0 [(MetaElementTypeID objectID)* ,*]
	 */
	@Test
	public void testAddToResourceEvent() throws Exception{

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);
		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());
		
		assertEquals(output.getLine(1),
				SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE + " " + getID(university.eClass()) + " 0");
	}

	/*
	 * event format: 6 objectID EAttributeID [value*]
	 */
	@Test
	public void testAddToEAttributeEvent() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		university.getCodes().add("UoY");

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());

		assertEquals(output.getLine(2), SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE + " 0 "
				+ getID(university.eClass(), university.eClass().getEStructuralFeature("codes")) + " UoY");
	}

	/*
	 * event format: 6 objectID EAttributeID [value*]
	 */
	@Test
	public void testAddToEAttributeEventMultiple() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		ArrayList<String> codes = new ArrayList<String>();
		codes.add("UoY");
		codes.add("1234");

		university.getCodes().addAll(codes);

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());
		
		assertEquals(output.getLine(2), SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE + " 0 "
				+ getID(university.eClass(), university.eClass().getEStructuralFeature("codes")) + " UoY");
	}

	/*
	 * event format: 3 objectID EAttributeID [value*]
	 */
	@Test
	public void testSetEAttributeEvent() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		university.setName("University of York");

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());

		assertEquals(output.getLine(2),
				SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE + " 0 "
						+ getID(university.eClass(), university.eClass().getEStructuralFeature("name"))
						+ " University of York");
	}

	/*
	 * event format: 3 objectID EAttributeID [value*]
	 */
	@Test
	public void testSetEAttributeEvent_complex() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();

		university.setChancelor(chancelor);

		chancelor.setStaffMemberType(StaffMemberType.OTHER);

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());
		
		assertEquals(output.getLine(3), SerialisationEventType.SET_EATTRIBUTE_COMPLEX + " 1 "
				+ getID(chancelor.eClass(), chancelor.eClass().getEStructuralFeature("staffMemberType")) + " Other");
	}

	/*
	 * event format: 11 objectID EReferenceID [(ECLass ID, EObject ID)* ,*] 12
	 * objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testAddToEReferenceEvent() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		Department department = factory.createDepartment();

		university.getDepartments().add(department);

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());

		assertEquals(output.getLine(2),
				SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE + " 0 "
						+ getID(university.eClass(), university.eClass().getEStructuralFeature("departments")) + " "
						+ getID(department.eClass()) + " 1");
	}

	/*
	 * event format: 11 objectID EReferenceID [(ECLass ID, EObject ID)* ,*] 12
	 * objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testAddToEReferenceEventMultiple() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		Department department1 = factory.createDepartment();

		Department department2 = factory.createDepartment();

		ArrayList<Department> departments = new ArrayList<Department>();

		departments.add(department1);
		departments.add(department2);

		university.getDepartments().addAll(departments);

		
		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());

		assertEquals(output.getLine(2),
				SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE + " 0 "
						+ getID(university.eClass(), university.eClass().getEStructuralFeature("departments")) + " "
						+ getID(department1.eClass()) + " 1");
	}

	/*
	 * event format: 10 objectID EReferenceID [(ECLass ID, EObject)*(,)*] 12/9
	 * objectID EReferenceID [EObjectID]
	 */
	@Test
	public void testSetEReferenceEvent() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		StaffMember member1 = factory.createStaffMember();

		university.setChancelor(member1);

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());
		
		assertEquals(output.getLine(2),
				SerialisationEventType.CREATE_AND_SET_EREFERENCE + " 0 "
						+ getID(university.eClass(), university.eClass().getEStructuralFeature("chancelor")) + " "
						+ getID(member1.eClass()) + " 1");
	}

	/*
	 * event type: 7/8 objectID EAttributeID [value*]
	 */
	@Test
	public void testRemoveFromEAttributeEvent_one() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		university.getCodes().add("UoY");

		university.getCodes().clear();
		// university.getCodes().remove("UoY");

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());

		assertEquals(output.getLine(3), SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + " 0 "
				+ getID(university.eClass(), university.eClass().getEStructuralFeature("codes")) + " UoY");
	}

	/*
	 * event type: 7/8 objectID EAttributeID [value*]
	 */
	@Test
	public void testRemoveFromEAttributeEvent_two() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		university.getCodes().add("UoY");
		university.getCodes().add("111");

		university.getCodes().clear();

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());
		
		assertEquals(output.getLine(4), SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + " 0 "
				+ getID(university.eClass(), university.eClass().getEStructuralFeature("codes")) + " UoY");
	}

	/*
	 * event type: 7/8 objectID EAttributeID [value*]
	 */
	@Test
	public void testRemoveFromEAttributeEvent_three() throws Exception {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		university.setName("University of York");

		university.setName(null);

		StringOutputStream output = new StringOutputStream();
		resource.save(output, getOptions());

		assertEquals(output.getLine(3),
				SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + " 0 "
						+ getID(university.eClass(), university.eClass().getEStructuralFeature("name"))
						+ " University of York");
	}

	/*
	 * event type: 13 objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testRemoveFromEReferenceEvent_one() {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		Department department = factory.createDepartment();

		university.getDepartments().add(department);

		university.getDepartments().clear();
		
		StringOutputStream output = new StringOutputStream();
		try {
			resource.save(output, getOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(output.getLine(3), SerialisationEventType.REMOVE_FROM_EREFERENCE + " 0 "
				+ getID(university.eClass(), university.eClass().getEStructuralFeature("departments")) + " 1");
	}

	/*
	 * event type: 13 objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testRemoveFromEReferenceEvent_two() {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();

		university.setChancelor(chancelor);

		university.setChancelor(null);
		StringOutputStream output = new StringOutputStream();
		
		try {
			resource.save(output, getOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(output.getLine(3), SerialisationEventType.REMOVE_FROM_EREFERENCE + " 0 "
				+ getID(university.eClass(), university.eClass().getEStructuralFeature("chancelor")) + " 1");
	}

	/*
	 * event type: 2 [EObjectID*]
	 */
	@Test
	public void testRemoveFromResource() {

		// create resource
		CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		resource.getContents().remove(university);
		StringOutputStream output = new StringOutputStream();
		try {
			resource.save(output,getOptions());
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(output.getLine(2), SerialisationEventType.REMOVE_FROM_RESOURCE + " 0");
	}

	public int getID(EClass eClass, EStructuralFeature feature) {
		return PersistenceUtil.getInstance().getePackageElementsNamesMap()
				.getID(eClass.getEPackage().getName() + "-" + eClass.getName() + "-" + feature.getName());
	}

	public int getID(EClass eClass) {
		return PersistenceUtil.getInstance().getePackageElementsNamesMap()
				.getID(eClass.getEPackage().getName() + "-" + eClass.getName());
	}

	public Map<String, Object> getOptions() {
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("verbose", false);
		return options;
	}

}
