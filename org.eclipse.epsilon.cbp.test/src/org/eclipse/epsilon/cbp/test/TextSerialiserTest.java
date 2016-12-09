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
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.impl.CBPTextResourceImpl;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.PersistenceUtil;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.junit.Test;

import university.Department;
import university.StaffMember;
import university.StaffMemberType;
import university.University;
import university.UniversityFactory;
import university.UniversityPackage;

public class TextSerialiserTest {

	/*
	 * event has the format of:
	 * 0 [(MetaElementTypeID objectID)* ,*]
	 */
	@Test
	public void testAddToResourceEvent() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(2), SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE+" [" + map.getID(university.eClass().getName())+" 0]");
	}
	
	/*
	 * event format:
	 * 6 objectID EAttributeID [value*]
	 */
	@Test
	public void testAddToEAttributeEvent() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		university.getCodes().add("UoY");
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("codes").getName())+" [UoY]");
	}
	
	/*
	 * event format:
	 * 6 objectID EAttributeID [value*]
	 */
	@Test
	public void testAddToEAttributeEventMultiple() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		ArrayList<String> codes = new ArrayList<String>();
		codes.add("UoY");
		codes.add("1234");
		
		university.getCodes().addAll(codes);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("codes").getName())+" [UoY,1234]");
	}
	
	/*
	 * event format:
	 * 3 objectID EAttributeID [value*]
	 */
	@Test
	public void testSetEAttributeEvent() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		university.setName("University of York");
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("name").getName())+" [University of York]");
	}
	
	/*
	 * event format:
	 * 3 objectID EAttributeID [value*]
	 */
	@Test
	public void testSetEAttributeEvent_complex() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();
		
		university.setChancelor(chancelor);
		
		chancelor.setStaffMemberType(StaffMemberType.OTHER);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(4), SerialisationEventType.SET_EATTRIBUTE_COMPLEX + " 1 " + map.getID(chancelor.eClass().getName()+ "-" + chancelor.eClass().getEStructuralFeature("staffMemberType").getName())+" [Other]");
	}
	
	/*
	 * event format:
	 * 11 objectID EReferenceID [(ECLass ID, EObject ID)* ,*]
	 * 12 objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testAddToEReferenceEvent() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		Department department = factory.createDepartment();
		
		university.getDepartments().add(department);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("departments").getName())+" ["+ map.getID(department.eClass().getName())  +" 1]");
	}
	
	/*
	 * event format:
	 * 11 objectID EReferenceID [(ECLass ID, EObject ID)* ,*]
	 * 12 objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testAddToEReferenceEventMultiple() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		Department department1 = factory.createDepartment();
		
		Department department2 = factory.createDepartment();
		
		ArrayList<Department> departments = new ArrayList<Department>();
		
		departments.add(department1);
		departments.add(department2);
		
		university.getDepartments().addAll(departments);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		for(String l: lines)
		{
			System.out.println(l);
		}
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("departments").getName())+" ["+ map.getID(department1.eClass().getName())  +" 1," +  map.getID(department1.eClass().getName()) +" 2]");
	}
	
	/*
	 * event format:
	 * 10 objectID EReferenceID [(ECLass ID, EObject)*(,)*]
	 * 12/9 objectID EReferenceID [EObjectID]
	 */
	@Test
	public void testSetEReferenceEvent() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		StaffMember member1 = factory.createStaffMember();
		
		university.setChancelor(member1);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.CREATE_AND_SET_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("chancelor").getName())+" ["+ map.getID(member1.eClass().getName())  +" 1]");
	}
	
	/*
	 * event type:
	 * 7/8 objectID EAttributeID [value*]
	 */
	@Test
	public void testRemoveFromEAttributeEvent_one() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		university.getCodes().add("UoY");
		
		university.getCodes().clear();
		//university.getCodes().remove("UoY");
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(4), SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("codes").getName())+" [UoY]");
	}
	
	/*
	 * event type:
	 * 7/8 objectID EAttributeID [value*]
	 */
	@Test
	public void testRemoveFromEAttributeEvent_two() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		university.getCodes().add("UoY");
		university.getCodes().add("111");

		
		university.getCodes().clear();
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(5), SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("codes").getName())+" [UoY,111]");
	}
	
	/*
	 * event type:
	 * 7/8 objectID EAttributeID [value*]
	 */
	@Test
	public void testRemoveFromEAttributeEvent_three() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		university.setName("University of York");
		
		university.setName(null);
		//university.eUnset(university.eClass().getEStructuralFeature("name"));
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(4), SerialisationEventType.REMOVE_FROM_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("name").getName())+" [University of York]");
	}
	
	/*
	 * event type:
	 * 13 objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testRemoveFromEReferenceEvent_one() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		Department department = factory.createDepartment();
		
		university.getDepartments().add(department);
		
		university.getDepartments().clear();
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(4), SerialisationEventType.REMOVE_FROM_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("departments").getName())+" [1]");
	}
	
	/*
	 * event type:
	 * 13 objectID EReferenceID [EObjectID*]
	 */
	@Test
	public void testRemoveFromEReferenceEvent_two() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();
		
		university.setChancelor(chancelor);
		
		university.setChancelor(null);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(4), SerialisationEventType.REMOVE_FROM_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("chancelor").getName())+" [1]");
	}
	
	/*
	 * event type:
	 * 2 [EObjectID*]
	 */
	@Test
	public void testRemoveFromResource() {
		
		//create resource
	    CBPResource resource = new CBPTextResourceImpl(URI.createURI(new File("model/test.txt").getAbsolutePath()));
	    
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();

		//add university
		resource.getContents().add(university);

		resource.getContents().remove(university);
		
		CBPTextSerialiser serialiser = new CBPTextSerialiser(resource);

		Map<String, Object> options = new HashMap<String, Object>();
		options.put("ePackage", UniversityPackage.eINSTANCE);
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		
		try {
			serialiser.serialise(options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
//		for(String l: lines)
//		{
//			System.out.println(l);
//		}
		assertEquals(lines.get(3), SerialisationEventType.REMOVE_FROM_RESOURCE + " [0]");
	}
	
	public ArrayList<String> getLines(String path)
	{
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line;
			while((line = reader.readLine())!=null)
			{
				lines.add(line);
			}
			reader.close();
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
