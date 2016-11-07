package org.eclipse.epsilon.cbp.test.workbench;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.epsilon.cbp.event.EventAdapter;
import org.eclipse.epsilon.cbp.util.Changelog;

import university.Department;
import university.StaffMember;
import university.StaffMemberType;
import university.University;
import university.UniversityFactory;
import university.UniversityPackage;

public class TestEventAdapter {

	public static void main(String[] args) {
		EventAdapter adapter = new EventAdapter(new Changelog());
		
		UniversityFactory factory = UniversityFactory.eINSTANCE;
		University university = factory.createUniversity();
		
		university.eAdapters().add(adapter);
		
		university.getCodes().add("UoY");
		
		ArrayList<String> codes = new ArrayList<String>();
		codes.add("a");
		codes.add("b");
		
		university.getCodes().clear();
		university.getCodes().addAll(codes);
		
		
		Department cs = factory.createDepartment();
		university.getDepartments().add(cs);
		
		StaffMember member = factory.createStaffMember();
		cs.getStaff().add(member);
		member.setStaffMemberType(StaffMemberType.ADMIN);
		
		StaffMember chancelor = factory.createStaffMember();
		chancelor.setStaffMemberType(StaffMemberType.OTHER);
		university.setChancelor(chancelor);
		
		EClass university_eClass = (EClass) UniversityPackage.eINSTANCE.getEClassifier("University");
		EReference chancelor_eReference = (EReference) university_eClass.getEStructuralFeature("chancelor");
		university.eUnset(chancelor_eReference);
		
		EAttribute codes_eAttribute = (EAttribute) university_eClass.getEStructuralFeature("codes");
		university.eUnset(codes_eAttribute);
		
		adapter.showLog();
	}
}
