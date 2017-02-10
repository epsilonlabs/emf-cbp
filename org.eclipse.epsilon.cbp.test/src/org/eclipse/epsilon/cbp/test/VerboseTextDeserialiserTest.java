package org.eclipse.epsilon.cbp.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryImpl;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.utils.ReferenceUtil;
import org.eclipse.emf.compare.utils.UseIdentifiers;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.CBPTextResourceImpl;
import org.junit.Test;

import university.Department;
import university.StaffMember;
import university.StaffMemberType;
import university.Student;
import university.University;
import university.UniversityFactory;

public class VerboseTextDeserialiserTest {
	private boolean ignoreWhitespace = false, ignoreUnorderedMoves = true;
	private Set<String> ignoreAttributes = Collections.emptySet();

	private final class OptionBasedDiffBuilder extends DiffBuilder {
		@Override
		public void attributeChange(Match match, EAttribute attribute, Object value, DifferenceKind kind,
				DifferenceSource source) {
			if ((ignoreWhitespace || !ignoreAttributes.isEmpty()) && kind == DifferenceKind.CHANGE
					&& source == DifferenceSource.LEFT) {
				final Object o1 = ReferenceUtil.safeEGet(match.getLeft(), attribute);
				final Object o2 = ReferenceUtil.safeEGet(match.getRight(), attribute);

				if (o1 != null && o2 != null) {
					// attribute is set on both sides

					// should it be ignored as long as it is set on both sides?
					if (!ignoreAttributes.isEmpty()) {
						final String attrQualifiedName = getQualifiedName(attribute);
						if (ignoreAttributes.contains(attrQualifiedName)) {
							return;
						}
					}

					if (ignoreWhitespace && o1 instanceof String && o2 instanceof String) {
						// same after stripping whitespace?
						final String s1 = ((String) o1).replaceAll("\\s+", "");
						final String s2 = ((String) o2).replaceAll("\\s+", "");
						if (s1.equals(s2)) {
							return;
						}
					}

				}
			}

			super.attributeChange(match, attribute, value, kind, source);
		}

		@Override
		public void referenceChange(Match match, EReference reference, EObject value, DifferenceKind kind,
				DifferenceSource source) {

			// ignore MOVE changes for unordered references
			if (ignoreUnorderedMoves && !reference.isOrdered() && kind == DifferenceKind.MOVE) {
				return;
			}

			super.referenceChange(match, reference, value, kind, source);
		}

		private String getQualifiedName(ENamedElement elem) {
			if (elem.eContainer() instanceof ENamedElement) {
				return getQualifiedName((ENamedElement) elem.eContainer()) + "." + elem.getName();
			} else {
				return elem.getName();
			}
		}
	}

	@Test
	public void testCreateAndAddToResource() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testRemoveFromResource() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		resource1.getContents().remove(university);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());

		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testSetAttributePrimitive() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		university.setName("University of York");

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testSetAttributeComplex() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();

		university.setChancelor(chancelor);

		chancelor.setStaffMemberType(StaffMemberType.OTHER);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testAddToEAttributePrimitive() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		university.getCodes().add("UOY");

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());

		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testRemoveFromEAttributePrimitive() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		university.getCodes().add("UOY");

		university.getCodes().clear();

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	// need discussion
	@Test
	public void testRemoveFromEAttributeComplex() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		StaffMember chancelor = factory.createStaffMember();

		university.setChancelor(chancelor);

		chancelor.setStaffMemberType(StaffMemberType.OTHER);

		chancelor.setStaffMemberType(null);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());

		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testSetEReference() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		Department cs = factory.createDepartment();

		university.getDepartments().add(cs);

		StaffMember lecturer1 = factory.createStaffMember();

		cs.getStaff().add(lecturer1);

		Student stu1 = factory.createStudent();

		cs.getStudents().add(stu1);

		stu1.setTutor(lecturer1);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testCreateAndSetEReference() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		Department cs = factory.createDepartment();

		university.getDepartments().add(cs);

		StaffMember lecturer1 = factory.createStaffMember();

		cs.getStaff().add(lecturer1);

		Student stu1 = factory.createStudent();

		cs.getStudents().add(stu1);

		stu1.setTutor(lecturer1);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testAddToEReference() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		Department cs = factory.createDepartment();

		university.getDepartments().add(cs);

		StaffMember lecturer1 = factory.createStaffMember();

		cs.getStaff().add(lecturer1);

		Student stu1 = factory.createStudent();

		cs.getStudents().add(stu1);

		university.Module mode = factory.createModule();

		cs.getModules().add(mode);

		lecturer1.getTaughtModules().add(mode);

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testRemoveFromEReference_one() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		Department cs = factory.createDepartment();

		university.getDepartments().add(cs);

		university.getDepartments().clear();

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	@Test
	public void testRemoveFromEReference_two() throws Exception {

		// create resource
		CBPResource resource1 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		// create factory
		UniversityFactory factory = UniversityFactory.eINSTANCE;

		// --create university
		University university = factory.createUniversity();

		// add university
		resource1.getContents().add(university);

		StaffMember member = factory.createStaffMember();

		university.setChancelor(member);

		university.setChancelor(null);

		//resource1.getChangelog().printLog();

		CBPResource resource2 = new CBPTextResourceImpl(
				URI.createFileURI(new File("model/test.txt").getAbsolutePath()));

		StringOutputStream output = new StringOutputStream();
		resource1.save(output, getOptions());
		resource2.load(output.getInputStream(), getOptions());
		
		assertTrue(compare(resource1, resource2));		
	}

	public boolean compare(Resource r1, Resource r2) {
		ResourceSet resourceSet1 = new ResourceSetImpl();
		ResourceSet resourceSet2 = new ResourceSetImpl();

		resourceSet1.getResources().add(r1);
		resourceSet2.getResources().add(r2);

		final IMatchEngine.Factory matchEngineFactory = new MatchEngineFactoryImpl(UseIdentifiers.NEVER);
		final IMatchEngine.Factory.Registry matchEngineRegistry = new MatchEngineFactoryRegistryImpl();
		matchEngineRegistry.add(matchEngineFactory);
		final EMFCompare emfCompare = EMFCompare.builder().setMatchEngineFactoryRegistry(matchEngineRegistry)
				.setDiffEngine(new DefaultDiffEngine(new OptionBasedDiffBuilder())).build();

		final IComparisonScope scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
		final Comparison cmp = emfCompare.compare(scope);

		return cmp.getDifferences().isEmpty() ? true : false;
	}

	public Map<String, Object> getOptions() throws Exception {
		Map<String, Object> options = new HashMap<String, Object>();
		File f = new File("model/test.txt");
		options.put("path", f.getAbsolutePath());
		options.put("verbose", true);
		return options;
	}

}
