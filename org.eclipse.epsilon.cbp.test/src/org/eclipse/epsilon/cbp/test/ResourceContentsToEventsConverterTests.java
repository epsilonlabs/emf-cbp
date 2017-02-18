package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.junit.Test;

import university.Department;
import university.Module;
import university.StaffMember;
import university.StaffMemberType;
import university.Student;
import university.University;
import university.UniversityFactory;

public class ResourceContentsToEventsConverterTests {
	
	@Test
	public void countAddEObjectsToResourceEventTest() {

		List<Event> events = getChangelog();

		assertEquals(getAllOfType(events, AddToResourceEvent.class).size(), 7);
	}

	@Test
	public void countSetEAttributeEventTest() {

		List<Event> events = getChangelog();
		assertEquals(getAllOfType(events, SetEAttributeEvent.class).size(), 10);
	}

	@Test
	public void countAddToEAttributeEventTest() {

		List<Event> events = getChangelog();

		assertEquals(getAllOfType(events, AddToEAttributeEvent.class).size(), 1);
	}

	@Test
	public void countSetEReferenceEventTest() {

		List<Event> events = getChangelog();

		assertEquals(getAllOfType(events, SetEReferenceEvent.class).size(), 2);
	}

	@Test
	public void countAddToEReferenceEventTest() {

		List<Event> events = getChangelog();

		assertEquals(getAllOfType(events, AddToEReferenceEvent.class).size(), 8);
	}

	@Test
	public void countRemoveFromResourceTest() {
		// in the change log there should be 0 remove events
		List<Event> events = getChangelog();
		assertEquals(getAllOfType(events, RemoveFromResourceEvent.class).size(), 0);
	}

	@Test
	public void countRemoveFromEAttributeTest() {
		// in the change log there should be 0 remove events
		List<Event> events = getChangelog();
		assertEquals(getAllOfType(events, RemoveFromEAttributeEvent.class).size(), 0);
	}

	@Test
	public void countRemoveFromEReferenceTest() {
		// in the change log there should be 0 remove events
		List<Event> events = getChangelog();
		assertEquals(getAllOfType(events, RemoveFromEReferenceEvent.class).size(), 0);
	}

	public List<Event> getChangelog() {

		// create resource
		Resource resource = new ResourceImpl();

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource.getContents().add(university);

		// --set university attributes
		university.getCodes().add("UoY");
		university.setName("University of York");

		// --create department and set attribute
		Department cs = factory.createDepartment();
		cs.setName("Computer Science");

		// add department to university
		university.getDepartments().add(cs);

		// --create staff member 1, set attribute
		StaffMember member1 = factory.createStaffMember();
		member1.setFirst_name("John");
		member1.setStaffMemberType(StaffMemberType.ACADEMIC);
		// add member1 to department
		cs.getStaff().add(member1);

		// --create staff member 2, set attributes
		StaffMember member2 = factory.createStaffMember();
		member2.setFirst_name("Dimitris");
		member2.setStaffMemberType(StaffMemberType.ACADEMIC);
		// add member 2 to department
		cs.getStaff().add(member2);

		// --create chancelor, set attributes
		StaffMember chancelor = factory.createStaffMember();
		chancelor.setFirst_name("Carol");
		chancelor.setStaffMemberType(StaffMemberType.OTHER);
		// add chancelor to uni
		university.setChancelor(chancelor);

		// --create student 1, set attributes
		Student s1 = factory.createStudent();
		s1.setId(12345);
		s1.setTutor(member1);
		// add to department
		cs.getStudents().add(s1);

		// --create module, set attibutes
		Module mode = factory.createModule();
		mode.setName("MODE");
		mode.getModuleLecturers().add(member1);
		mode.getEnrolledStudents().add(s1);

		// add to department
		cs.getModules().add(mode);

		// create converter
		ResourceContentsToEventsConverter converter = new ResourceContentsToEventsConverter();

		return converter.convert(resource);
	}
	
	protected List<Event> getAllOfType(List<Event> events, Class<?> clazz) {
		List<Event> result = new ArrayList<Event>();
		for (Event event : events) {
			if (event.getClass() == clazz) {
				result.add(event);
			}
		}
		return result;
	}
	
}
