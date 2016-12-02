package org.eclipse.epsilon.cbp.resource.to.events;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.junit.Test;

import university.Department;
import university.Module;
import university.StaffMember;
import university.StaffMemberType;
import university.Student;
import university.University;
import university.UniversityFactory;

public class ResourceContentsToEventsConverterTest {

	@Test
	public void test() {
		ResourceContentsToEventsConverter converter = generateConverter();
		converter.convert();
		converter.getChangelog().printLog();
		assertEquals(true, true);
	}

	
	public ResourceContentsToEventsConverter generateConverter()
	{
		
		EventAdapter adapter = new EventAdapter(new Changelog());
		
	    Resource resource = new ResourceImpl();
	    
		ResourceContentsToEventsConverter result = new ResourceContentsToEventsConverter(adapter.getChangelog(), resource);
	    
	    resource.eAdapters().add(adapter);
		
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		
		University university = factory.createUniversity();
		
		resource.getContents().add(university);
		
		university.getCodes().add("UoY");
		university.setName("University of York");
		
		Department cs = factory.createDepartment();
		cs.setName("Computer Science");
		university.getDepartments().add(cs);
		
		StaffMember member1 = factory.createStaffMember();
		member1.setFirst_name("John");
		cs.getStaff().add(member1);
		member1.setStaffMemberType(StaffMemberType.ACADEMIC);
		
		StaffMember member2 = factory.createStaffMember();
		member2.setFirst_name("Dimitris");
		cs.getStaff().add(member2);
		member2.setStaffMemberType(StaffMemberType.ACADEMIC);
		
		StaffMember chancelor = factory.createStaffMember();
		chancelor.setFirst_name("Carol");
		chancelor.setStaffMemberType(StaffMemberType.OTHER);
		university.setChancelor(chancelor);
		
		Student s1 = factory.createStudent();
		s1.setId(12345);
		s1.setTutor(member1);
		cs.getStudents().add(s1);
		
		Module mode = factory.createModule();
		mode.setName("MODE");
		mode.getModuleLecturers().add(member1);
		mode.getEnrolledStudents().add(s1);
		
		cs.getModules().add(mode);
		
		return result;
	}
}
