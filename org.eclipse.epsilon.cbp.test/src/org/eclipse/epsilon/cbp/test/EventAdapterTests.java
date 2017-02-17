package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.Event;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.eol.execute.operations.simple.assertions.AssertOperation;
import org.junit.Test;

import university.Department;
import university.StaffMember;
import university.StaffMemberType;
import university.University;
import university.UniversityFactory;
import university.UniversityPackage;

public class EventAdapterTests {

	@Test
	public void addToResourceTest() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		Changelog changelog = adapter.getChangelog();
		
		assertTrue(changelog.getEventsList().get(1) instanceof AddToResourceEvent);
	}

	/*
	@Test
	public void addManyToResourceTest() {
		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University u1 = factory.createUniversity();
		University u2 = factory.createUniversity();
		
		resource.getContents().addAll(Arrays.asList(u1, u2));

		Changelog changelog = adapter.getChangelog();
		
		assertEquals(2, changelog.getEventsList().size());		
	}*/
	
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

		assertTrue(changelog.getEventsList().get(2) instanceof AddToEReferenceEvent);
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

		assertTrue(changelog.getEventsList().get(2) instanceof AddToEAttributeEvent);
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

		assertTrue(changelog.getEventsList().get(2) instanceof RemoveFromResourceEvent);
	}

	@Test
	public void removeFromReferenceTest_zero() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		EClass eClass_University = (EClass) UniversityPackage.eINSTANCE.getEClassifier("University");
		EReference departments = (EReference) eClass_University.getEStructuralFeature("departments");
		university.eUnset(departments);

		Changelog changelog = adapter.getChangelog();
		
		assertEquals(changelog.getEventsList().size(), 2);
	}
	
	@Test
	public void removeFromReferenceTest_one() {

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

		assertTrue(changelog.getEventsList().get(3) instanceof RemoveFromEReferenceEvent);
	}
	
	@Test
	public void removeFromReferenceTest_two() {

		EventAdapter adapter = new EventAdapter(new Changelog());

		Resource resource = new ResourceImpl();

		resource.eAdapters().add(adapter);

		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();

		resource.getContents().add(university);

		Department cs = factory.createDepartment();
		Department cs2 = factory.createDepartment();
		university.getDepartments().add(cs);
		university.getDepartments().add(cs2);

		EClass eClass_University = (EClass) UniversityPackage.eINSTANCE.getEClassifier("University");
		EReference departments = (EReference) eClass_University.getEStructuralFeature("departments");
		university.eUnset(departments);

		Changelog changelog = adapter.getChangelog();

		assertTrue(changelog.getEventsList().get(4) instanceof RemoveFromEReferenceEvent);
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

		assertTrue(changelog.getEventsList().get(3) instanceof RemoveFromEAttributeEvent);
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

		assertTrue(changelog.getEventsList().get(2) instanceof SetEAttributeEvent);
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

		assertTrue(changelog.getEventsList().get(2) instanceof SetEReferenceEvent);
	}
}
