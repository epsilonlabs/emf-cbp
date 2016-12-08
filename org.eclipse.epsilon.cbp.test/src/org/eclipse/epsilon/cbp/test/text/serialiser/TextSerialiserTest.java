package org.eclipse.epsilon.cbp.test.text.serialiser;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.impl.CBPResource;
import org.eclipse.epsilon.cbp.impl.CBPTextResourceImpl;
import org.eclipse.epsilon.cbp.io.CBPTextSerialiser;
import org.eclipse.epsilon.cbp.resource.to.events.ResourceContentsToEventsConverter;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.PersistenceUtil;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.junit.Test;

import university.Department;
import university.Module;
import university.StaffMember;
import university.StaffMemberType;
import university.Student;
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.ADD_TO_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("codes").getName())+" [UoY]");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("name").getName())+" [University of York]");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.CREATE_AND_ADD_TO_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("departments").getName())+" ["+ map.getID(department.eClass().getName())  +" 1]");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> lines = getLines(f.getAbsolutePath());
		
		for(String l: lines)
		{
			System.out.println(l);
		}
		
		ModelElementIDMap map = PersistenceUtil.getInstance().getePackageElementsNamesMap();
		
		assertEquals(lines.get(3), SerialisationEventType.CREATE_AND_SET_EREFERENCE + " 0 " + map.getID(university.eClass().getName()+ "-" + university.eClass().getEStructuralFeature("chancelor").getName())+" ["+ map.getID(member1.eClass().getName())  +" 1]");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public ResourceContentsToEventsConverter generateConverter()
	{
		
		//create adapter
		EventAdapter adapter = new EventAdapter(new Changelog());
		
		//create resource
	    Resource resource = new ResourceImpl();
	    
	    //create converter
		ResourceContentsToEventsConverter converter = new ResourceContentsToEventsConverter(adapter.getChangelog(), resource);
	    
		//add adapter to resource
	    resource.eAdapters().add(adapter);
		
	    //create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		// --create university
		University university = factory.createUniversity();
		
		//add university
		resource.getContents().add(university);
		
		// --set university attributes
		university.getCodes().add("UoY");
		university.setName("University of York");
		
		// --create department and set attribute
		Department cs = factory.createDepartment();
		cs.setName("Computer Science");
		
		//add department to university
		university.getDepartments().add(cs);
		
		// --create staff member 1, set attribute
		StaffMember member1 = factory.createStaffMember();
		member1.setFirst_name("John");
		member1.setStaffMemberType(StaffMemberType.ACADEMIC);
		//add member1 to department
		cs.getStaff().add(member1);
		
		// --create staff member 2, set attributes
		StaffMember member2 = factory.createStaffMember();
		member2.setFirst_name("Dimitris");
		member2.setStaffMemberType(StaffMemberType.ACADEMIC);
		//add member 2 to department
		cs.getStaff().add(member2);
		
		// --create chancelor, set attributes
		StaffMember chancelor = factory.createStaffMember();
		chancelor.setFirst_name("Carol");
		chancelor.setStaffMemberType(StaffMemberType.OTHER);
		//add chancelor to uni
		university.setChancelor(chancelor);
		
		// --create student 1, set attributes
		Student s1 = factory.createStudent();
		s1.setId(12345);
		s1.setTutor(member1);
		//add to department
		cs.getStudents().add(s1);
		
		// --create module, set attibutes
		Module mode = factory.createModule();
		mode.setName("MODE");
		mode.getModuleLecturers().add(member1);
		mode.getEnrolledStudents().add(s1);
		
		//add to department
		cs.getModules().add(mode);
		
		return converter;
	}
}
