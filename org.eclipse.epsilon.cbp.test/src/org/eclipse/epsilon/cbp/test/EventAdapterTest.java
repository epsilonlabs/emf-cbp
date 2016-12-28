package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.junit.Test;

import university.Department;
import university.StaffMember;
import university.StaffMemberType;
import university.University;
import university.UniversityFactory;
import university.UniversityPackage;

public class EventAdapterTest {

	@Test
	public void addToResourceTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		Changelog changelog = adapter.getChangelog();
		changelog.printLog();

		assertEquals(changelog.getEventsList().get(1).getEventType(), Event.ADD_EOBJ_TO_RESOURCE);
	}

	@Test
	public void addToReferenceTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		Department cs = factory.createDepartment();
		university.getDepartments().add(cs);

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(2).getEventType(), Event.ADD_TO_EREFERENCE);
	}

	@Test
	public void addToAttributeTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		university.getCodes().add("UoY");

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(2).getEventType(), Event.ADD_TO_EATTRIBUTE);
	}

	@Test
	public void removeFromResourceTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		resource.getContents().remove(university);

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(2).getEventType(), Event.REMOVE_EOBJ_FROM_RESOURCE);
	}

	@Test
	public void removeFromReferenceTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		Department cs = factory.createDepartment();
		university.getDepartments().add(cs);

		EClass eClass_University = (EClass) UniversityPackage.eINSTANCE.getEClassifier("University");
		EReference departments = (EReference) eClass_University.getEStructuralFeature("departments");
		university.eUnset(departments);

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(3).getEventType(), Event.REMOVE_FROM_EREFERENCE);
	}

	@Test
	public void removeFromEAttributeTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		university.getCodes().add("UoY");

		EClass eClass_University = (EClass) UniversityPackage.eINSTANCE.getEClassifier("University");
		EAttribute codes = (EAttribute) eClass_University.getEStructuralFeature("codes");
		university.eUnset(codes);

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(3).getEventType(), Event.REMOVE_FROM_EATTRIBUTE);
	}

	@Test
	public void setEAttributeTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		university.setName("University of York");

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(2).getEventType(), Event.SET_EATTRIBUTE);
	}

	@Test
	public void setEReferenceTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();
		chancelor.setStaffMemberType(StaffMemberType.OTHER);
		university.setChancelor(chancelor);

		Changelog changelog = adapter.getChangelog();

		assertEquals(changelog.getEventsList().get(2).getEventType(), Event.SET_EREFERENCE);
	}
}
